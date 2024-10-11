package fi.espoo.vekkuli.config

import fi.espoo.vekkuli.domain.BoatSpaceAmenity

data class Dimensions(
    val width: Int,
    val length: Int
)

enum class ReservationWarningType {
    BoatWidth,
    BoatLength,
    BoatFutureOwner,
    BoatCoOwner,
    BoatWeight,
    BoatType
}

object BoatSpaceConfig {
    const val SESSION_TIME_IN_SECONDS = 20 * 60
    const val PAYMENT_TIMEOUT = 24 * 60 * 60
    const val BOAT_RESERVATION_ALV_PERCENTAGE = 25.5

    const val MIN_WIDTH_ADJUSTMENT_CM = 40

    // No restrictions for buoys. We use large negative values to
    // make sure the boat always fits
    const val BUOY_WIDTH_ADJUSTMENT_CM = -100000
    const val BUOY_LENGTH_ADJUSTMENT_CM = -100000

    const val BEAM_WIDTH_ADJUSTMENT_CM = 40
    const val BEAM_LENGTH_ADJUSTMENT_CM = -100

    const val WALK_BEAM_WIDTH_ADJUSTMENT_CM = 75
    const val WALK_BEAM_LENGTH_ADJUSTMENT_CM = -100

    const val REAR_BUOY_WIDTH_ADJUSTMENT_CM = 50
    const val REAR_BUOY_LENGTH_ADJUSTMENT_CM = 300

    // Boat length after which a buoy is always needed
    const val BOAT_LENGTH_THRESHOLD_CM = 1500

    const val BOAT_WEIGHT_THRESHOLD_KG = 15000

    fun getWidthLimitsForBoat(
        spaceWidth: Int,
        amenity: BoatSpaceAmenity
    ): Pair<Int?, Int?> =
        when (amenity) {
            BoatSpaceAmenity.Buoy -> Pair(null, null)
            BoatSpaceAmenity.RearBuoy -> Pair(null, spaceWidth - 50)
            BoatSpaceAmenity.Beam -> Pair(spaceWidth - 100, spaceWidth - 40)
            BoatSpaceAmenity.WalkBeam -> Pair(spaceWidth - 100, spaceWidth - 75)
            BoatSpaceAmenity.None -> Pair(null, null)
        }

    fun getWidthLimitsForBoatSpace(
        boatWidth: Int?,
        amenity: BoatSpaceAmenity
    ): Pair<Int, Int> {
        if (boatWidth == null) {
            return Pair(0, Int.MAX_VALUE)
        }
        return when (amenity) {
            BoatSpaceAmenity.Buoy -> Pair(0, Int.MAX_VALUE) // No width limits for Buoy
            BoatSpaceAmenity.RearBuoy -> Pair(0, boatWidth + 50) // Max width is boat width + 50 cm
            BoatSpaceAmenity.Beam -> Pair(boatWidth + 100, boatWidth + 40) // Min width is +100 cm, max width is +40 cm
            BoatSpaceAmenity.WalkBeam ->
                Pair(
                    boatWidth + 100,
                    boatWidth + 75
                ) // Min width is +100 cm, max width is +75 cm
            BoatSpaceAmenity.None -> Pair(0, Int.MAX_VALUE) // No specific limits
        }
    }

    fun getLengthLimitsForBoat(
        spaceLength: Int,
        amenity: BoatSpaceAmenity
    ) = when (amenity) {
        BoatSpaceAmenity.Buoy -> Pair(null, null)
        BoatSpaceAmenity.RearBuoy -> Pair(null, spaceLength - 300)
        BoatSpaceAmenity.Beam -> Pair(spaceLength - 100, spaceLength + 130)
        BoatSpaceAmenity.WalkBeam -> Pair(spaceLength - 150, spaceLength + 130)
        BoatSpaceAmenity.None -> Pair(null, null)
    }

    fun getLengthLimitsForBoatSpace(
        boatLength: Int?,
        amenity: BoatSpaceAmenity
    ): Pair<Int, Int> {
        if (boatLength == null) {
            return Pair(0, Int.MAX_VALUE)
        }
        return when (amenity) {
            BoatSpaceAmenity.Buoy -> Pair(0, Int.MAX_VALUE)
            BoatSpaceAmenity.RearBuoy -> Pair(0, boatLength + 300)
            BoatSpaceAmenity.Beam -> Pair(boatLength + 100, boatLength - 130)
            BoatSpaceAmenity.WalkBeam -> Pair(boatLength + 150, boatLength - 130)
            BoatSpaceAmenity.None -> Pair(0, Int.MAX_VALUE)
        }
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
        return isWidthOk(space, amenity, boat) && isLengthOk(space, amenity, boat)
    }

    fun isWidthOk(
        space: Dimensions,
        amenity: BoatSpaceAmenity,
        boat: Dimensions
    ): Boolean {
        val (minWidth, maxWidth) = getWidthLimitsForBoat(space.width, amenity)
        return (minWidth == null || boat.width >= minWidth) && (maxWidth == null || boat.width <= maxWidth)
    }

    fun isLengthOk(
        space: Dimensions,
        amenity: BoatSpaceAmenity,
        boat: Dimensions
    ): Boolean {
        if (boat.length > BOAT_LENGTH_THRESHOLD_CM && amenity != BoatSpaceAmenity.Buoy) {
            return false
        }
        val (minLength, maxLength) = getLengthLimitsForBoat(space.length, amenity)
        return (minLength == null || boat.length >= minLength) && (maxLength == null || boat.length <= maxLength)
    }
}
