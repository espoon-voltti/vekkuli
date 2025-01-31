package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.config.BoatSpaceConfig.DAYS_BEFORE_RESERVATION_EXPIRY_NOTICE
import fi.espoo.vekkuli.config.validateReservationIsActive
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service
import java.util.*

@Service
class RenewalPolicyService(
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val seasonalService: SeasonalService,
    private val timeProvider: TimeProvider,
    private val permissionService: PermissionService,
    private val reserverRepo: ReserverRepository
) {
    fun employeeCanRenewReservation(reservationId: Int): ReservationResult {
        val reservation = boatSpaceReservationRepo.getBoatSpaceReservationDetails(reservationId)
                ?: throw IllegalArgumentException("Reservation not found")

        val contractPeriod = seasonalService.getRenewReservationStartAndEndDate(reservation.type, reservation.validity)

        val reserver = reserverRepo.getReserverById(reservation.reserverId) ?: throw IllegalArgumentException("Reserver not found")

        // Hacky solution to finding if season is open. Only storage reservations have seasons for non Espoo citizens
        val isEspooCitizenFlag = reservation.type != BoatSpaceType.Storage || reserver.isEspooCitizen()

        // Season not open
        if(!seasonalService.isReservationRenewalPeriodActive(isEspooCitizenFlag, reservation.type)){
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        // Employee can always renew the reservation
        return ReservationResult.Success(
            ReservationResultSuccess(
                contractPeriod.startDate,
                contractPeriod.endDate,
                reservation.validity
            )
        )
    }

    fun citizenCanRenewReservation(reservationId: Int, reserverId: UUID): ReservationResult {
        val reservation = boatSpaceReservationRepo.getBoatSpaceReservationDetails(reservationId)
                ?: throw IllegalArgumentException("Reservation not found")

        val reserver = reserverRepo.getReserverById(reserverId) ?: throw IllegalArgumentException("Reserver not found")

        // Citizen can renew only reservations if they live in Espoo
        if (!reserver.isEspooCitizen()) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotEspooCitizen)
        }

        // Can renew only active reservations
        if (!validateReservationIsActive(reservation, timeProvider.getCurrentDateTime())) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        // User has rights to renew the reservation
        if (!permissionService.canRenewReservation(reserver, reservation)) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        if (reservation.validity == ReservationValidity.FixedTerm) {
            // Fixed term reservations cannot be renewed
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        val currentDate = timeProvider.getCurrentDate()
        val originalReservationRenewGracePeriod = reservation.endDate.minusDays(DAYS_BEFORE_RESERVATION_EXPIRY_NOTICE.toLong())
        // Check if the reservation is within the renewal period and reservation is about to expire
        if (currentDate.isBefore(originalReservationRenewGracePeriod)) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        // Season not open
        if(!seasonalService.isReservationRenewalPeriodActive(reserver.isEspooCitizen(), reservation.type)){
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        val contractPeriod = seasonalService.getRenewReservationStartAndEndDate(reservation.type, reservation.validity)

        return ReservationResult.Success(
            ReservationResultSuccess(
                contractPeriod.startDate,
                contractPeriod.endDate,
                reservation.validity
            )
        )
    }
}
