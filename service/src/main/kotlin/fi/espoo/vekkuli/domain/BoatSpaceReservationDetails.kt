package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReason
import fi.espoo.vekkuli.utils.centsToEuro
import fi.espoo.vekkuli.utils.cmToM
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
    val registrationCode: String?,
    val boatOwnership: OwnershipStatus?,
    val boatRegistrationCode: String?,
    val boatId: Int,
    val boatName: String?,
    val boatWidthCm: Int,
    val boatLengthCm: Int,
    val boatWeightKg: Int,
    val boatDepthCm: Int,
    val boatType: BoatType,
    val boatOtherIdentification: String?,
    val boatExtraInformation: String?,
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
    val boatLengthInM: Double
        get() = boatLengthCm.cmToM()
    val boatWidthInM: Double
        get() = boatWidthCm.cmToM()
    val boatDepthInM: Double
        get() = boatDepthCm.cmToM()
    val boatSpaceLengthInM: Double
        get() = boatSpaceLengthCm.cmToM()
    val boatSpaceWidthInM: Double
        get() = boatSpaceWidthCm.cmToM()
    val priceInEuro: Double
        get() = priceCents.centsToEuro()
    val vatPriceInEuro: Double
        get() = vatCents.centsToEuro()
    val priceWithoutVatInEuro: Double
        get() = netPriceCents.centsToEuro()

    fun hasWarning(warning: String): Boolean = warnings.contains(warning)

    fun hasAnyWarnings(): Boolean = warnings.isNotEmpty()
}
