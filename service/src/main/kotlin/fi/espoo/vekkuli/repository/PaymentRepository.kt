package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.CreateInvoiceParams
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.domain.Payment
import fi.espoo.vekkuli.domain.PaymentDetails
import java.time.LocalDateTime
import java.util.*

interface PaymentRepository {
    fun getPayment(stamp: UUID): Payment?

    fun getPaymentForReservation(reservationId: Int): Payment?

    fun deletePaymentInCreatedStatusForReservation(reservationId: Int): Unit

    fun insertPayment(
        params: CreatePaymentParams,
        reservationId: Int
    ): Payment

    fun updatePayment(payment: Payment): Payment

    fun updatePaymentStatus(
        id: UUID,
        success: Boolean,
        paidDate: LocalDateTime?
    ): Payment?

    fun insertInvoice(params: CreateInvoiceParams): Invoice

    fun getInvoice(invoiceId: UUID): Invoice?

    fun getInvoice(reservationId: Int): Invoice?

    fun getInvoiceWithInvoiceNumber(invoiceNumber: Int): Invoice?

    fun getReserverPaymentDetails(reserverId: UUID): List<PaymentDetails>
}
