package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JdbiBoatRepository(
    private val jdbi: Jdbi
) : BoatRepository {
    override fun getBoatsForCitizen(citizenId: UUID): List<Boat> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT * FROM boat
                    WHERE citizen_id = :citizenId
                    ORDER BY id
                    """.trimIndent()
                )
            query.bind("citizenId", citizenId)
            val boats = query.mapTo<Boat>().list()
            boats.map {
                val warningQuery =
                    handle.createQuery(
                        """
                        SELECT key
                        FROM reservation_warning
                        WHERE boat_id = :boatId
                        """.trimIndent()
                    )
                warningQuery.bind("boatId", it.id)
                val warnings = warningQuery.mapTo<String>().list()
                it.copy(
                    warnings = warnings.toSet()
                )
            }
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
            query.bind("registrationCode", boat.registrationCode)
            query.bind("name", boat.name)
            query.bind("widthCm", boat.widthCm)
            query.bind("lengthCm", boat.lengthCm)
            query.bind("depthCm", boat.depthCm)
            query.bind("weightKg", boat.weightKg)
            query.bind("type", boat.type)
            query.bind("otherIdentification", boat.otherIdentification)
            query.bind("extraInformation", boat.extraInformation)
            query.bind("ownership", boat.ownership)
            query.mapTo<Boat>().one()
        }

    override fun insertBoat(
        citizenId: UUID,
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
                    INSERT INTO boat (citizen_id, registration_code, name, width_cm, length_cm, depth_cm, weight_kg, type, other_identification, extra_information, ownership)
                    VALUES (:citizenId, :registrationCode, :name, :widthCm, :lengthCm, :depthCm, :weightKg, :type, :otherIdentification, :extraInformation, :ownership)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("citizenId", citizenId)
            query.bind("registrationCode", registrationCode)
            query.bind("name", name)
            query.bind("widthCm", widthCm)
            query.bind("lengthCm", lengthCm)
            query.bind("depthCm", depthCm)
            query.bind("weightKg", weightKg)
            query.bind("type", type)
            query.bind("otherIdentification", otherIdentification)
            query.bind("extraInformation", extraInformation)
            query.bind("ownership", ownership)
            query.mapTo<Boat>().one()
        }

    override fun deleteBoat(boatId: Int): Boolean =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createUpdate(
                    """
                    DELETE FROM boat
                    WHERE boat.id = :id
                      AND NOT EXISTS (
                          SELECT boat_id
                          FROM boat_space_reservation
                          WHERE boat_space_reservation.boat_id = boat.id
                      );
                    """.trimIndent()
                )
            query.bind("id", boatId)
            query.execute() == 1
        }
}
