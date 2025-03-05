package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.boatSpace.invoice.InvoicePayment

interface InvoicePaymentRepository {
    fun insertInvoicePayments(invoicePayments: List<InvoicePayment>): Unit

    fun getInvoicePayments(invoiceNumber: Long): List<InvoicePayment>
}
