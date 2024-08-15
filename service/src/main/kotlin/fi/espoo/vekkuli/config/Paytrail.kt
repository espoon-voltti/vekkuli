package fi.espoo.vekkuli.config

import fi.espoo.vekkuli.common.VekkuliHttpClient
import io.ktor.client.call.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
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
    val vatPercentage: Int,
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
const val SUCCESS_URL = "http://localhost:3000/maksut/onnistui"
const val CANCEL_URL = "http://localhost:3000/maksut/peruuntui"
const val CURRENCY = "EUR"
const val HASH_ALGORITHM_NAME = "sha512"
val HASH_ALGORITHM =
    when (HASH_ALGORITHM_NAME) {
        "sha256" -> HmacAlgorithms.HMAC_SHA_256
        "sha512" -> HmacAlgorithms.HMAC_SHA_512
        else -> throw IllegalArgumentException("Unsupported hash algorithm: $HASH_ALGORITHM_NAME")
    }

class Paytrail {
    companion object {
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
                    redirectUrls = PaytrailCallbackUrl(SUCCESS_URL, CANCEL_URL),
                    callbackUrls = null,
                    callbackDelay = null,
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
                VekkuliHttpClient.makePostRequest("${BASE_URL}/payments", encodedBody, headers).body()
            }
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
    }
}
