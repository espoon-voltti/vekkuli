package fi.espoo.vekkuli.boatSpace.invoice

import fi.espoo.vekkuli.config.ReservationWarningType
import fi.espoo.vekkuli.repository.InvoicePaymentRepository
import fi.espoo.vekkuli.repository.PaymentRepository
import fi.espoo.vekkuli.service.ReservationWarningRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.UUID

@Service
@Profile("!test")
class ScheduledInvoicePaymentService(
    private val invoicePaymentService: InvoicePaymentService
) {
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    fun scheduleFetchAndStoreInvoicePayments() {
        invoicePaymentService.fetchAndStoreInvoicePayments()
    }
}

@Service
class InvoicePaymentService(
    private val invoicePaymentClient: InvoicePaymentClient,
    private val invoicePaymentRepository: InvoicePaymentRepository,
    private val paymentRepository: PaymentRepository,
    private val reservationWarningRepository: ReservationWarningRepository
) {
    val logger = KotlinLogging.logger {}

    fun fetchAndStoreInvoicePayments() {
        val newInvoicePayments = invoicePaymentClient.getPayments().receipts.map { createInvoicePayment(it) }
        val invoicePayments =
            newInvoicePayments.filter {
                invoicePaymentRepository.getInvoicePayments(it.invoiceNumber.toLong()).none { existingInvoicePayment ->
                    existingInvoicePayment.transactionNumber == it.transactionNumber
                }
            }

        if (invoicePayments.isNotEmpty()) {
            invoicePaymentRepository.insertInvoicePayments(invoicePayments)
            invoicePayments.forEach { invoicePayment ->
                paymentRepository.getInvoiceWithInvoiceNumber(invoicePayment.invoiceNumber)?.let { invoice ->
                    reservationWarningRepository.addReservationWarnings(
                        UUID.randomUUID(),
                        invoice.reservationId,
                        null,
                        null,
                        invoicePayment.invoiceNumber,
                        null,
                        listOf(ReservationWarningType.InvoicePayment.name)
                    )
                } ?: logger.error { "No invoice found for invoice payment: $invoicePayment" }
            }
        }
    }

    private fun createInvoicePayment(invoicePaymentResponse: Receipt): InvoicePayment {
        try {
            val invoiceNumber = (invoicePaymentResponse.invoiceNumber.split("_").last()).toInt()
            val amountPaidCents =
                invoicePaymentResponse.amountPaid
                    .multiply(BigDecimal(100))
                    .setScale(0, RoundingMode.HALF_UP) // Ensures proper rounding
                    .toInt()
            val paymentDate = LocalDate.parse(invoicePaymentResponse.paymentDate)
            return InvoicePayment(
                transactionNumber = invoicePaymentResponse.transactionNumber,
                amountPaidCents = amountPaidCents,
                paymentDate = paymentDate,
                invoiceNumber = invoiceNumber
            )
        } catch (e: Exception) {
            throw Exception("Error creating invoice payment", e)
        }
    }

    fun getInvoicePayments(invoiceId: Long): List<InvoicePayment> = invoicePaymentRepository.getInvoicePayments(invoiceId)
}

data class InvoicePayment(
    val transactionNumber: Int,
    val amountPaidCents: Int,
    val paymentDate: LocalDate,
    val invoiceNumber: Int,
)

data class InvoicePaymentWithReservationWarningId(
    val transactionNumber: Int,
    val amountPaidCents: Int,
    val paymentDate: LocalDate,
    val invoiceNumber: Int,
    val reservationWarningId: UUID?
)
