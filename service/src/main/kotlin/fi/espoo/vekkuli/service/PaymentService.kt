package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.CreateInvoiceParams
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.domain.Payment
import fi.espoo.vekkuli.repository.PaymentRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class PaymentService(
    private val paymentRepo: PaymentRepository
) {
    fun getPayment(stamp: UUID): Payment? = paymentRepo.getPayment(stamp)

    fun updatePayment(
        id: UUID,
        success: Boolean,
        paidDate: LocalDateTime?
    ): Payment? = paymentRepo.updatePaymentStatus(id, success, paidDate)

    fun insertPayment(
        params: CreatePaymentParams,
        reservationId: Int
    ): Payment = paymentRepo.insertPayment(params, reservationId)

    fun insertInvoicePayment(params: CreateInvoiceParams): Invoice = paymentRepo.insertInvoicePayment(params)

    fun getInvoice(invoiceId: UUID): Invoice? = paymentRepo.getInvoice(invoiceId)

    fun getInvoiceForReservation(reservationId: Int): Invoice? = paymentRepo.getInvoice(reservationId)
}
