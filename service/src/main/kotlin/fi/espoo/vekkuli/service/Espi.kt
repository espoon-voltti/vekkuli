package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.CreateInvoiceParams
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.utils.LocalDateSerializer
import fi.espoo.vekkuli.utils.TimeProvider
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

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
    val rows: List<Row>
)

data class InvoiceBatchParameters(
    val agreementType: Int,
    @Serializable(with = LocalDateSerializer::class) val batchDate: LocalDate,
    val batchNumber: Long,
    val currency: String = "EUR",
    val sourcePrinted: Boolean = false,
    val systemId: String,
    val invoices: List<InvoiceParameters>,
)

interface InvoiceClient {
    fun sendBatchInvoice(invoiceBatch: InvoiceBatchParameters): Boolean
}

@Service
class MockInvoiceClient(
    private val timeProvider: TimeProvider
) : InvoiceClient {
    override fun sendBatchInvoice(invoiceBatch: InvoiceBatchParameters): Boolean {
        val invoice = invoiceBatch.invoices.first()
        println("sending invoice ${invoice.invoiceNumber}")
        return true
    }
}

@Service
class BoatSpaceInvoiceService(
    private val invoiceClient: InvoiceClient,
    private val paymentService: PaymentService,
    private val timeProvider: TimeProvider
) {
    fun createInvoice(
        invoiceNumber: Long,
        dueDate: LocalDate,
        recipient: InvoiceRecipient,
        reservationId: Int,
        citizenId: UUID,
        invoiceRows: List<Row>
    ): Invoice? {
        val invoice =
            InvoiceParameters(
                invoiceNumber = invoiceNumber,
                dueDate = dueDate,
                recipient = recipient,
                rows = invoiceRows
            )
        val batch =
            InvoiceBatchParameters(
                // TODO: add correct values
                agreementType = 249,
                batchDate = timeProvider.getCurrentDate(),
                batchNumber = 1,
                systemId = System.getenv("INVOICE_SYSTEM_ID") ?: "vekkuli",
                invoices = listOf(invoice),
            )
        val sendInvoiceSuccess = invoiceClient.sendBatchInvoice(batch)
        if (!sendInvoiceSuccess) {
            // error handling
            return null
        }
        return paymentService.insertInvoicePayment(
            CreateInvoiceParams(
                dueDate = invoice.dueDate,
                reference = invoice.invoiceNumber.toString(),
                citizenId = citizenId,
                reservationId = reservationId
            )
        )
    }
}
