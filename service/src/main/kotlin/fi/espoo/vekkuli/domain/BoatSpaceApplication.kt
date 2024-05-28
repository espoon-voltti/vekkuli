package fi.espoo.vekkuli.domain

import java.time.LocalDateTime

enum class BoatType {
    Rowboat,
    OutboardMotor,
    InboardMotor,
    Sailboat,
    JetSki
}

data class BoatSpaceApplication(
    val createdAt: LocalDateTime,
    val type: BoatSpaceType,
    val boatType: BoatType,
    val amenity: BoatSpaceAmenity,
    val boatWidthCm: Int,
    val boatLengthCm: Int,
    val boatWeightKg: Int,
    val boatRegistrationCode: String,
    val information: String,
    )