package fi.espoo.vekkuli.domain

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.time.LocalDateTime
import java.util.*

data class ReservationWarning(
    val id: Int,
    val key: String,
    val created: LocalDateTime,
    val reservationId: Int,
    val ackAt: LocalDateTime?,
    val ackBy: UUID?,
    val note: String,
)

fun Handle.addReservationWarning(
    reservationId: Int,
    key: String,
): ReservationWarning =
    createQuery(
        """
        INSERT INTO reservation_warning (reservation_id, key)
        VALUES (:reservationId, :key)
        ON CONFLICT (reservation_id, key) DO NOTHING
        RETURNING *
        """
    ).bind("reservationId", reservationId)
        .bind("key", key)
        .mapTo<ReservationWarning>()
        .one()

fun Handle.getUnAcknowledgedReservationWarnings(reservationId: Int): List<ReservationWarning> =
    createQuery(
        """
        SELECT *
        FROM reservation_warning
        WHERE reservation_id = :reservationId AND ack_at IS NULL
        """
    ).bind("reservationId", reservationId)
        .mapTo<ReservationWarning>()
        .list()

fun Handle.setReservationWarningAcknowledged(
    reservationId: Int,
    key: String,
    ackBy: UUID,
    note: String
): ReservationWarning? =
    createQuery(
        """
        UPDATE reservation_warning
        SET ack_at = now(), ack_by = :ackBy, note = :note
        WHERE reservation_Id = :reservationId AND key = :key
        RETURNING *
        """
    ).bind("reservationId", reservationId)
        .bind("key", key)
        .bind("ackBy", ackBy)
        .bind("note", note)
        .mapTo<ReservationWarning>()
        .one()
