package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.common.VekkuliHttpClient
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.controllers.Utils.Companion.isStagingOrProduction
import fi.espoo.vekkuli.domain.*
import io.ktor.client.call.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@Serializable
data class PaytrailCustomer(
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
)

@Serializable
data class PaytrailCallbackUrl(
    val success: String,
    val cancel: String
)

@Serializable
data class PaytrailPurchaseItem(
    val unitPrice: Int,
    val units: Int,
    val vatPercentage: Double,
    val productCode: String,
    val description: String? = null,
    val category: String? = null,
    val deliveryDate: LocalDate? = null,
)

@Serializable
data class PaytrailPaymentParams(
    val stamp: String,
    val reference: String,
    val amount: Int,
    val language: String,
    val items: List<PaytrailPurchaseItem>? = null,
    val customer: PaytrailCustomer,
)

@Serializable
data class PaytrailPaymentBody(
    val stamp: String,
    val reference: String,
    val amount: Int,
    val currency: String,
    val language: String,
    val items: List<PaytrailPurchaseItem>? = null,
    val customer: PaytrailCustomer,
    val redirectUrls: PaytrailCallbackUrl,
    val callbackUrls: PaytrailCallbackUrl? = null,
    val callbackDelay: Int? = null,
)

@Serializable
data class NameValuePair(
    val name: String,
    val value: String,
)

@Serializable
data class PaytrailProvider(
    val name: String,
    val url: String,
    val icon: String,
    val svg: String,
    val id: String,
    val group: String,
    val parameters: List<NameValuePair>
)

@Serializable
data class PaytrailPaymentResponse(
    val transactionId: String,
    val reference: String,
    val terms: String,
    val providers: List<PaytrailProvider>
)

const val MERCHANT_SECRET = "SAIPPUAKAUPPIAS"
const val MERCHANT_ID = "375917"

const val BASE_URL = "https://services.paytrail.com"

val redirectUrls =
    PaytrailCallbackUrl(
        getServiceUrl("/kuntalainen/maksut/onnistunut"),
        getServiceUrl("/kuntalainen/maksut/peruuntunut")
    )

val callbackUrls: PaytrailCallbackUrl? =
    if (isStagingOrProduction()) {
        PaytrailCallbackUrl(
            getServiceUrl("/ext/payments/paytrail/success"),
            getServiceUrl("/ext/payments/paytrail/cancel")
        )
    } else {
        null
    }

const val CURRENCY = "EUR"

// HASH_ALGORITHM_NAME and HASH_ALGORITHM must be changed together
const val HASH_ALGORITHM_NAME = "sha512"
val HASH_ALGORITHM = HmacAlgorithms.HMAC_SHA_512

sealed class PaymentProcessResult {
    data class Success(
        val reservation: BoatSpaceReservationDetails
    ) : PaymentProcessResult()

    object Failure : PaymentProcessResult()

    object HandledAlready : PaymentProcessResult()
}

@Service
class Paytrail {
    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var emailService: TemplateEmailService

    @Autowired
    lateinit var messageUtil: MessageUtil

    fun createPayment(params: PaytrailPaymentParams): PaytrailPaymentResponse {
        val nonce = UUID.randomUUID().toString()
        val timestamp = LocalDateTime.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)

        var headers =
            mapOf(
                "checkout-account" to MERCHANT_ID,
                "checkout-algorithm" to HASH_ALGORITHM_NAME,
                "checkout-method" to "POST",
                "checkout-nonce" to nonce,
                "checkout-timestamp" to timestamp,
            )
        val body =
            PaytrailPaymentBody(
                stamp = params.stamp,
                reference = params.reference,
                amount = params.amount,
                currency = CURRENCY,
                language = params.language,
                customer = params.customer,
                redirectUrls = redirectUrls,
                callbackUrls = callbackUrls,
                callbackDelay = 1,
                items = params.items,
            )

        val json =
            Json {
                encodeDefaults = false
            }

        val encodedBody = json.encodeToString(body)
        val sig =
            calculateHmac(
                headers,
                encodedBody,
            )

        // Add calculated signature to headers
        headers = headers + ("signature" to sig)

        return runBlocking {
            VekkuliHttpClient.makePostRequest("$BASE_URL/payments", encodedBody, headers).body()
        }
    }

    fun checkSignature(params: Map<String, String>): Boolean {
        val signature = calculateHmac(params, "")
        return signature == params["signature"]
    }

    private fun calculateHmac(
        params: Map<String, String>,
        body: String
    ): String {
        var items =
            params.entries
                // Take only headers that start with 'checkout' to calculate
                // the signature. Others are ignored for calculation.
                .filter {
                    it.key.startsWith(
                        "checkout-"
                    )
                }
                // Sort headers alphabetically
                .sortedBy { it.key }
                // Combined key and value with colon as a separator
                .map {
                    String.format(
                        "%s:%s",
                        it.key,
                        it.value
                    )
                }

        // Append request body and join all items with new line character as separator
        val data = (items + body).joinToString("\n")
        return computeHash(data)
    }

    private fun computeHash(message: String): String {
        val hmac = HmacUtils(HASH_ALGORITHM, MERCHANT_SECRET)
        val outMsg = hmac.hmacHex(message)
        return outMsg.replace("-", "").lowercase(Locale.getDefault())
    }

    fun handlePaymentResult(
        params: Map<String, String>,
        success: Boolean
    ): PaymentProcessResult {
        if (!checkSignature(params)) {
            return PaymentProcessResult.Failure
        }
        val stamp = UUID.fromString(params.get("checkout-stamp"))

        val payment = jdbi.inTransactionUnchecked { it.getPayment(stamp) }
        if (payment == null) return PaymentProcessResult.Failure

        if (payment.status != PaymentStatus.Created) return PaymentProcessResult.HandledAlready

        jdbi.inTransactionUnchecked {
            it.handleReservationPaymentResult(stamp, PaymentStatus.Success)
        }

        val reservation = jdbi.inTransactionUnchecked { it.getBoatSpaceReservationsWithPaymentId(stamp) }
        if (reservation == null) return PaymentProcessResult.Failure

        emailService.sendEmail(
            "reservationSuccess",
            reservation.email,
            messageUtil.getMessage("boatSpaceReservation.title.confirmation"),
            mapOf(
                "name" to " ${reservation.locationName} ${reservation.place}",
                "width" to reservation.boatSpaceWidthInM,
                "length" to reservation.boatSpaceLengthInM,
                "amenity" to messageUtil.getMessage("boatSpaces.amenityOption.${reservation.amenity}"),
                "endDate" to reservation.endDate
            )
        )

        return PaymentProcessResult.Success(reservation)
    }
}
