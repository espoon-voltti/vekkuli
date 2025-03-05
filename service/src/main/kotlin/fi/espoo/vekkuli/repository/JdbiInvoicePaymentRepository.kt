package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.boatSpace.invoice.InvoicePayment
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
                    ON CONFLICT (transaction_number) DO NOTHING
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
}
