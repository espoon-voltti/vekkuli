package fi.espoo.vekkuli.domain

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.time.LocalDateTime

data class ReservationWarning(
    val reservationId: Int,
    val key: String,
    val created: LocalDateTime,
)

fun Handle.addReservationWarnings(
    reservationId: Int,
    keys: List<String>,
) {
    val sql = StringBuilder()

    sql.append("INSERT INTO reservation_warning (reservation_id, key) VALUES ")
    sql.append(
        keys
            .mapIndexed { index, _ ->
                "(:reservationId, :key$index)"
            }.joinToString(", ")
    )
    sql.append(" ON CONFLICT (reservation_id, key) DO NOTHING")

    val query = createUpdate(sql.toString())
    query.bind("reservationId", reservationId)
    keys.forEachIndexed { index, key ->
        query.bind("key$index", key)
    }
    query.execute()
}

fun Handle.getUnAcknowledgedReservationWarnings(reservationId: Int): List<ReservationWarning> =
    createQuery(
        """
        SELECT *
        FROM reservation_warning
        WHERE reservation_id = :reservationId
        """
    ).bind("reservationId", reservationId)
        .mapTo<ReservationWarning>()
        .list()

fun Handle.setReservationWarningAcknowledged(
    reservationId: Int,
    key: String,
) {
    createUpdate(
        """
        DELETE from reservation_warning
        WHERE reservation_Id = :reservationId AND key = :key
        """
    ).bind("reservationId", reservationId)
        .bind("key", key)
        .execute()
}
