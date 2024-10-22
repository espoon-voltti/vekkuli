package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.Invoice
import kotlinx.serialization.Serializable

interface InvoiceClient {
    fun sendInvoice(invoice: Invoice): Boolean
}

class MockInvoiceClient : InvoiceClient {
    override fun sendInvoice(invoice: Invoice): Boolean {
        println("sending invoice ${invoice.id}")
        return true
    }
}

// dtos
@Serializable
data class InvoiceBatch(
    val agreementType: Int,
    val batchDate: String,
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
    val date: String?,
    val dueDate: String?,
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
