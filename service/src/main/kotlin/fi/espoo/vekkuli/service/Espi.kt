package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.CreateInvoiceParams
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.utils.LocalDateSerializer
import fi.espoo.vekkuli.utils.TimeProvider
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Service
import java.time.LocalDate

// dtos
@Serializable
data class InvoiceBatch(
    val agreementType: Int,
    @Serializable(with = LocalDateSerializer::class) val batchDate: LocalDate,
    val batchNumber: Long,
    val currency: String = "EUR",
    val sourcePrinted: Boolean,
    val systemId: String,
    val invoices: List<InvoiceDto>,
    val totalObjects: Int?,
    val totalAmount: Long?
)

@Serializable
data class InvoiceDto(
    val invoiceNumber: Long,
    val useInvoiceNumber: Boolean?,
    @Serializable(with = LocalDateSerializer::class) val date: LocalDate?,
    @Serializable(with = LocalDateSerializer::class) val dueDate: LocalDate?,
    val printDate: String?,
    val client: Client?,
    val recipient: Recipient?,
    val rows: List<Row>?
)

@Serializable
data class Recipient(
    val ssn: String?
)

@Serializable
data class Client(
    val ssn: String?,
    val ytunnus: String?,
    val registerNumber: String?,
    val lastname: String,
    val firstnames: String,
    val contactPerson: String?,
    val street: String?,
    val post: String?,
    val postalCode: String?,
    val language: String,
    val homePhone: String?,
    val mobilePhone: String?,
    val faxNumber: String?,
    val email: String?
)

@Serializable
data class Row(
    val productGroup: String?,
    val productComponent: String?,
    val periodStartDate: String,
    val periodEndDate: String?,
    val unitCount: Long?,
    val unitPrice: Long?,
    val amount: Long,
    val vatAmount: Long?,
    val description: String?,
    val account: Long?,
    val costCenter: String?,
    val subCostCenter1: String?,
    val subCostCenter2: String?,
    val project: String?,
    val product: String?
)

data class InvoiceAddress(
    val street: String,
    val postalCode: String,
    val postOffice: String,
)

data class InvoiceRecipient(
    val ssn: String,
    val firstName: String,
    val lastName: String,
    val address: InvoiceAddress,
)

data class InvoiceParameters(
    val invoiceNumber: Long,
    val dueDate: LocalDate,
    val recipient: InvoiceRecipient,
)

data class InvoiceBatchParameters(
    val agreementType: Int,
    @Serializable(with = LocalDateSerializer::class) val batchDate: LocalDate,
    val batchNumber: Long,
    val currency: String = "EUR",
    val sourcePrinted: Boolean,
    val systemId: String,
    val invoices: List<InvoiceParameters>,
    val totalObjects: Int?,
    val totalAmount: Long?
)

interface EspClient {
    fun sendBatchInvoice(invoiceBatch: InvoiceBatchParameters): Boolean
}

@Service
class MockEspClient(
    private val timeProvider: TimeProvider
) : EspClient {
    override fun sendBatchInvoice(invoiceBatch: InvoiceBatchParameters): Boolean {
        val invoice = invoiceBatch.invoices.first()
        println("sending invoice ${invoice.invoiceNumber}")
        return true
    }
}

interface InvoiceClient {
    fun sendInvoice(invoice: InvoiceParameters): Boolean
}

@Service
class MockInvoiceClient(
    private val espClient: EspClient,
    private val timeProvider: TimeProvider
) : InvoiceClient {
    override fun sendInvoice(invoice: InvoiceParameters): Boolean {
        val batch =
            InvoiceBatchParameters(
                agreementType = 1,
                batchDate = timeProvider.getCurrentDate(),
                batchNumber = 1,
                systemId = "1",
                sourcePrinted = false,
                invoices = listOf(invoice),
                totalObjects = 1,
                totalAmount = 100
            )
        val sendingSuccessful = espClient.sendBatchInvoice(batch)
        if (!sendingSuccessful) {
            // error handling
            println("invoice sending failed")
            return null
        }

        // add invoice to db
    }
}

@Service
class SendInvoiceService(
    private val invoiceClient: InvoiceClient,
    private val paymentService: PaymentService,
    private val timeProvider: TimeProvider
) {
    fun createInvoiceForReservation(
        invoiceNumber: Long,
        dueDate: LocalDate,
        recipient: InvoiceRecipient,
        reservationId: Int,
        citizenId: UUID,
    ): Invoice? {
        val invoice =
            InvoiceParameters(
                invoiceNumber = invoiceNumber,
                dueDate = dueDate,
                recipient = recipient
            )
        val sendInvoiceSuccess = invoiceClient.sendInvoice(invoice)
        if (!sendInvoiceSuccess) {
            // error handling
            return null
        }
        paymentService.insertInvoicePayment(
            CreateInvoiceParams(
                dueDate = invoice.dueDate,
                reference = invoice.invoiceNumber.toString(),
                citizenId = citizenId
            )
        )
    }
}
