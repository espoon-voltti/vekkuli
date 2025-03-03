package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.config.validateReservationIsActive
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service
import java.util.*

@Service
class SwitchPolicyService(
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val seasonalService: SeasonalService,
    private val timeProvider: TimeProvider,
    private val permissionService: PermissionService,
    private val reserverRepo: ReserverRepository,
    private val boatSpaceRepository: BoatSpaceRepository
) {
    fun citizenCanSwitchToReservation(
        originalReservationId: Int,
        actingCitizenId: UUID,
        targetSpaceId: Int,
    ): ReservationResult {
        // mandatory information, otherwise the request is malformed
        val reserver = reserverRepo.getReserverById(actingCitizenId) ?: throw BadRequest("Reserver not found")
        val reservation =
            boatSpaceReservationRepo.getBoatSpaceReservationDetails(originalReservationId) ?: throw BadRequest("Reservation not found")

        val boatSpace = boatSpaceRepository.getBoatSpace(targetSpaceId) ?: throw BadRequest("Boat space not found")

        // Can switch only if the target space is active
        if (!boatSpace.isActive) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        // Can only switch to the same type of space
        if (reservation.type != boatSpace.type) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        // Can switch only from an active reservation
        if (!validateReservationIsActive(reservation, timeProvider.getCurrentDateTime())) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        // User has rights to switch the reservation
        if (!permissionService.canSwitchReservation(reserver, reservation)) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        // Make sure the target space isn't reserved already
        if (!boatSpaceRepository.isBoatSpaceAvailable(targetSpaceId)) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        // Check the period is active
        if (!seasonalService.isReservationSwitchPeriodActive(reserver.isEspooCitizen(), boatSpace.type)) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        // It should have the same dates as the original
        return ReservationResult.Success(
            ReservationResultSuccess(
                reservation.startDate,
                reservation.endDate,
                reservation.validity
            )
        )
    }

    fun citizenCanSwitchReservation(
        originalReservationId: Int,
        actingCitizenId: UUID
    ): ReservationResult {
        val reserver = reserverRepo.getReserverById(actingCitizenId) ?: throw BadRequest("Reserver not found")
        val reservation =
            boatSpaceReservationRepo.getBoatSpaceReservationDetails(originalReservationId) ?: throw BadRequest("Reservation not found")

        // Can switch only from an active reservation
        if (!validateReservationIsActive(reservation, timeProvider.getCurrentDateTime())) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        // Check the period is active
        if (!seasonalService.isReservationSwitchPeriodActive(reserver.isEspooCitizen(), reservation.type)) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        // It should have the same dates as the original
        return ReservationResult.Success(
            ReservationResultSuccess(
                reservation.startDate,
                reservation.endDate,
                reservation.validity
            )
        )
    }
}
