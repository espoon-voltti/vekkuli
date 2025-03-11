package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.ReservationWarning
import fi.espoo.vekkuli.service.ReservationWarningRepository
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JdbiReservationWarningRepository(
    private val jdbi: Jdbi
) : ReservationWarningRepository {
    override fun addReservationWarnings(
        id: UUID,
        reservationId: Int,
        boatId: Int?,
        trailerId: Int?,
        invoiceNumber: Int?,
        infoText: String?,
        keys: List<String>,
    ): Unit =
        jdbi.withHandleUnchecked { handle ->
            val batch =
                handle.prepareBatch(
                    """
                    INSERT INTO reservation_warning (id, reservation_id, boat_id, trailer_id, invoice_number, info_text, key) 
                    VALUES (:id, :reservationId, :boatId, :trailerId, :invoiceNumber, :infoText, :key)
                    """.trimIndent()
                )
            for (key in keys) {
                batch
                    .bind("id", id)
                    .bind("reservationId", reservationId)
                    .bind("boatId", boatId)
                    .bind("trailerId", trailerId)
                    .bind("invoiceNumber", invoiceNumber)
                    .bind("infoText", infoText)
                    .bind("key", key)
                    .add()
            }
            batch.execute()
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
            handle
                .createUpdate(
                    """
                DELETE FROM reservation_warning
                WHERE reservation_id = :reservationId AND 
                  (boat_id = :boatIdOrTrailerId OR trailer_id = :boatIdOrTrailerId) AND 
                  key IN (<keys>)
                """
                ).bind("reservationId", reservationId)
                .bind("boatIdOrTrailerId", boatIdOrTrailerId)
                .bindList("keys", keys)
                .execute()
        }

    override fun deleteReservationWarning(id: UUID) {
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    """
                    DELETE FROM reservation_warning
                    WHERE id = :id
                    """
                ).bind("id", id)
                .execute()
        }
    }

    override fun deleteReservationWarningsForReservation(
        reservationId: Int,
        key: String?
    ) {
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle
                    .createUpdate(
                        """
                    DELETE from reservation_warning
                    WHERE reservation_id = :reservationId
                    ${if (key != null) " AND key = :key" else "" }
                    """
                    ).bind("reservationId", reservationId)
            if (key != null) {
                query.bind("key", key)
            }

            query.execute()
        }
    }
}
