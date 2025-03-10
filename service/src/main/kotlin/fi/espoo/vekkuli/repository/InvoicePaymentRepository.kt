package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.boatSpace.invoice.InvoicePayment
import fi.espoo.vekkuli.boatSpace.invoice.InvoicePaymentWithReservationWarningId

interface InvoicePaymentRepository {
    fun insertInvoicePayments(invoicePayments: List<InvoicePayment>): Unit

    fun getInvoicePayments(invoiceNumber: Long): List<InvoicePayment>

    fun getInvoicePaymentsWithReservationWarningInfo(invoiceNumber: Long): List<InvoicePaymentWithReservationWarningId>
}
