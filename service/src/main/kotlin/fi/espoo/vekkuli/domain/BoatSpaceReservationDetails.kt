package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReason
import fi.espoo.vekkuli.utils.discountedPriceInCents
import fi.espoo.vekkuli.utils.formatInt
import fi.espoo.vekkuli.utils.intToDecimal
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class BoatSpaceReservationDetails(
    val id: Int,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val priceCents: Int,
    val vatCents: Int,
    val netPriceCents: Int,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: ReservationStatus,
    val terminationReason: ReservationTerminationReason?,
    val terminationComment: String?,
    val terminationTimestamp: LocalDateTime?,
    val reserverType: ReserverType,
    val reserverId: UUID,
    val actingCitizenId: UUID?,
    val name: String,
    val email: String,
    val phone: String,
    val streetAddress: String?,
    val postalCode: String?,
    val municipalityCode: Int,
    val municipalityName: String,
    val type: BoatSpaceType,
    val place: String,
    val locationName: String,
    val storageType: StorageType?,
    val boat: Boat?,
    val trailer: Trailer?,
    val boatSpaceLengthCm: Int,
    val boatSpaceWidthCm: Int,
    val amenity: BoatSpaceAmenity,
    val validity: ReservationValidity,
    val excludedBoatTypes: List<BoatType>?,
    val canSwitch: Boolean = false,
    val canRenew: Boolean = false,
    val originalReservationId: Int? = null,
    val paymentDate: LocalDate?,
    val paymentId: UUID?,
    val paymentReference: String?,
    val paymentType: PaymentType?,
    val invoiceDueDate: LocalDate?,
    val creationType: CreationType,
    val discountPercentage: Int,
) {
    val boatSpaceLengthInM: BigDecimal
        get() = intToDecimal(boatSpaceLengthCm)
    val boatSpaceWidthInM: BigDecimal
        get() = intToDecimal(boatSpaceWidthCm)
    val priceInEuro: String
        get() = formatInt(priceCents)
    val vatPriceInEuro: String
        get() = formatInt(vatCents)
    val priceWithoutVatInEuro: String
        get() = formatInt(netPriceCents)
}

fun BoatSpaceReservationDetails.toBoatSpaceReservation() =
    BoatSpaceReservation(
        id = id,
        boatSpaceId = boatSpaceId,
        startDate = startDate,
        endDate = endDate,
        created = created,
        updated = updated,
        status = status,
        actingCitizenId = actingCitizenId,
        reserverId = reserverId,
        validity = validity,
        paymentDate = paymentDate,
        creationType = creationType,
    )
