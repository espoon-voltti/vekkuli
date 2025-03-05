package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.domain.CreateInvoiceParams
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.domain.Payment
import fi.espoo.vekkuli.domain.PaymentHistory
import fi.espoo.vekkuli.domain.PaymentType
import fi.espoo.vekkuli.repository.InvoicePaymentRepository
import fi.espoo.vekkuli.repository.PaymentRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class PaymentService(
    private val paymentRepo: PaymentRepository,
    private val invoicePaymentRepository: InvoicePaymentRepository,
) {
    fun getPaymentClasses(): List<Price> = paymentRepo.getPaymentClasses()

    fun getPayment(stamp: UUID): Payment? = paymentRepo.getPayment(stamp)

    fun deletePaymentInCreatedStatusForReservation(reservationId: Int) {
        paymentRepo.deletePaymentInCreatedStatusForReservation(reservationId)
    }

    fun updatePayment(
        id: UUID,
        success: Boolean,
        paidDate: LocalDateTime?
    ): Payment? = paymentRepo.updatePaymentStatus(id, success, paidDate)

    fun updatePayment(payment: Payment): Payment? = paymentRepo.updatePayment(payment)

    fun insertPayment(
        params: CreatePaymentParams,
        reservationId: Int
    ): Payment = paymentRepo.insertPayment(params, reservationId)

    fun insertInvoice(params: CreateInvoiceParams): Invoice = paymentRepo.insertInvoice(params)

    fun getInvoice(invoiceId: UUID): Invoice? = paymentRepo.getInvoice(invoiceId)

    fun getInvoiceForReservation(reservationId: Int): Invoice? = paymentRepo.getInvoice(reservationId)

    fun getReserverPaymentHistory(reserverId: UUID): List<PaymentHistory> =
        paymentRepo.getReserverPaymentDetails(reserverId).map {
            PaymentHistory(
                paymentDetails = it,
                invoicePayments =
                    if (it.paymentType == PaymentType.Invoice && it.paymentReference.isNotEmpty()) {
                        invoicePaymentRepository.getInvoicePayments(it.paymentReference.toLong())
                    } else {
                        emptyList()
                    }
            )
        }
}
