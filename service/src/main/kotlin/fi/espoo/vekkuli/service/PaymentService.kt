package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.CreateInvoiceParams
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.domain.Payment
import fi.espoo.vekkuli.repository.PaymentRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentService(
    private val paymentRepo: PaymentRepository
) {
    fun getPayment(stamp: UUID): Payment? = paymentRepo.getPayment(stamp)

    fun updatePayment(
        id: UUID,
        success: Boolean
    ): Payment? = paymentRepo.updatePayment(id, success)

    fun insertPayment(
        params: CreatePaymentParams,
        reservationId: Int
    ): Payment = paymentRepo.insertPayment(params, reservationId)

    fun insertInvoicePayment(
        params: CreateInvoiceParams,
        reservationId: Int
    ): Invoice = paymentRepo.insertInvoicePayment(params, reservationId)

    fun getInvoicePayment(stamp: UUID): Invoice? = paymentRepo.getInvoicePayment(stamp)

    fun setInvoicePaid(invoiceId: UUID): Invoice? = paymentRepo.setInvoicePaid(invoiceId)
}
