package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JdbiBoatRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) : BoatRepository {
    override fun getBoatsForReserver(reserverId: UUID): List<Boat> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT * FROM boat
                    WHERE reserver_id = :reserverId AND deleted_at IS NULL
                    ORDER BY id
                    """.trimIndent()
                )
            query.bind("reserverId", reserverId)
            val boats = query.mapTo<Boat>().list()
            boats.map { boat ->
                val warningQuery =
                    handle.createQuery(
                        """
                        SELECT *
                        FROM reservation_warning
                        WHERE boat_id = :boatId
                        ORDER BY created DESC
                        """.trimIndent()
                    )
                warningQuery.bind("boatId", boat.id)
                val warnings = warningQuery.mapTo<ReservationWarning>().list()
                boat.copy(
                    warnings = warnings.distinctBy { it.key }.toSet()
                )
            }
        }

    override fun getBoatCountForReserver(reserverId: UUID): Int =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery("SELECT COUNT(*) FROM boat WHERE reserver_id = :reserverId AND deleted_at IS NULL")
                .bind("reserverId", reserverId)
                .mapTo<Int>()
                .one()
        }

    override fun getBoatsForReserversOrganizations(reserverId: UUID): Map<String, List<Boat>> =
        jdbi.withHandleUnchecked { handle ->
            val query = """
            WITH reserver_organizations AS (
                SELECT om.organization_id
                FROM organization_member om
                WHERE om.member_id = :reserverId
            )
            SELECT * FROM boat b
            WHERE 
                b.reserver_id IN (
                    SELECT organization_id 
                    FROM reserver_organizations
                )
                AND
                deleted_at IS NULL;
        """
            handle
                .createQuery(query)
                .bind("reserverId", reserverId)
                .mapTo<Boat>()
                .list()
                .groupBy { it.reserverId.toString() }
        }

    override fun getBoat(boatId: Int): Boat? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT * FROM boat
                    WHERE id = :id
                    """.trimIndent()
                )
            query.bind("id", boatId)
            query.mapTo<Boat>().findOne().orElse(null)
        }

    private fun hyphenToEmpty(value: String?) =
        when {
            value != null && value.trim() == "-"
            -> ""
            else -> value
        }

    override fun updateBoat(boat: Boat): Boat =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE boat
                    SET registration_code = :registrationCode, 
                        name = :name, 
                        width_cm = :widthCm, 
                        length_cm = :lengthCm, 
                        depth_cm = :depthCm, 
                        weight_kg = :weightKg, 
                        type = :type, 
                        other_identification = :otherIdentification, 
                        extra_information = :extraInformation, 
                        ownership = :ownership
                    WHERE id = :id
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("id", boat.id)
            query.bind("registrationCode", hyphenToEmpty(boat.registrationCode))
            query.bind("name", hyphenToEmpty(boat.name))
            query.bind("widthCm", boat.widthCm)
            query.bind("lengthCm", boat.lengthCm)
            query.bind("depthCm", boat.depthCm)
            query.bind("weightKg", boat.weightKg)
            query.bind("type", boat.type)
            query.bind("otherIdentification", hyphenToEmpty(boat.otherIdentification))
            query.bind("extraInformation", hyphenToEmpty(boat.extraInformation))
            query.bind("ownership", boat.ownership)
            query.mapTo<Boat>().one()
        }

    override fun insertBoat(
        reserverId: UUID,
        registrationCode: String,
        name: String,
        widthCm: Int,
        lengthCm: Int,
        depthCm: Int,
        weightKg: Int,
        type: BoatType,
        otherIdentification: String,
        extraInformation: String,
        ownership: OwnershipStatus,
    ): Boat =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    INSERT INTO boat (reserver_id, registration_code, name, width_cm, length_cm, depth_cm, weight_kg, type, other_identification, extra_information, ownership)
                    VALUES (:reserverId, :registrationCode, :name, :widthCm, :lengthCm, :depthCm, :weightKg, :type, :otherIdentification, :extraInformation, :ownership)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("reserverId", reserverId)
            query.bind("registrationCode", hyphenToEmpty(registrationCode))
            query.bind("name", hyphenToEmpty(name))
            query.bind("widthCm", widthCm)
            query.bind("lengthCm", lengthCm)
            query.bind("depthCm", depthCm)
            query.bind("weightKg", weightKg)
            query.bind("type", type)
            query.bind("otherIdentification", hyphenToEmpty(otherIdentification))
            query.bind("extraInformation", hyphenToEmpty(extraInformation))
            query.bind("ownership", ownership)
            query.mapTo<Boat>().one()
        }

    override fun deleteBoat(boatId: Int): Boolean =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createUpdate(
                    """
                    UPDATE boat
                    SET deleted_at = :timestamp
                    WHERE boat.id = :id
                      AND NOT EXISTS (
                          SELECT 1
                          FROM boat_space_reservation
                          WHERE 
                            boat_space_reservation.boat_id = boat.id
                            AND
                            boat_space_reservation.end_date::date > :timestamp::date
                      );
                    """.trimIndent()
                )
            query.bind("id", boatId)
            query.bind("timestamp", timeProvider.getCurrentDateTime())
            query.execute() == 1
        }

    override fun getBoatsByRegistrationCode(registrationCode: String): List<Boat> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT * FROM boat
                    WHERE lower(registration_code) = :registrationCode
                    AND deleted_at IS NULL
                    """.trimIndent()
                )
            query.bind("registrationCode", registrationCode.lowercase())
            query.mapTo<Boat>().list()
        }
}
