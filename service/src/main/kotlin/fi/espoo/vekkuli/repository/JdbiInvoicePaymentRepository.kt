package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.boatSpace.invoice.InvoicePayment
import fi.espoo.vekkuli.boatSpace.invoice.InvoicePaymentWithReservationWarningId
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository

@Repository
class JdbiInvoicePaymentRepository(
    private val jdbi: Jdbi
) : InvoicePaymentRepository {
    override fun insertInvoicePayments(invoicePayments: List<InvoicePayment>) {
        jdbi.withHandleUnchecked { handle ->
            val batch =
                handle.prepareBatch(
                    """
                    INSERT INTO invoice_payment (transaction_number, amount_paid_cents, payment_date, invoice_number)
                    VALUES (:transactionNumber, :amountPaidCents, :paymentDate, :invoiceNumber)
                    """.trimIndent()
                )

            for (payment in invoicePayments) {
                batch
                    .bind("transactionNumber", payment.transactionNumber)
                    .bind("amountPaidCents", payment.amountPaidCents)
                    .bind("paymentDate", payment.paymentDate)
                    .bind("invoiceNumber", payment.invoiceNumber)
                    .add()
            }

            batch.execute()
        }
    }

    override fun getInvoicePayments(invoiceNumber: Long): List<InvoicePayment> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    "SELECT transaction_number, amount_paid_cents, payment_date, invoice_number FROM invoice_payment WHERE invoice_number = :invoiceNumber"
                ).bind("invoiceNumber", invoiceNumber)
                .mapTo<InvoicePayment>()
                .list()
        }

    override fun getInvoicePaymentsWithReservationWarningInfo(invoiceNumber: Long): List<InvoicePaymentWithReservationWarningId> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT 
                        transaction_number, 
                        amount_paid_cents, 
                        payment_date, 
                        invoice_payment.invoice_number,
                        rw.id as reservation_warning_id
                    FROM invoice_payment 
                    LEFT JOIN reservation_warning rw ON rw.invoice_number = invoice_payment.invoice_number
                    WHERE invoice_payment.invoice_number = :invoiceNumber
                    """.trimIndent()
                ).bind("invoiceNumber", invoiceNumber)
                .mapTo<InvoicePaymentWithReservationWarningId>()
                .list()
        }
}
