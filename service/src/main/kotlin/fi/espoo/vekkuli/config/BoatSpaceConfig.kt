package fi.espoo.vekkuli.config

import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.ReservationValidity
import java.time.LocalDate
import java.time.Month

data class Dimensions(
    val width: Int?,
    val length: Int?
)

enum class ReservationWarningType {
    BoatWidth,
    BoatLength,
    BoatFutureOwner,
    BoatCoOwner,
    BoatWeight,
    BoatType,
    TrailerWidth,
    TrailerLength,
}

object BoatSpaceConfig {
    const val SESSION_TIME_IN_SECONDS = 20 * 60
    const val BOAT_RESERVATION_ALV_PERCENTAGE = 25.5

    const val BEAM_MAX_WIDTH_ADJUSTMENT_CM = 40

    const val WALK_BEAM_MAX_WIDTH_ADJUSTMENT_CM = 75
    const val WALK_BEAM_MIN_WIDTH_ADJUSTMENT_CM = 100

    const val REAR_BUOY_WIDTH_ADJUSTMENT_CM = 50

    // Boat length after which a buoy is always needed
    const val BOAT_LENGTH_THRESHOLD_CM = 1500

    const val BOAT_WEIGHT_THRESHOLD_KG = 15000

    val winterStorageLocations = listOf(3, 4, 6)

    fun getWidthLimitsForBoat(
        spaceWidth: Int,
        amenity: BoatSpaceAmenity
    ): Pair<Int?, Int?> =
        when (amenity) {
            BoatSpaceAmenity.Buoy -> Pair(null, null)
            BoatSpaceAmenity.RearBuoy -> Pair(null, spaceWidth - REAR_BUOY_WIDTH_ADJUSTMENT_CM)
            BoatSpaceAmenity.Beam -> Pair(null, spaceWidth - BEAM_MAX_WIDTH_ADJUSTMENT_CM)
            BoatSpaceAmenity.WalkBeam ->
                Pair(
                    spaceWidth - WALK_BEAM_MIN_WIDTH_ADJUSTMENT_CM,
                    spaceWidth - WALK_BEAM_MAX_WIDTH_ADJUSTMENT_CM
                )

            BoatSpaceAmenity.None -> Pair(null, spaceWidth)
            else -> Pair(null, null)
        }

    fun getWidthLimitsForBoatSpace(
        boatWidth: Int?,
        amenity: BoatSpaceAmenity
    ): Pair<Int, Int> {
        if (boatWidth == null) {
            return Pair(0, Int.MAX_VALUE)
        }
        return when (amenity) {
            BoatSpaceAmenity.Buoy -> Pair(0, Int.MAX_VALUE)
            BoatSpaceAmenity.RearBuoy -> Pair(boatWidth + REAR_BUOY_WIDTH_ADJUSTMENT_CM, Int.MAX_VALUE)
            BoatSpaceAmenity.Beam -> Pair(boatWidth + BEAM_MAX_WIDTH_ADJUSTMENT_CM, Int.MAX_VALUE)
            BoatSpaceAmenity.WalkBeam ->
                Pair(
                    boatWidth + WALK_BEAM_MAX_WIDTH_ADJUSTMENT_CM,
                    boatWidth + WALK_BEAM_MIN_WIDTH_ADJUSTMENT_CM
                )

            BoatSpaceAmenity.None -> Pair(boatWidth, Int.MAX_VALUE)
            else -> Pair(0, Int.MAX_VALUE)
        }
    }

    const val REAR_BUYO_MAX_LENGTH_ADJUSTMENT_CM = 300
    const val BEAM_MIN_LENGTH_ADJUSTMENT_CM = 100
    const val BEAM_MAX_LENGTH_ADJUSTMENT_CM = 130

    const val WALK_BEAM_MIN_LENGTH_ADJUSTMENT_CM = 150
    const val WALK_BEAM_MAX_LENGTH_ADJUSTMENT_CM = 130

    fun getLengthLimitsForBoat(
        spaceLength: Int,
        amenity: BoatSpaceAmenity
    ) = when (amenity) {
        BoatSpaceAmenity.Buoy -> Pair(null, null)
        BoatSpaceAmenity.RearBuoy -> Pair(null, spaceLength - REAR_BUYO_MAX_LENGTH_ADJUSTMENT_CM)
        BoatSpaceAmenity.Beam -> Pair(null, spaceLength + BEAM_MAX_LENGTH_ADJUSTMENT_CM)
        BoatSpaceAmenity.WalkBeam ->
            Pair(
                spaceLength - WALK_BEAM_MIN_LENGTH_ADJUSTMENT_CM,
                spaceLength + WALK_BEAM_MAX_LENGTH_ADJUSTMENT_CM
            )

        BoatSpaceAmenity.None -> Pair(null, spaceLength)
        else -> Pair(null, null)
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
            BoatSpaceAmenity.RearBuoy -> Pair(boatLength + REAR_BUYO_MAX_LENGTH_ADJUSTMENT_CM, Int.MAX_VALUE)
            BoatSpaceAmenity.Beam -> Pair(boatLength - BEAM_MAX_LENGTH_ADJUSTMENT_CM, Int.MAX_VALUE)
            BoatSpaceAmenity.WalkBeam ->
                Pair(
                    boatLength - WALK_BEAM_MAX_LENGTH_ADJUSTMENT_CM,
                    boatLength + WALK_BEAM_MIN_LENGTH_ADJUSTMENT_CM
                )

            BoatSpaceAmenity.None -> Pair(boatLength, Int.MAX_VALUE)
            else -> Pair(0, Int.MAX_VALUE)
        }
    }

    fun doesBoatFit(
        space: Dimensions,
        amenity: BoatSpaceAmenity,
        boat: Dimensions
    ): Boolean {
        // If the boat is longer than the threshold, it always needs a buoy place
        if (boat.length != null && boat.length > BOAT_LENGTH_THRESHOLD_CM && amenity != BoatSpaceAmenity.Buoy) {
            return false
        }
        return isWidthOk(space, amenity, boat) && isLengthOk(space, amenity, boat)
    }

    fun isWidthOk(
        space: Dimensions,
        amenity: BoatSpaceAmenity,
        boat: Dimensions
    ): Boolean {
        val (minWidth, maxWidth) = getWidthLimitsForBoat(space.width ?: 0, amenity)
        return boat.width == null || ((minWidth == null || boat.width >= minWidth) && (maxWidth == null || boat.width <= maxWidth))
    }

    fun isLengthOk(
        space: Dimensions,
        amenity: BoatSpaceAmenity,
        boat: Dimensions
    ): Boolean {
        if (boat.length != null && boat.length > BOAT_LENGTH_THRESHOLD_CM && amenity != BoatSpaceAmenity.Buoy) {
            return false
        }
        val (minLength, maxLength) = getLengthLimitsForBoat(space.length ?: 0, amenity)
        return boat.length == null || ((minLength == null || boat.length >= minLength) && (maxLength == null || boat.length <= maxLength))
    }

    const val DAYS_BEFORE_RESERVATION_EXPIRY_NOTICE = 30

    fun getSlipEndDate(
        year: Int,
        validity: ReservationValidity
    ) = when (validity) {
        ReservationValidity.FixedTerm -> LocalDate.of(year, Month.DECEMBER, 31)
        ReservationValidity.Indefinite -> LocalDate.of(year + 1, Month.JANUARY, 31)
    }

    fun getWinterEndDate(year: Int) = LocalDate.of(year + 1, Month.AUGUST, 31)

    fun getStorageEndDate(
        year: Int,
        validity: ReservationValidity
    ) = LocalDate.of(year + 1, Month.SEPTEMBER, 14)

    fun getTrailerEndDate(
        year: Int,
        validity: ReservationValidity
    ) = when (validity) {
        ReservationValidity.FixedTerm -> LocalDate.of(year + 1, Month.APRIL, 30)
        ReservationValidity.Indefinite -> LocalDate.of(year + 1, Month.APRIL, 30)
    }
}
