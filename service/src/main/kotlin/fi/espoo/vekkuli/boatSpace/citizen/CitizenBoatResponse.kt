package fi.espoo.vekkuli.boatSpace.citizen

import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus

data class CitizenBoatResponse(
    val id: Int,
    val registrationCode: String?,
    val name: String?,
    val widthCm: Int,
    val lengthCm: Int,
    val depthCm: Int,
    val weightKg: Int,
    val type: BoatType,
    val otherIdentification: String? = null,
    val extraInformation: String? = null,
    val ownership: OwnershipStatus,
)

fun Boat.toCitizenBoatResponse() =
    CitizenBoatResponse(
        id = id,
        registrationCode = registrationCode,
        name = name,
        widthCm = widthCm,
        lengthCm = lengthCm,
        depthCm = depthCm,
        weightKg = weightKg,
        type = type,
        otherIdentification = otherIdentification,
        extraInformation = extraInformation,
        ownership = ownership,
    )

fun List<Boat>.toCitizenBoatListResponse() =
    map {
        it.toCitizenBoatResponse()
    }
