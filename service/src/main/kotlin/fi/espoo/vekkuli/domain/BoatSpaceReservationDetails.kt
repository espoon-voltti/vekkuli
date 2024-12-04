package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReason
import fi.espoo.vekkuli.utils.centsToEuro
import fi.espoo.vekkuli.utils.cmToM
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class BoatSpaceReservationDetails(
    val id: Int,
    val created: LocalDateTime,
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
    val boat: Boat?,
    val trailer: Trailer?,
    val boatSpaceLengthCm: Int,
    val boatSpaceWidthCm: Int,
    val amenity: BoatSpaceAmenity,
    val validity: ReservationValidity,
    val warnings: Set<String> = emptySet(),
    val excludedBoatTypes: List<BoatType>?,
    val canSwitch: Boolean = false,
    val canRenew: Boolean = false,
    val renewedFromId: Int? = null,
    val paymentDate: LocalDate?,
    val paymentId: UUID?,
) {
    val boatSpaceLengthInM: BigDecimal
        get() = boatSpaceLengthCm.cmToM()
    val boatSpaceWidthInM: BigDecimal
        get() = boatSpaceWidthCm.cmToM()
    val priceInEuro: String
        get() = priceCents.centsToEuro()
    val vatPriceInEuro: String
        get() = vatCents.centsToEuro()
    val priceWithoutVatInEuro: String
        get() = netPriceCents.centsToEuro()
}
