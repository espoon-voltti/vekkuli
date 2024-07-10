package fi.espoo.vekkuli.domain

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.util.UUID

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
    val ownership: OwnershipStatus
)

enum class BoatType {
    Unknown,
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
