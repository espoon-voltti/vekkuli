package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.utils.LocalDateSerializer
import fi.espoo.vekkuli.utils.TimeProvider
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

data class Row(
    // reservationId is used as productId
    val productId: String,
    val productGroup: String?,
    val productComponent: String?,
    val periodStartDate: String,
    val periodEndDate: String?,
    val unitCount: Long?,
    val unitPrice: Long?,
    val amount: Long,
    val vatAmount: Long?,
    val description: String?,
    val project: String?,
    val product: String?
)

data class InvoiceAddress(
    val street: String,
    val postalCode: String,
    val postOffice: String,
)

data class InvoiceRecipient(
    val id: UUID,
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
