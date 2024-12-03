package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.Trailer
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import java.util.*

class JdbiTrailerRepository(
    private val jdbi: Jdbi
) : TrailerRepository {
    override fun getTrailersForReserver(reserverId: UUID): List<Trailer> =
        jdbi.withHandle<List<Trailer>, Exception> { handle ->
            handle
                .createQuery(
                    """
                    SELECT * FROM trailer
                    WHERE reserver_id = :reserverId
                    """.trimIndent()
                ).bind("reserverId", reserverId)
                .mapTo<Trailer>()
                .list()
        }

    override fun getTrailer(trailerId: Int): Trailer? =
        jdbi.withHandle<Trailer?, Exception> { handle ->
            handle
                .createQuery(
                    """
                    SELECT * FROM trailer
                    WHERE id = :id
                    """.trimIndent()
                ).bind("id", trailerId)
                .mapTo<Trailer>()
                .findOne()
                .orElse(null)
        }

    override fun updateTrailer(trailer: Trailer): Trailer =
        jdbi.withHandle<Trailer, Exception> { handle ->
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
                    """.trimIndent()
                ).bind("registrationCode", trailer.registrationCode)
                .bind("reserverId", trailer.reserverId)
                .bind("widthCm", trailer.widthCm)
                .bind("lengthCm", trailer.lengthCm)
                .bind("id", trailer.id)
                .execute()
            trailer
        }

    override fun insertTrailer(
        reserverId: UUID,
        registrationCode: String,
        widthCm: Int,
        lengthCm: Int,
        depthCm: Int
    ): Trailer =
        jdbi.withHandle<Trailer, Exception> { handle ->
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
            Trailer(id, registrationCode, reserverId, widthCm, lengthCm)
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
