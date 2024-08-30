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
        boatId: Int,
        keys: List<String>,
    ): Unit =
        jdbi.withHandleUnchecked { handle ->
            val sql = StringBuilder()

            sql.append("INSERT INTO reservation_warning (reservation_id, boat_id, key) VALUES ")
            sql.append(
                keys
                    .mapIndexed { index, _ ->
                        "(:reservationId, :boatId, :key$index)"
                    }.joinToString(", ")
            )
            sql.append(" ON CONFLICT (reservation_id, boat_id, key) DO NOTHING")

            val query = handle.createUpdate(sql.toString())
            query.bind("reservationId", reservationId)
            query.bind("boatId", boatId)
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
        }

    override fun getWarningsForBoat(boatId: Int): List<ReservationWarning> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
        SELECT *
        FROM reservation_warning
        WHERE boat_id = :boatId
        """
                ).bind("boatId", boatId)
                .mapTo<ReservationWarning>()
                .list()
        }

    override fun setReservationWarningAcknowledged(
        reservationId: Int,
        boatId: Int,
        key: String,
    ): Unit =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    """
        DELETE from reservation_warning
        WHERE reservation_id = :reservationId AND boat_id = :boatId AND key = :key
        """
                ).bind("reservationId", reservationId)
                .bind("boatId", boatId,)
                .bind("key", key)
                .execute()
        }
}
