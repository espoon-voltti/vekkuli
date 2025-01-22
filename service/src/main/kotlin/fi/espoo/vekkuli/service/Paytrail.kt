package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.common.VekkuliHttpClient
import fi.espoo.vekkuli.config.PaytrailEnv
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.controllers.Utils.Companion.isStagingOrProduction
import fi.espoo.vekkuli.utils.TimeProvider
import io.ktor.client.call.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
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
    val cancel: String,
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
    val redirectUrls: PaytrailCallbackUrl? = null,
    val callbackUrls: PaytrailCallbackUrl? = null,
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
    val methodIsPost: Boolean = true,
    val icon: String,
    val svg: String,
    val id: String,
    val group: String,
    val parameters: List<NameValuePair>,
)

@Serializable
data class PaytrailPaymentResponse(
    val transactionId: String,
    val reference: String,
    val terms: String,
    val providers: List<PaytrailProvider>
)

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

public interface PaytrailInterface {
    fun createPayment(params: PaytrailPaymentParams): PaytrailPaymentResponse

    fun checkSignature(params: Map<String, String>): Boolean
}

@Service
@Profile("test")
class PaytrailMock : PaytrailInterface {
    companion object {
        val paytrailPayments = mutableListOf<PaytrailPaymentParams>()

        fun reset() {
            paytrailPayments.clear()
        }
    }

    override fun createPayment(params: PaytrailPaymentParams): PaytrailPaymentResponse {
        paytrailPayments.add(params)
        return PaytrailPaymentResponse(
            transactionId = "123",
            reference = params.reference,
            terms = "https://www.paytrail.com",
            providers =
                listOf(
                    PaytrailProvider(
                        name = "Nordea success",
                        methodIsPost = false,
                        url = params.redirectUrls?.success ?: "/kuntalainen/maksut/onnistunut",
                        icon = "https://www.nordea.fi/icon.png",
                        svg = "https://www.nordea.fi/icon.svg",
                        id = "nordea-success",
                        group = "bank",
                        parameters =
                            listOf(
                                NameValuePair("checkout-stamp", params.stamp),
                            )
                    ),
                    PaytrailProvider(
                        name = "Nordea failed",
                        methodIsPost = false,
                        url = params.redirectUrls?.cancel ?: "/kuntalainen/maksut/peruuntunut",
                        icon = "https://www.nordea.fi/icon.png",
                        svg = "https://www.nordea.fi/icon.svg",
                        id = "nordea-fail",
                        group = "bank",
                        parameters =
                            listOf(
                                NameValuePair("checkout-stamp", params.stamp),
                            )
                    )
                )
        )
    }

    override fun checkSignature(params: Map<String, String>): Boolean = true
}

@Service
@Profile("!test")
class Paytrail(
    private val paytrailEnv: PaytrailEnv,
    private val timeProvider: TimeProvider
) : PaytrailInterface {
    override fun createPayment(params: PaytrailPaymentParams): PaytrailPaymentResponse {
        val nonce = UUID.randomUUID().toString()
        val timestamp = timeProvider.getCurrentDateTime().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)

        var headers =
            mapOf(
                "checkout-account" to paytrailEnv.merchantId,
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
                redirectUrls = params.redirectUrls ?: redirectUrls,
                callbackUrls = params.callbackUrls ?: callbackUrls,
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

    override fun checkSignature(params: Map<String, String>): Boolean {
        val signature = calculateHmac(params, "")
        return signature == params["signature"]
    }

    private fun calculateHmac(
        params: Map<String, String>,
        body: String,
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
        val hmac = HmacUtils(HASH_ALGORITHM, paytrailEnv.merchantSecret)
        val outMsg = hmac.hmacHex(message)
        return outMsg.replace("-", "").lowercase(Locale.getDefault())
    }
}
