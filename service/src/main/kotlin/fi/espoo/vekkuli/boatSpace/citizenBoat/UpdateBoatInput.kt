package fi.espoo.vekkuli.boatSpace.citizenBoat

import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.utils.decimalToInt
import java.math.BigDecimal
import java.util.UUID

data class UpdateBoatInput(
    val name: String,
    val type: BoatType,
    val width: BigDecimal,
    val length: BigDecimal,
    val depth: BigDecimal,
    val weight: Int,
    val registrationNumber: String,
    val otherIdentification: String,
    val extraInformation: String? = null,
    val ownership: OwnershipStatus,
)

fun UpdateBoatInput.toBoatInput(
    boatId: Int,
    reserverId: UUID
): Boat =
    Boat(
        id = boatId,
        reserverId = reserverId,
        name = name,
        type = type,
        widthCm = decimalToInt(width),
        lengthCm = decimalToInt(length),
        depthCm = decimalToInt(depth),
        weightKg = weight,
        registrationCode = registrationNumber,
        otherIdentification = otherIdentification,
        extraInformation = extraInformation,
        ownership = ownership,
    )
