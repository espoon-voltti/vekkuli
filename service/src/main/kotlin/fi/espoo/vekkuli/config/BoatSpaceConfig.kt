package fi.espoo.vekkuli.config

import fi.espoo.vekkuli.domain.BoatSpaceAmenity

data class Dimensions(
    val width: Int,
    val length: Int
)

enum class ReservationWarningType {
    BoatDimensions,
    BoatOwnership,
    BoatWeight,
}

object BoatSpaceConfig {
    const val SESSION_TIME_IN_SECONDS = 20 * 60
    const val PAYMENT_TIMEOUT = 24 * 60 * 60
    const val BOAT_RESERVATION_ALV_PERCENTAGE = 10.0

    const val MIN_WIDTH_ADJUSTMENT_CM = 40

    const val BUOY_WIDTH_ADJUSTMENT_CM = 40
    const val BUOY_LENGTH_ADJUSTMENT_CM = 100

    const val BEAM_WIDTH_ADJUSTMENT_CM = 40
    const val BEAM_LENGTH_ADJUSTMENT_CM = 100

    const val WALK_BEAM_WIDTH_ADJUSTMENT_CM = 75
    const val WALK_BEAM_LENGTH_ADJUSTMENT_CM = 100

    const val REAR_BUOY_WIDTH_ADJUSTMENT_CM = 50
    const val REAR_BUOY_LENGTH_ADJUSTMENT_CM = 300

    // Boat length after which a buoy is always needed
    const val BOAT_LENGTH_THRESHOLD_CM = 1500

    const val BOAT_WEIGHT_THRESHOLD_KG = 10000

    const val EMAIL_SENDER = "venepaikat@espoo.fi"

    fun getRequiredDimensions(
        amenity: BoatSpaceAmenity,
        boat: Dimensions
    ): Dimensions =
        when (amenity) {
            BoatSpaceAmenity.None -> Dimensions(boat.width + MIN_WIDTH_ADJUSTMENT_CM, boat.length)
            BoatSpaceAmenity.Buoy -> Dimensions(boat.width + BUOY_WIDTH_ADJUSTMENT_CM, boat.length + BUOY_LENGTH_ADJUSTMENT_CM)
            BoatSpaceAmenity.Beam -> Dimensions(boat.width + BEAM_WIDTH_ADJUSTMENT_CM, boat.length + BEAM_LENGTH_ADJUSTMENT_CM)
            BoatSpaceAmenity.WalkBeam ->
                Dimensions(
                    boat.width + WALK_BEAM_WIDTH_ADJUSTMENT_CM,
                    boat.length + WALK_BEAM_LENGTH_ADJUSTMENT_CM
                )
            BoatSpaceAmenity.RearBuoy ->
                Dimensions(
                    boat.width + REAR_BUOY_WIDTH_ADJUSTMENT_CM,
                    boat.length + REAR_BUOY_LENGTH_ADJUSTMENT_CM
                )
        }

    fun doesBoatFit(
        space: Dimensions,
        amenity: BoatSpaceAmenity,
        boat: Dimensions
    ): Boolean {
        // If the boat is longer than the threshold, it always needs a buoy place
        if (boat.length > BOAT_LENGTH_THRESHOLD_CM && amenity != BoatSpaceAmenity.Buoy) {
            return false
        }
        val requiredDimensions = getRequiredDimensions(amenity, boat)
        return requiredDimensions.width <= space.width && requiredDimensions.length <= space.length
    }
}
