package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.common.VekkuliHttpClient
import fi.espoo.vekkuli.config.EspiEnv
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.utils.TimeProvider
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.*

@Serializable
data class Row(
    val productGroup: String? = null,
    val productComponent: String? = null,
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
    val product: String? = null
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
    val firstnames: String,
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
        println("Sending invoice $invoiceData")
        val json =
            Json {
                encodeDefaults = false
            }
        val (apiUrl, apiUsername, apiPassword) = espiEnv
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
        if (response?.status == HttpStatusCode.OK) {
            throw RuntimeException("Failed to send invoice")
        }
    }
}

fun createInvoiceBatch(
    invoiceData: InvoiceData,
    timeProvider: TimeProvider
): InvoiceBatch =
    InvoiceBatch(
        // TODO: add correct values for batchNumber
        agreementType = 256,
        batchDate = timeProvider.getCurrentDate().toString(),
        batchNumber = 1,
        currency = "EUR",
        sourcePrinted = false,
        systemId = "VKK",
        invoices =
            listOf(
                Invoice(
                    // TODO: add correct invoice number
                    invoiceNumber = invoiceData.invoiceNumber,
                    useInvoiceNumber = true,
                    dueDate = timeProvider.getCurrentDate().plusDays(21).toString(),
                    client =
                        Client(
                            ssn = invoiceData.ssn,
                            ytunnus = invoiceData.orgId,
                            lastname = invoiceData.lastname,
                            firstnames = invoiceData.firstnames,
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
                                // TODO: add correct values for productGroup, productComponent, project
                                productGroup = "2560001",
                                productComponent = getProductComponent(invoiceData.type),
                                periodStartDate = invoiceData.startDate.toString(),
                                periodEndDate = invoiceData.endDate.toString(),
                                unitCount = 100,
                                amount = invoiceData.priceCents.toLong(),
                                description = invoiceData.description,
                                product = "T1270",
                                account = 329700,
                                costCenter = "1230329",
                            )
                        )
                )
            ),
    )

fun getProductComponent(boatSpaceType: BoatSpaceType): String =
    when (boatSpaceType) {
        BoatSpaceType.Slip -> "T1270"
        BoatSpaceType.Winter -> "T1271"
        BoatSpaceType.Storage -> "T1276"
        BoatSpaceType.Trailer -> "T1270"
    }
