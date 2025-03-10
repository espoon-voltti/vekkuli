package fi.espoo.vekkuli.boatSpace.invoice

import fi.espoo.vekkuli.common.VekkuliHttpClient
import fi.espoo.vekkuli.config.EspiEnv
import fi.espoo.vekkuli.utils.TimeProvider
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.util.*

val logger = KotlinLogging.logger {}

data class InvoicePaymentResponse(
    val receipts: List<Receipt>
)

data class Receipt(
    val transactionNumber: Int,
    val amountPaid: BigDecimal,
    val paymentDate: String,
    val invoiceNumber: String
)

@Serializable
data class RawInvoicePaymentResponse(
    val receipts: List<RawReceipt>
)

@Serializable
data class RawReceipt(
    val transactionNumber: String,
    val amountPaid: String,
    val paymentDate: String,
    val invoiceNumber: String
)

interface InvoicePaymentClient {
    fun getPayments(): InvoicePaymentResponse
}


@Profile("!(staging || production)")
@Service
class MockInvoicePaymentClient : InvoicePaymentClient {
    companion object {
        var payments: List<Receipt> = listOf()

        fun setPayments(payments: List<Receipt>) {
            this.payments = payments
        }
    }
    override fun getPayments(): InvoicePaymentResponse = InvoicePaymentResponse(receipts = payments)
}

@Profile("staging || production")
@Service
class EspiInvoicePaymentClient(
    val espiEnv: EspiEnv,
    val timeProvider: TimeProvider
) : InvoicePaymentClient {
    override fun getPayments(): InvoicePaymentResponse =
        runBlocking {
            val (apiUrl, apiUsername, apiPassword) = espiEnv
            val startDate = timeProvider.getCurrentDateTime().minusDays(1)

            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            val formattedDate = startDate.format(formatter)
            val encodedDate = URLEncoder.encode(formattedDate, StandardCharsets.UTF_8.toString())

            val url = "$apiUrl/invoice/api/v1/receipts?startDate=$encodedDate"
            val authHeader = Base64.getEncoder().encodeToString("$apiUsername:$apiPassword".toByteArray())
            val headers =
                mapOf(
                    "Content-Type" to "application/json",
                    "Authorization" to "Basic $authHeader"
                )
            val response =
                VekkuliHttpClient.makeGetRequest(
                    url,
                    headers,
                )

            val parsedReceipts =
                response.body<RawInvoicePaymentResponse>().receipts.mapNotNull {
                    try {
                        Receipt(
                            transactionNumber = it.transactionNumber.toInt(),
                            amountPaid = BigDecimal(it.amountPaid),
                            paymentDate = it.paymentDate,
                            invoiceNumber = it.invoiceNumber
                        )
                    } catch (e: Exception) {
                        logger.error { "Parsing receipt failed: ${e.message}" }
                        null
                    }
                }

            return@runBlocking InvoicePaymentResponse(
                receipts = parsedReceipts
            )
        }
}
