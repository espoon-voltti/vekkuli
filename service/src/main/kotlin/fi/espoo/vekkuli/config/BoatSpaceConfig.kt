package fi.espoo.vekkuli.config

import fi.espoo.vekkuli.config.DomainConstants.INVOICE_PAYMENT_PERIOD
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.CreationType
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.utils.SecondsRemaining
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.getNextDate
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

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
    const val SESSION_TIME_IN_SECONDS = 40 * 60
    const val BOAT_RESERVATION_ALV_PERCENTAGE = 25.5

    fun paytrailProductCode(product: BoatSpaceType): String {
        val product =
            when (product) {
                BoatSpaceType.Slip, BoatSpaceType.Trailer -> "T1270"
                BoatSpaceType.Winter -> "T1271"
                BoatSpaceType.Storage -> "T1276"
            }

        return "329700-1230329-$product-0-0-0-0-0-0-0-0-0-100"
    }

    fun paytrailDescription(reservation: BoatSpaceReservationDetails): String {
        val creationType =
            when (reservation.creationType) {
                CreationType.New -> ""
                CreationType.Renewal -> ""
                CreationType.Switch -> "Vaihto "
            }
        val typeDescription =
            when (reservation.type) {
                BoatSpaceType.Slip -> "Venepaikka"
                BoatSpaceType.Trailer -> "Traileripaikka"
                BoatSpaceType.Winter -> "Talvipaikka"
                BoatSpaceType.Storage -> "Säilytyspaikka"
            }
        return "$creationType$typeDescription ${reservation.startDate.year} ${reservation.locationName} ${reservation.place}"
    }

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
            BoatSpaceAmenity.Buck -> Pair(null, spaceWidth)
            BoatSpaceAmenity.Trailer -> Pair(null, spaceWidth)
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
            BoatSpaceAmenity.Trailer -> Pair(boatWidth, Int.MAX_VALUE)
            BoatSpaceAmenity.Buck -> Pair(boatWidth, Int.MAX_VALUE)
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
        BoatSpaceAmenity.Buck -> Pair(null, spaceLength)
        BoatSpaceAmenity.Trailer -> Pair(null, spaceLength)
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
            BoatSpaceAmenity.Trailer -> Pair(boatLength, Int.MAX_VALUE)
            BoatSpaceAmenity.Buck -> Pair(boatLength, Int.MAX_VALUE)
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

    // How many max days later reservation expired emails are sent after expiration
    const val MAX_DAYS_BEFORE_RESERVATION_EXPIRED_NOTICE = 30

    const val DAYS_BEFORE_RESERVATION_EXPIRY_NOTICE = 30

    fun getSlipEndDate(
        currentDate: LocalDate,
        validity: ReservationValidity
    ) = when (validity) {
        ReservationValidity.FixedTerm -> getNextDate(currentDate, 12, 31)
        ReservationValidity.Indefinite -> getNextDate(currentDate, 1, 31)
    }

    fun getWinterEndDate(now: LocalDate) = getNextDate(now, 9, 14)

    fun getStorageEndDate(now: LocalDate) = getNextDate(now, 9, 14)

    fun getTrailerEndDate(
        now: LocalDate,
        validity: ReservationValidity
    ) = when (validity) {
        ReservationValidity.FixedTerm -> getNextDate(now, 4, 30)
        ReservationValidity.Indefinite -> getNextDate(now, 4, 30)
    }

    fun getUnfinishedReservationExpirationTime(
        reservationCreated: LocalDateTime,
        currentDate: LocalDateTime
    ): SecondsRemaining {
        val reservationTimePassed = Duration.between(reservationCreated, currentDate).toSeconds()
        val remainingTime = BoatSpaceConfig.SESSION_TIME_IN_SECONDS - reservationTimePassed

        // Check for overflow or underflow before converting to Int. This should never happen without bad input.
        if (remainingTime > Int.MAX_VALUE || remainingTime < Int.MIN_VALUE) {
            throw IllegalArgumentException("Remaining time exceeds the range of Int")
        }
        return SecondsRemaining(remainingTime.toInt())
    }

    fun getInvoiceDueDate(timeProvider: TimeProvider): LocalDate = timeProvider.getCurrentDate().plusDays(INVOICE_PAYMENT_PERIOD.toLong())
}
