package fi.espoo.vekkuli.boatSpace.invoice

import fi.espoo.vekkuli.common.VekkuliHttpClient
import fi.espoo.vekkuli.config.EspiEnv
import fi.espoo.vekkuli.utils.TimeProvider
import io.ktor.client.call.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.util.*

object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: BigDecimal
    ) {
        encoder.encodeString(value.toPlainString())
    }

    override fun deserialize(decoder: Decoder): BigDecimal = BigDecimal(decoder.decodeString())
}

@Serializable
data class InvoicePaymentResponse(
    val receipts: List<Receipt>
)

@Serializable
data class Receipt(
    val transactionNumber: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val amountPaid: BigDecimal,
    val paymentDate: String,
    val invoiceNumber: String
)

interface InvoicePaymentClient {
    fun getPayments(): InvoicePaymentResponse
}

@Profile("test || local")
@Service
class MockInvoicePaymentClient : InvoicePaymentClient {
    private var payments: List<Receipt> = listOf()

    override fun getPayments(): InvoicePaymentResponse = InvoicePaymentResponse(receipts = payments)

    fun setPayments(payments: List<Receipt>) {
        this.payments = payments
    }
}

@Profile("!(test || local)")
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

            if (response.bodyAsText().isEmpty()) {
                return@runBlocking InvoicePaymentResponse(receipts = listOf())
            }
            return@runBlocking response.body<InvoicePaymentResponse>()
        }
}
