package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.bindKotlin
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class JdbiPaymentRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) : PaymentRepository {
    override fun insertPayment(
        params: CreatePaymentParams,
        reservationId: Int
    ): Payment =
        jdbi.withHandleUnchecked { handle ->
            val id = UUID.randomUUID()
            val result =
                handle
                    .createQuery(
                        """
                        INSERT INTO payment (id, citizen_id, reference, total_cents, vat_percentage, product_code, reservation_id)
                        VALUES (:id, :citizenId,  :reference, :totalCents, :vatPercentage, :productCode, :reservationId)
                        RETURNING *
                        """
                    ).bindKotlin(params)
                    .bind("id", id)
                    .bind("reservationId", reservationId)
                    .mapTo<Payment>()
                    .one()
            result
        }

    // Update payment only if it is 'Created' state
    override fun updatePaymentStatus(
        id: UUID,
        success: Boolean,
        paidDate: LocalDateTime?
    ): Payment? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    UPDATE payment
                    SET status = :status, updated = :updated, paid = :paid
                    WHERE id = :id AND status = 'Created'
                    RETURNING *
                    """
                ).bind("id", id)
                .bind("paid", paidDate)
                .bind("updated", timeProvider.getCurrentDateTime())
                .bind("status", if (success) PaymentStatus.Success else PaymentStatus.Failed)
                .mapTo<Payment>()
                .firstOrNull()
        }

    override fun insertInvoicePayment(params: CreateInvoiceParams): Invoice {
        val id = UUID.randomUUID()
        return jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    INSERT INTO invoice (id, due_date, reference, reservation_id, citizen_id, payment_id)
                    VALUES (:id, :dueDate, :reference, :reservationId, :citizenId, :paymentId)
                    RETURNING *
                    """
                ).bindKotlin(params)
                .bind("id", id)
                .bind("reservationId", params.reservationId)
                .bind("citizenId", params.citizenId)
                .bind("paymentId", params.paymentId)
                .mapTo<Invoice>()
                .one()
        }
    }

    override fun getInvoice(invoiceId: UUID): Invoice? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT * FROM invoice WHERE id = :invoiceId
                    """.trimIndent()
                ).bind("invoiceId", invoiceId)
                .mapTo<Invoice>()
                .firstOrNull()
        }

    override fun getInvoice(reservationId: Int): Invoice? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT * FROM invoice WHERE reservation_id = :reservationId
                    """.trimIndent()
                ).bind("reservationId", reservationId)
                .mapTo<Invoice>()
                .firstOrNull()
        }

    override fun getPayment(stamp: UUID): Payment? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT * FROM payment WHERE id = :id
                    """.trimIndent()
                ).bind("id", stamp)
                .mapTo<Payment>()
                .firstOrNull()
        }
}
