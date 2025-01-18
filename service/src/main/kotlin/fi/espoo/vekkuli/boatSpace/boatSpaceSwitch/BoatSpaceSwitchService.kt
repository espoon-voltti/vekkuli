package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.FillReservationInformationInput
import fi.espoo.vekkuli.boatSpace.renewal.ModifyReservationInput
import fi.espoo.vekkuli.boatSpace.renewal.RenewalReservationForApplicationForm
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationFormService
import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Conflict
import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.service.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

data class BoatSpaceSwitchViewParams(
    val reservation: RenewalReservationForApplicationForm,
    val boats: List<Boat>,
    val citizen: CitizenWithDetails? = null,
    val input: ModifyReservationInput,
    val userType: UserType,
)

@Service
class BoatSpaceSwitchService(
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val organizationService: OrganizationService,
    private val seasonalService: SeasonalService,
    private val reserverService: ReserverService,
    private val boatReservationService: BoatReservationService,
    private val boatSpaceSwitchRepository: BoatSpaceSwitchRepository,
    private val reservationService: ReservationFormService,
    private val boatSpaceRepository: BoatSpaceRepository,
    private val citizenAccessControl: ContextCitizenAccessControl,
) {
    fun getOrCreateSwitchReservationForCitizen(
        reserverId: UUID,
        originalReservationId: Int,
        boatSpaceId: Int
    ): ReservationWithDependencies {
        val reserver = reserverService.getReserverById(reserverId) ?: throw IllegalArgumentException("Reserver not found")
        val original = boatSpaceSwitchRepository.getSwitchReservationForCitizen(reserverId, originalReservationId)
        if (original != null) return original

        val originalReservation =
            createSwitchReservation(
                originalReservationId,
                reserverId,
                UserType.CITIZEN,
                reserver.isEspooCitizen(),
                boatSpaceId
            )
                ?: throw IllegalStateException("Reservation not found")
        return originalReservation
    }

    @Transactional
    fun startReservation(
        spaceId: Int,
        reservationId: Int
    ): ReservationWithDependencies {
        val (citizenId) = citizenAccessControl.requireCitizen()
        boatReservationService.getBoatSpaceReservation(reservationId)
            ?: throw NotFound("Reservation not found")
        // @TODO - Make sure the citizen can switch the reservation
        val result = getOrCreateSwitchReservationForCitizen(citizenId, reservationId, spaceId)
        return result
    }

    @Transactional
    fun fillReservationInformation(
        reservationId: Int,
        input: FillReservationInformationInput
    ): ReservationWithDependencies {
        val citizen = citizenAccessControl.requireCitizen()
        // @TODO - Make sure the citizen can fill the reservation
        val result = processSwitchInformation(citizen.id, input, reservationId)
        return result
    }

    fun processSwitchInformation(
        citizenId: UUID,
        input: FillReservationInformationInput,
        reservationId: Int,
    ): ReservationWithDependencies {
        // val priceDifference = getPriceDifference(reservationId)
        val reservation =
            boatReservationService.getReservationWithReserver(reservationId)
                ?: throw NotFound("Reservation not found")
        if (reservation.originalReservationId == null) {
            throw BadRequest("Original reservation not found")
        }
        if (reservation.reserverId == null) {
            throw UnauthorizedException()
        }
        updateReserver(reservation.reserverType, reservation.reserverId, input)

        val priceDifference = getPriceDifference(reservationId)
        // when there is nothing to pay, reservation can be set as completed
        reservationService.processBoatSpaceReservation(
            reserverId = reservation.reserverId,
            reservationService.buildReserveBoatSpaceInput(reservationId, input),
            if (priceDifference <= 0) ReservationStatus.Confirmed else ReservationStatus.Payment,
            reservation.validity,
            reservation.startDate,
            reservation.endDate
        )
        if (priceDifference <= 0) {
            // mark the original reservation as ended if the payment is skipped
            boatReservationService.markReservationEnded(reservation.originalReservationId)
        }
        return reservation
    }

    fun updateReserver(
        reserverType: ReserverType?,
        reserverId: UUID,
        input: FillReservationInformationInput,
    ) {
        if (reserverType == ReserverType.Organization) {
            organizationService.updateOrganization(
                UpdateOrganizationParams(
                    id = reserverId,
                    phone = input.organization?.phone,
                    email = input.organization?.email
                )
            )
        } else {
            reserverService.updateCitizen(
                UpdateCitizenParams(
                    id = reserverId,
                    phone = input.citizen.phone,
                    email = input.citizen.email,
                )
            )
        }
    }

    fun isSwitchedReservation(reservation: BoatSpaceReservationDetails): Boolean = reservation.creationType == CreationType.Switch

    // Returns the price difference between the original reservation and the new reservation
    fun getPriceDifference(reservationId: Int): Int {
        val reservation =
            boatReservationService.getBoatSpaceReservation(reservationId)
                ?: throw BadRequest("Reservation not found")
        if (reservation.originalReservationId == null) {
            return 0
        }
        val originalReservationPrice =
            boatReservationService
                .getBoatSpaceReservation(reservation.originalReservationId)
                ?.priceCents

        val priceDifference = calculatePriceDifference(originalReservationPrice ?: reservation.priceCents, reservation.priceCents)
        return priceDifference
    }

    fun calculatePriceDifference(
        originalPriceCents: Int,
        newPriceCents: Int
    ): Int = newPriceCents - originalPriceCents

    fun createSwitchReservation(
        originalReservationId: Int,
        userId: UUID,
        userType: UserType,
        isEspooCitizen: Boolean,
        boatSpaceId: Int
    ): ReservationWithDependencies? {
        val originalReservation =
            boatSpaceReservationRepo.getBoatSpaceReservation(originalReservationId)
                ?: throw BadRequest("Reservation to switch not found")

        if (seasonalService.isBoatSpaceReserved(boatSpaceId)) {
            throw BadRequest("Boat space is already reserved")
        }
        val boatSpace =
            boatSpaceRepository.getBoatSpace(boatSpaceId)
                ?: throw BadRequest("Boat space not found")
        if (originalReservation.type !== boatSpace.type) {
            throw BadRequest("Boat space type does not match")
        }

        val reservationResult =
            seasonalService
                .canSwitchReservation(
                    originalReservation.type,
                    originalReservation.startDate,
                    originalReservation.endDate,
                    originalReservation.validity,
                    isEspooCitizen
                )
        if (!reservationResult.success
        ) {
            throw Conflict("Reservation cannot be renewed")
        }
        val reservationResultSuccessData = (reservationResult as ReservationResult.Success).data

        val newId =
            boatSpaceSwitchRepository.createSwitchRow(
                originalReservationId,
                userType,
                userId,
                boatSpaceId,
                reservationResultSuccessData.endDate,
                reservationResultSuccessData.reservationValidity
            )
        return boatSpaceReservationRepo.getReservationWithReserverInInfoPaymentRenewalStateWithinSessionTime(newId)
    }
}
