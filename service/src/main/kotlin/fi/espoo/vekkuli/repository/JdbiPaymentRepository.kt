package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.bindKotlin
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JdbiPaymentRepository(
    private val jdbi: Jdbi
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
    override fun updatePayment(
        id: UUID,
        success: Boolean
    ): Payment? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    UPDATE payment
                    SET status = :status, updated = now()
                    WHERE id = :id AND status = 'Created'
                    RETURNING *
                    """
                ).bind("id", id)
                .bind("status", if (success) PaymentStatus.Success else PaymentStatus.Failed)
                .mapTo<Payment>()
                .firstOrNull()
        }

    override fun insertInvoicePayment(
        params: CreateInvoiceParams,
        reservationId: Int
    ): Invoice {
        val id = UUID.randomUUID()
        return jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    INSERT INTO invoice (id, due_date, reference, reservation_id, citizen_id)
                    VALUES (:id, :dueDate, :reference, :reservationId, :citizenId)
                    RETURNING *
                    """
                ).bindKotlin(params)
                .bind("id", id)
                .bind("reservationId", reservationId)
                .mapTo<Invoice>()
                .one()
        }
    }

    override fun getInvoicePayment(id: UUID): Invoice? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT * FROM invoice WHERE id = :id
                    """.trimIndent()
                ).bind("id", id)
                .mapTo<Invoice>()
                .firstOrNull()
        }

    override fun setInvoicePaid(invoiceId: UUID): Invoice? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    UPDATE invoice
                    SET payment_date = now()
                    WHERE id = :id
                    RETURNING *
                    """
                ).bind("id", invoiceId)
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
