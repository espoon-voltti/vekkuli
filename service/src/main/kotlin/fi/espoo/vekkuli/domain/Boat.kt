package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.utils.cmToM
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.jdbi.v3.core.kotlin.mapTo
import java.util.*

data class Boat(
    val id: Int,
    val registrationCode: String?,
    val citizenId: UUID,
    val name: String?,
    val widthCm: Int,
    val lengthCm: Int,
    val depthCm: Int,
    val weightKg: Int,
    val type: BoatType,
    val otherIdentification: String?,
    val extraInformation: String?,
    val ownership: OwnershipStatus,
    val displayName: String? = null
) {
    fun updateBoatDisplayName(messageUtil: MessageUtil): Boat =
        this.copy(
            displayName =
                when {
                    !name.isNullOrBlank() -> name
                    !registrationCode.isNullOrBlank() -> registrationCode
                    else -> {
                        messageUtil.getMessage(
                            "boatApplication.boatTypeOption.$type"
                        ) + " " + widthCm.cmToM() + " x " + lengthCm.cmToM() + " m"
                    }
                }
        )
}

enum class BoatType {
    Rowboat,
    OutboardMotor,
    InboardMotor,
    Sailboat,
    JetSki
}

enum class OwnershipStatus {
    Owner,
    User,
    CoOwner,
    FutureOwner
}

fun getBoatsForCitizen(
    citizenId: UUID,
    jdbi: Jdbi
): List<Boat> {
    return jdbi.inTransactionUnchecked {
        it.getBoatsForCitizen(citizenId)
    }
}

fun Handle.getBoatsForCitizen(citizenId: UUID): List<Boat> {
    val query =
        createQuery(
            """
            SELECT * FROM boat
            WHERE citizen_id = :citizenId
            ORDER BY id
            """.trimIndent()
        )
    query.bind("citizenId", citizenId)
    return query.mapTo<Boat>().list()
}

fun Handle.getBoat(boatId: Int): Boat? {
    val query =
        createQuery(
            """
            SELECT * FROM boat
            WHERE id = :id
            """.trimIndent()
        )
    query.bind("id", boatId)
    return query.mapTo<Boat>().findOne().orElse(null)
}

fun updateBoat(
    boat: Boat,
    jdbi: Jdbi
): Boat {
    return jdbi.inTransactionUnchecked {
        it.updateBoat(boat)
    }
}

fun Handle.updateBoat(boat: Boat): Boat {
    val query =
        createQuery(
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
    return query.mapTo<Boat>().one()
}

fun Handle.insertBoat(
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
): Boat {
    val query =
        createQuery(
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
    return query.mapTo<Boat>().one()
}
