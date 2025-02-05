package fi.espoo.vekkuli.boatSpace.citizen

import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.SwitchPolicyService
import fi.espoo.vekkuli.boatSpace.renewal.RenewalPolicyService
import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.config.validateReservationIsActive
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class ExistingReservationResponse(
    val id: Int,
    val boatSpace: BoatSpace,
    val boat: Boat?,
    val created: LocalDateTime,
    val endDate: LocalDate,
    val validity: ReservationValidity,
    val isActive: Boolean,
    val totalPrice: String,
    val vatValue: String,
    val storageType: StorageType?,
    val trailer: Trailer?,
    val canRenew: Boolean,
    val canSwitch: Boolean,
    val status: ReservationStatus,
    val paymentDate: LocalDate?,
    val dueDate: LocalDate?
) {
    data class Boat(
        val id: Int?,
        val name: String,
        val type: BoatType,
        val width: BigDecimal,
        val length: BigDecimal,
        val depth: BigDecimal,
        val weight: Int,
        val registrationNumber: String,
        val hasNoRegistrationNumber: Boolean = false,
        val otherIdentification: String,
        val extraInformation: String? = null,
        val ownership: OwnershipStatus,
    )

    data class BoatSpace(
        val id: Int,
        val type: BoatSpaceType,
        val section: String,
        val placeNumber: Int,
        val amenity: BoatSpaceAmenity,
        val width: BigDecimal,
        val length: BigDecimal,
        val description: String,
        val excludedBoatTypes: List<BoatType>? = null,
        val locationName: String?,
    )

    data class Trailer(
        val id: Int,
        val registrationNumber: String,
        val width: BigDecimal,
        val length: BigDecimal,
    )
}

@Service
class ExistingReservationResponseMapper(
    private val boatService: BoatService,
    private val spaceReservationService: BoatReservationService,
    private val renewalPolicyService: RenewalPolicyService,
    private val switchPolicyService: SwitchPolicyService,
    private val citizenAccessControl: CitizenAccessControl,
    private val timeProvider: TimeProvider
) {
    fun toActiveReservationResponse(boatSpaceReservation: BoatSpaceReservationDetails) = toReservationResponse(boatSpaceReservation, true)

    fun toExpiredReservationResponse(boatSpaceReservation: BoatSpaceReservationDetails) = toReservationResponse(boatSpaceReservation, false)

    fun toReservationResponse(
        boatSpaceReservation: BoatSpaceReservationDetails,
        isActive: Boolean? = null
    ): ExistingReservationResponse {
        val (reserverId) = citizenAccessControl.requireCitizen()
        val reservationWithDependencies =
            spaceReservationService.getReservationWithDependencies(boatSpaceReservation.id) ?: throw NotFound()

        val boatSpace = spaceReservationService.getBoatSpaceRelatedToReservation(boatSpaceReservation.id) ?: throw NotFound()

        val boat = getBoat(reservationWithDependencies)

        val trailer = getTrailer(reservationWithDependencies)

        val canRenew = getCanRenew(boatSpaceReservation.id, reserverId)
        val canSwitch = getCanSwitch(boatSpaceReservation.id, reserverId)
        val isActive =
            if (isActive !== null) {
                isActive
            } else {
                validateReservationIsActive(
                    boatSpaceReservation,
                    timeProvider.getCurrentDateTime()
                )
            }

        return ExistingReservationResponse(
            id = boatSpaceReservation.id,
            boatSpace =
                ExistingReservationResponse.BoatSpace(
                    id = boatSpace.id,
                    type = boatSpace.type,
                    section = boatSpace.section,
                    placeNumber = boatSpace.placeNumber,
                    amenity = boatSpace.amenity,
                    width =
                        fi.espoo.vekkuli.utils
                            .intToDecimal(boatSpace.widthCm),
                    length =
                        fi.espoo.vekkuli.utils
                            .intToDecimal(boatSpace.lengthCm),
                    description = boatSpace.description,
                    excludedBoatTypes = boatSpace.excludedBoatTypes,
                    locationName = boatSpace.locationName
                ),
            boat = formatBoat(boat),
            created = boatSpaceReservation.created,
            endDate = boatSpaceReservation.endDate.toLocalDate(),
            validity = reservationWithDependencies.validity,
            isActive = isActive,
            paymentDate = boatSpaceReservation.paymentDate,
            totalPrice = reservationWithDependencies.priceInEuro,
            vatValue = reservationWithDependencies.vatPriceInEuro,
            storageType = reservationWithDependencies.storageType,
            trailer = formatTrailer(trailer),
            canRenew = canRenew,
            canSwitch = canSwitch,
            status = boatSpaceReservation.status,
            dueDate = boatSpaceReservation.invoiceDueDate
        )
    }

    private fun getBoat(reservation: ReservationWithDependencies): Boat? {
        if (reservation.boatId == null) {
            return null
        }

        return boatService.getBoat(reservation.boatId) ?: throw NotFound()
    }

    private fun formatBoat(boat: Boat?): ExistingReservationResponse.Boat? {
        if (boat == null) {
            return null
        }

        return ExistingReservationResponse.Boat(
            id = boat.id,
            name = boat.name ?: "",
            type = boat.type,
            width =
                fi.espoo.vekkuli.utils
                    .intToDecimal(boat.widthCm),
            length =
                fi.espoo.vekkuli.utils
                    .intToDecimal(boat.lengthCm),
            depth =
                fi.espoo.vekkuli.utils
                    .intToDecimal(boat.depthCm),
            weight = boat.weightKg,
            registrationNumber = boat.registrationCode ?: "",
            hasNoRegistrationNumber = boat.registrationCode == null,
            otherIdentification = boat.otherIdentification ?: "",
            extraInformation = boat.extraInformation,
            ownership = boat.ownership,
        )
    }

    private fun getTrailer(reservation: ReservationWithDependencies): Trailer? {
        if (reservation.trailerId == null) {
            return null
        }
        return spaceReservationService.getTrailer(reservation.trailerId) ?: throw NotFound()
    }

    private fun formatTrailer(trailer: Trailer?): ExistingReservationResponse.Trailer? {
        if (trailer == null) {
            return null
        }
        return ExistingReservationResponse.Trailer(
            id = trailer.id,
            registrationNumber = trailer.registrationCode ?: "",
            width =
                fi.espoo.vekkuli.utils
                    .intToDecimal(trailer.widthCm),
            length =
                fi.espoo.vekkuli.utils
                    .intToDecimal(trailer.lengthCm),
        )
    }

    private fun getCanRenew(
        reservationId: Int,
        reserverId: UUID?
    ): Boolean {
        return if (reserverId != null) {
            renewalPolicyService.citizenCanRenewReservation(
                reservationId,
                reserverId
            ).success
        } else {
            false
        }
    }

    private fun getCanSwitch(
        reservationId: Int,
        reserverId: UUID?
    ): Boolean {
        return if (reserverId != null) {
            switchPolicyService.citizenCanSwitchReservation(
                reservationId,
                reserverId
            ).success
        } else {
            false
        }
    }
}
