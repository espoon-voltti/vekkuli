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
                        INSERT INTO payment (id, reserver_id, reference, total_cents, vat_percentage, product_code, reservation_id, status)
                        VALUES (:id, :reserverId,  :reference, :totalCents, :vatPercentage, :productCode, :reservationId, :status)
                        RETURNING *
                        """
                    ).bindKotlin(params)
                    .bind("id", id)
                    .bind("reservationId", reservationId)
                    .mapTo<Payment>()
                    .one()
            result
        }

    override fun updatePayment(payment: Payment): Payment =
        jdbi.withHandleUnchecked { handle ->
            val id = UUID.randomUUID()
            val result =
                handle
                    .createQuery(
                        """
                        UPDATE payment
                            SET status = :status, updated = :updated, paid = :paid, reference = :reference, total_cents = :totalCents, vat_percentage = :vatPercentage, product_code = :productCode
                        WHERE id = :id 
                        RETURNING *
                        """
                    ).bindKotlin(payment)
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

    override fun insertInvoice(params: CreateInvoiceParams): Invoice {
        val id = UUID.randomUUID()
        return jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    INSERT INTO invoice (id, due_date, reference, reservation_id, reserver_id, payment_id)
                    VALUES (:id, :dueDate, :reference, :reservationId, :reserverId, :paymentId)
                    RETURNING *
                    """
                ).bindKotlin(params)
                .bind("id", id)
                .bind("reservationId", params.reservationId)
                .bind("reserverId", params.reserverId)
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

    override fun deletePaymentInCreatedStatusForReservation(reservationId: Int) {
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    """
                    DELETE FROM payment WHERE reservation_id = :reservationId AND status = 'Created'
                    """.trimIndent()
                ).bind("reservationId", reservationId)
                .execute()
        }
    }

    override fun getPaymentForReservation(reservationId: Int): Payment? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT * FROM payment WHERE reservation_id = :reservationId
                    """.trimIndent()
                ).bind("reservationId", reservationId)
                .mapTo<Payment>()
                .firstOrNull()
        }

    // TODO invoice referenceksi menee null
    // TODO paymentistä puuttuu tyyppi
    override fun getReserverPaymentHistory(reserverId: UUID): List<PaymentHistory> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT 
                        p.status AS paymentStatus,
                        p.paid AS paid_date, 
                        p.total_cents,
                        location.name AS harbor_name,
                        CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) as place,
                        bs.type AS boat_space_type,
                        p.reference AS paymentReference, 
                        i.reference AS invoiceReference,
                        i.due_date AS invoiceDueDate
                    FROM boat_space_reservation bsr
                        JOIN boat_space bs ON bsr.boat_space_id = bs.id
                        JOIN location ON bs.location_id = location.id
                        JOIN payment p ON bsr.id = p.reservation_id
                        LEFT JOIN invoice i ON p.id = i.payment_id
                    WHERE bsr.reserver_id = :reserverId
                    """.trimIndent()
                ).bind("reserverId", reserverId)
                .mapTo<PaymentHistory>()
                .list()
        }
}
