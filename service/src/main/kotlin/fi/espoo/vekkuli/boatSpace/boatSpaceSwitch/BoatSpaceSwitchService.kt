package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.config.validateReservationIsActive
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class BoatSpaceSwitchService(
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val seasonalService: SeasonalService,
    private val boatReservationService: BoatReservationService,
    private val boatSpaceSwitchRepository: BoatSpaceSwitchRepository,
    private val boatSpaceRepository: BoatSpaceRepository,
    private val citizenAccessControl: ContextCitizenAccessControl,
    private val reserverRepo: ReserverRepository,
    private val permissionService: PermissionService,
    private val timeProvider: TimeProvider
) {
    @Transactional
    fun startReservation(
        spaceId: Int,
        reservationId: Int
    ): BoatSpaceReservation {
        val (citizenId) = citizenAccessControl.requireCitizen()

        if (citizenHasExistingUnfinishedReservation(citizenId)) {
            throw Forbidden("Citizen can not have multiple reservations started")
        }

        if (!validateCitizenCanSwitchReservation(citizenId, spaceId, reservationId)) {
            throw Forbidden("Citizen can not switch reservation")
        }

        return boatSpaceSwitchRepository.copyReservationToSwitchReservation(
            reservationId,
            citizenId,
            spaceId
        )
    }

    fun validateCitizenCanSwitchReservation(
        actingCitizenId: UUID,
        targetSpaceId: Int,
        originalReservationId: Int
    ): Boolean {
        // mandatory information, otherwise the request is malformed
        val reserver = reserverRepo.getReserverById(actingCitizenId) ?: throw BadRequest("Reserver not found")
        val reservation =
            boatSpaceReservationRepo.getBoatSpaceReservationDetails(originalReservationId) ?: throw BadRequest("Reservation not found")
        val boatSpace = boatSpaceRepository.getBoatSpace(targetSpaceId) ?: throw BadRequest("Boat space not found")

        // Can only switch to the same type of space
        if (reservation.type != boatSpace.type) {
            return false
        }

        // Can switch only from an active reservation
        if (!validateReservationIsActive(reservation, timeProvider.getCurrentDateTime())) {
            return false
        }

        // User has rights to switch the reservation
        if (!permissionService.canSwitchReservation(reserver, boatSpace, reservation)) {
            return false
        }
        // Make sure the target space isn't reserved already
        if (boatSpaceRepository.isBoatSpaceReserved(targetSpaceId)) {
            return false
        }
        // Make sure the switch period is active
        return seasonalService.isReservationSwitchPeriodActive(reserver.id, boatSpace.type)
    }

    fun isSwitchedReservation(reservation: BoatSpaceReservationDetails): Boolean = reservation.creationType == CreationType.Switch

    // Returns the total payable amount of the new reservation. Can be negative
    fun getRevisedPrice(reservation: ReservationWithDependencies): Int {
        if (reservation.originalReservationId == null) {
            throw BadRequest("Original reservation not found")
        }
        return getRevisedPrice(reservation.priceCents, reservation.originalReservationId)
    }

    fun getRevisedPrice(reservation: BoatSpaceReservationDetails): Int {
        if (reservation.originalReservationId == null) {
            throw BadRequest("Original reservation not found")
        }
        return getRevisedPrice(reservation.priceCents, reservation.originalReservationId)
    }

    private fun getRevisedPrice(
        newReservationPriceCents: Int,
        originalReservationId: Int
    ): Int {
        val originalReservation =
            boatReservationService
                .getBoatSpaceReservation(originalReservationId) ?: throw BadRequest("Original reservation not found")

        return newReservationPriceCents - originalReservation.priceCents
    }

    private fun citizenHasExistingUnfinishedReservation(citizenId: UUID): Boolean =
        boatReservationService.getUnfinishedReservationForCitizen(citizenId) != null
}
