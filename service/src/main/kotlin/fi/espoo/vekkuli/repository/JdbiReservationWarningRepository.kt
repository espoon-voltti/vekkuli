package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.ReservationWarning
import fi.espoo.vekkuli.service.ReservationWarningRepository
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository

@Repository
class JdbiReservationWarningRepository(
    private val jdbi: Jdbi
) : ReservationWarningRepository {
    override fun addReservationWarnings(
        reservationId: Int,
        boatId: Int?,
        trailerId: Int?,
        keys: List<String>,
    ): Unit =
        jdbi.withHandleUnchecked { handle ->
            val sql = StringBuilder()

            sql.append("INSERT INTO reservation_warning (reservation_id, boat_id, trailer_id, key) VALUES ")
            sql.append(
                keys
                    .mapIndexed { index, _ ->
                        "(:reservationId, :boatId, :trailerId, :key$index)"
                    }.joinToString(", ")
            )

            val query = handle.createUpdate(sql.toString())
            query.bind("reservationId", reservationId)
            query.bind("boatId", boatId)
            query.bind("trailerId", trailerId)
            keys.forEachIndexed { index, key ->
                query.bind("key$index", key)
            }
            query.execute()
        }

    override fun getWarningsForReservation(reservationId: Int): List<ReservationWarning> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                        SELECT *
                        FROM reservation_warning
                        WHERE reservation_id = :reservationId
                        """
                ).bind("reservationId", reservationId)
                .mapTo<ReservationWarning>()
                .list()
                .groupBy { "${it.reservationId}${it.key}${it.boatId}${it.trailerId}" }
                .map { it.value.first() }
        }

    override fun setReservationWarningsAcknowledged(
        reservationId: Int,
        boatIdOrTrailerId: Int,
        keys: List<String>,
    ): Unit =
        jdbi.withHandleUnchecked { handle ->
            val keysStr = keys.joinToString(", ") { "'$it'" }
            handle
                .createUpdate(
                    """
                    DELETE from reservation_warning
                    WHERE reservation_id = :reservationId AND 
                      (boat_id = :boatIdOrTrailerId OR trailer_id = :boatIdOrTrailerId) AND 
                      key IN ($keysStr)
                    """
                ).bind("reservationId", reservationId)
                .bind("boatIdOrTrailerId", boatIdOrTrailerId)
                .execute()
        }
}
