package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.TrailerRow
import fi.espoo.vekkuli.domain.TrailerWithWarnings
import fi.espoo.vekkuli.domain.TrailerWithWarningsRow
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JdbiTrailerRepository(
    private val jdbi: Jdbi
) : TrailerRepository {
    override fun getTrailersForReserver(reserverId: UUID): List<TrailerRow> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT * FROM trailer
                    WHERE reserver_id = :reserverId
                    """.trimIndent()
                ).bind("reserverId", reserverId)
                .mapTo<TrailerRow>()
                .list()
        }

    override fun getTrailer(trailerId: Int): TrailerWithWarnings? =
        jdbi.withHandleUnchecked { handle ->
            val trailer =
                handle
                    .createQuery(
                        """
                        SELECT t.*, ARRAY_AGG(rw.key) AS warnings
                        FROM trailer t 
                        LEFT JOIN reservation_warning rw ON rw.trailer_id = t.id
                        WHERE t.id = :id
                        GROUP BY t.id;
                        """.trimIndent()
                    ).bind("id", trailerId)
                    .mapTo<TrailerWithWarningsRow>()
                    .findOne()
                    .orElse(null)
            trailer?.toTrailerWithWarnings()
        }

    override fun updateTrailer(
        id: Int,
        registrationCode: String?,
        widthCm: Int,
        lengthCm: Int,
        reserverId: UUID
    ): TrailerRow =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    """
                    UPDATE trailer
                    SET
                        registration_code = :registrationCode,
                        reserver_id = :reserverId,
                        width_cm = :widthCm,
                        length_cm = :lengthCm
                    WHERE id = :id
                    RETURNING *
                    """.trimIndent()
                ).bind("registrationCode", registrationCode)
                .bind("reserverId", reserverId)
                .bind("widthCm", widthCm)
                .bind("lengthCm", lengthCm)
                .bind("id", id)
                .executeAndReturnGeneratedKeys()
                .mapTo<TrailerRow>()
                .one()
        }

    override fun insertTrailer(
        reserverId: UUID,
        registrationCode: String,
        widthCm: Int,
        lengthCm: Int,
    ): TrailerRow =
        jdbi.withHandleUnchecked { handle ->
            val id =
                handle
                    .createQuery(
                        """
                        INSERT INTO trailer (registration_code, reserver_id, width_cm, length_cm)
                        VALUES (:registrationCode, :reserverId, :widthCm, :lengthCm)
                        RETURNING id
                        """.trimIndent()
                    ).bind("registrationCode", registrationCode)
                    .bind("reserverId", reserverId)
                    .bind("widthCm", widthCm)
                    .bind("lengthCm", lengthCm)
                    .mapTo<Int>()
                    .one()
            TrailerRow(id, registrationCode, reserverId, widthCm, lengthCm)
        }

    override fun insertTrailerAndAddToReservation(
        reservationId: Int,
        reserverId: UUID,
        registrationCode: String,
        widthCm: Int,
        lengthCm: Int,
    ): TrailerRow =
        jdbi.withHandleUnchecked { handle ->
            val trailer = insertTrailer(reserverId, registrationCode, widthCm, lengthCm)

            handle
                .createUpdate(
                    """
                    UPDATE boat_space_reservation
                    SET trailer_id = :trailerId
                    WHERE id = :reservationId
                    """.trimIndent()
                ).bind("trailerId", trailer.id)
                .bind("reservationId", reservationId)
                .execute()

            trailer
        }

    override fun deleteTrailer(trailerId: Int): Boolean =
        jdbi.withHandle<Boolean, Exception> { handle ->
            handle
                .createUpdate(
                    """
                    DELETE FROM trailer
                    WHERE id = :id
                    """.trimIndent()
                ).bind("id", trailerId)
                .execute()
            true
        }
}
