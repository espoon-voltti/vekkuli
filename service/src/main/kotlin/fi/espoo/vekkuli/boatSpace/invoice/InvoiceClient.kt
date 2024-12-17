package fi.espoo.vekkuli.boatSpace.invoice

import fi.espoo.vekkuli.common.VekkuliHttpClient
import fi.espoo.vekkuli.config.EspiEnv
import fi.espoo.vekkuli.utils.TimeProvider
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.*

@Serializable
data class Row(
    val productGroup: String? = null,
    val periodStartDate: String,
    val periodEndDate: String? = null,
    val unitCount: Long? = null,
    val unitPrice: Long? = null,
    val amount: Long,
    val vatAmount: Long? = null,
    val description: String? = null,
    val account: Long? = null,
    val costCenter: String? = null,
    val subCostCenter1: String? = null,
    val subCostCenter2: String? = null,
    val project: String? = null,
    val product: String? = null,
    val function: String? = null
)

@Serializable
data class Invoice(
    val invoiceNumber: Long,
    val useInvoiceNumber: Boolean? = false,
    val date: String? = null,
    val dueDate: String? = null,
    val printDate: String? = null,
    val client: Client? = null,
    val rows: List<Row>
)

@Serializable
data class Client(
    val ssn: String? = null,
    val ytunnus: String? = null,
    val registerNumber: String? = null,
    val lastname: String,
    val firstnames: String? = null,
    val contactPerson: String? = null,
    val street: String? = null,
    val post: String? = null,
    val postalCode: String? = null,
    val language: String? = null,
    val mobilePhone: String? = null,
    val email: String? = null
)

@Serializable
data class InvoiceBatch(
    val agreementType: Int,
    val batchDate: String,
    val batchNumber: Long,
    val currency: String,
    val sourcePrinted: Boolean,
    val systemId: String,
    val invoices: List<Invoice>,
    val totalObjects: Int? = null,
    val totalAmount: Long? = null
)

interface InvoiceClient {
    fun sendBatchInvoice(invoiceData: InvoiceData)
}

@Service
@Profile("test || local")
class MockInvoiceClient(
    val timeProvider: TimeProvider
) : InvoiceClient {
    override fun sendBatchInvoice(invoiceData: InvoiceData) {
        println("sending invoice $invoiceData")
    }
}

@Service
@Profile("!(test || local)")
class EspiInvoiceClient(
    val espiEnv: EspiEnv,
    val timeProvider: TimeProvider
) : InvoiceClient {
    override fun sendBatchInvoice(invoiceData: InvoiceData) {
        val logger = KotlinLogging.logger {}
        val json =
            Json {
                encodeDefaults = false
            }
        val (apiUrl, apiUsername, apiPassword) = espiEnv
        logger.info { "Sending invoice to $apiUrl, with username $apiUsername" }
        val url = "$apiUrl/invoice/api/v1/invoice-batches"
        val authHeader = Base64.getEncoder().encodeToString("$apiUsername:$apiPassword".toByteArray())
        val headers =
            mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "Basic $authHeader"
            )
        val invoiceBatch = createInvoiceBatch(invoiceData, timeProvider)
        val encodedBody = json.encodeToString(invoiceBatch)
        val response = runBlocking { VekkuliHttpClient.makePostRequest(url, encodedBody, headers) }
        if (response?.status != HttpStatusCode.OK) {
            logger.error { "Invoice sending failed: ${response.status}" }
            throw RuntimeException("Failed to send invoice")
        }
    }
}

fun createInvoiceBatch(
    invoiceData: InvoiceData,
    timeProvider: TimeProvider
): InvoiceBatch {
    if (invoiceData.orgId == null) {
        return createInvoiceBatchForPerson(timeProvider, invoiceData)
    } else {
        return createInvoiceBatchForOrganization(timeProvider, invoiceData)
    }
}

private const val PRODUCT_GROUP = "25600001"

private const val COST_CENTER = "1230329"

private const val ACCOUNT = 329700L

private const val SYSTEM_ID = "VKK"

private const val AGREEMENT_TYPE = 256

private fun createInvoiceBatchForPerson(
    timeProvider: TimeProvider,
    invoiceData: InvoiceData
) = InvoiceBatch(
    agreementType = AGREEMENT_TYPE,
    batchDate = timeProvider.getCurrentDate().toString(),
    batchNumber = invoiceData.invoiceNumber ?: throw RuntimeException("Invoice number is missing"),
    currency = "EUR",
    sourcePrinted = false,
    systemId = SYSTEM_ID,
    invoices =
        listOf(
            Invoice(
                invoiceNumber = invoiceData.invoiceNumber,
                useInvoiceNumber = true,
                dueDate = timeProvider.getCurrentDate().plusDays(21).toString(),
                client =
                    Client(
                        ssn = invoiceData.ssn,
                        lastname = invoiceData.lastname ?: "",
                        firstnames = invoiceData.firstnames ?: "",
                        street = invoiceData.street,
                        post = invoiceData.post,
                        postalCode = invoiceData.postalCode,
                        language = "fi",
                        mobilePhone = invoiceData.mobilePhone,
                        email = invoiceData.email
                    ),
                rows =
                    listOf(
                        Row(
                            productGroup = PRODUCT_GROUP,
                            periodStartDate = invoiceData.startDate.toString(),
                            periodEndDate = invoiceData.endDate.toString(),
                            unitCount = 100,
                            amount = invoiceData.priceCents.toLong(),
                            description = invoiceData.description,
                            account = ACCOUNT,
                            costCenter = COST_CENTER,
                            function = invoiceData.function
                        )
                    )
            )
        ),
)

private fun createInvoiceBatchForOrganization(
    timeProvider: TimeProvider,
    invoiceData: InvoiceData
) = InvoiceBatch(
    agreementType = AGREEMENT_TYPE,
    batchDate = timeProvider.getCurrentDate().toString(),
    batchNumber = invoiceData.invoiceNumber ?: throw RuntimeException("Invoice number is missing"),
    currency = "EUR",
    sourcePrinted = false,
    systemId = SYSTEM_ID,
    invoices =
        listOf(
            Invoice(
                invoiceNumber = invoiceData.invoiceNumber,
                useInvoiceNumber = true,
                dueDate = timeProvider.getCurrentDate().plusDays(21).toString(),
                client =
                    Client(
                        ytunnus = invoiceData.orgId,
                        lastname = invoiceData.orgName ?: "",
                        contactPerson = "${invoiceData.firstnames} ${invoiceData.lastname}",
                        street = invoiceData.street,
                        post = invoiceData.post,
                        postalCode = invoiceData.postalCode,
                        language = "fi",
                        mobilePhone = invoiceData.mobilePhone,
                        email = invoiceData.email
                    ),
                rows =
                    listOf(
                        Row(
                            productGroup = PRODUCT_GROUP,
                            function = invoiceData.function,
                            periodStartDate = invoiceData.startDate.toString(),
                            periodEndDate = invoiceData.endDate.toString(),
                            unitCount = 100,
                            amount = invoiceData.priceCents.toLong(),
                            description = invoiceData.description,
                            account = ACCOUNT,
                            costCenter = COST_CENTER,
                        )
                    )
            )
        ),
)
