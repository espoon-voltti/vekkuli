package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.CreateInvoiceParams
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.domain.Payment
import java.time.LocalDateTime
import java.util.*

interface PaymentRepository {
    fun getPayment(stamp: UUID): Payment?

    fun deletePaymentInCreatedStatusForReservation(reservationId: Int): Unit

    fun insertPayment(
        params: CreatePaymentParams,
        reservationId: Int
    ): Payment

    fun updatePaymentStatus(
        id: UUID,
        success: Boolean,
        paidDate: LocalDateTime?
    ): Payment?

    fun insertInvoicePayment(params: CreateInvoiceParams): Invoice

    fun getInvoice(invoiceId: UUID): Invoice?

    fun getInvoice(reservationId: Int): Invoice?
}
