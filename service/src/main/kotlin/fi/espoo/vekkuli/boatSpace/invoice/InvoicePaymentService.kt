package fi.espoo.vekkuli.boatSpace.invoice

import fi.espoo.vekkuli.repository.InvoicePaymentRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
class InvoicePaymentService(
    private val invoicePaymentClient: InvoicePaymentClient,
    private val invoicePaymentRepository: InvoicePaymentRepository
) {
//    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    // To test this in staging, setting the scheduler to run once per hour
    @Scheduled(fixedRate = 1000 * 60 * 60)
    fun fetchAndStoreInvoicePayments() {
        val invoicePaymentResponse = invoicePaymentClient.getPayments()
        val invoicePayments = invoicePaymentResponse.map { createInvoicePayment(it) }
        if (invoicePayments.isNotEmpty()) {
            invoicePaymentRepository.insertInvoicePayments(invoicePayments)
        }
    }

    private fun createInvoicePayment(invoicePaymentResponse: InvoicePaymentResponse): InvoicePayment {
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
            throw Exception("Error creating invoice payment")
        }
    }

    fun getInvoicePayments(invoiceId: Long): List<InvoicePayment> = invoicePaymentRepository.getInvoicePayments(invoiceId)
}

data class InvoicePayment(
    val transactionNumber: Int,
    val amountPaidCents: Int,
    val paymentDate: LocalDate,
    val invoiceNumber: Int
)
