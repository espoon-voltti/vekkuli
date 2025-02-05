package fi.espoo.vekkuli.boatSpace.seasonalService

import fi.espoo.vekkuli.config.BoatSpaceConfig.getSlipEndDate
import fi.espoo.vekkuli.config.BoatSpaceConfig.getStorageEndDate
import fi.espoo.vekkuli.config.BoatSpaceConfig.getTrailerEndDate
import fi.espoo.vekkuli.config.BoatSpaceConfig.getWinterEndDate
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.repository.ReserverRepository
import fi.espoo.vekkuli.service.BoatSpaceRepository
import fi.espoo.vekkuli.service.ReservationResult
import fi.espoo.vekkuli.service.ReservationResultErrorCode
import fi.espoo.vekkuli.service.ReservationResultSuccess
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.isMonthDayWithinRange
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.MonthDay
import java.util.*

@Service
class SeasonalService(
    private val seasonalRepository: SeasonalRepository,
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val reserverRepo: ReserverRepository,
    private val timeProvider: TimeProvider,
    private val boatSpaceRepository: BoatSpaceRepository,
) {
    fun hasActiveReservationPeriod(
        allPeriods: List<ReservationPeriod>,
        now: LocalDate,
        isEspooCitizen: Boolean,
        boatSpaceType: BoatSpaceType?,
        operation: ReservationOperation
    ): Boolean {
        val periods =
            allPeriods.filter {
                it.boatSpaceType == boatSpaceType &&
                    it.operation == operation &&
                    it.isEspooCitizen == isEspooCitizen
            }
        val today = MonthDay.from(now)
        return periods.any {
            isMonthDayWithinRange(today, MonthDay.of(it.startMonth, it.startDay), MonthDay.of(it.endMonth, it.endDay))
        }
    }

    fun isReservationRenewalPeriodActive(
        isEspooCitizen: Boolean,
        type: BoatSpaceType
    ): Boolean =
        hasActiveReservationPeriod(
            seasonalRepository.getReservationPeriods(),
            timeProvider.getCurrentDate(),
            isEspooCitizen,
            type,
            ReservationOperation.Renew
        )

    fun isReservationSwitchPeriodActive(
        isEspooCitizen: Boolean,
        type: BoatSpaceType
    ): Boolean =
        hasActiveReservationPeriod(
            seasonalRepository.getReservationPeriods(),
            timeProvider.getCurrentDate(),
            isEspooCitizen,
            type,
            ReservationOperation.Change
        )

    fun isBoatSpaceReserved(boatSpaceId: Int): Boolean = boatSpaceRepository.isBoatSpaceReserved(boatSpaceId)

    fun getReservationPeriods(): List<ReservationPeriod> = seasonalRepository.getReservationPeriods()

    fun canReserveANewSpace(
        reserverID: UUID,
        boatSpaceType: BoatSpaceType
    ): ReservationResult =
        when (boatSpaceType) {
            BoatSpaceType.Slip -> canReserveANewSlip(reserverID)
            BoatSpaceType.Winter -> canReserveANewWinterSpace(reserverID)
            BoatSpaceType.Trailer -> canReserveANewTrailerSpace(reserverID)
            BoatSpaceType.Storage -> canReserveANewStorageSpace(reserverID)
            else -> ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

    private fun canReserveANewStorageSpace(reserverId: UUID): ReservationResult {
        val reserver =
            reserverRepo.getReserverById(reserverId) ?: return ReservationResult.Failure(
                ReservationResultErrorCode.NoReserver
            )
        val isEspooCitizen = reserver.isEspooCitizen()

        val reservations = boatSpaceReservationRepo.getBoatSpaceReservationsForReserver(reserverId, BoatSpaceType.Storage)
        val hasSomePlace = reservations.isNotEmpty()
        val periods = seasonalRepository.getReservationPeriods()

        if (reservations.size >= 2) {
            // Only two reservations are allowed
            return ReservationResult.Failure(ReservationResultErrorCode.MaxReservations)
        }

        val now = timeProvider.getCurrentDate()

        val reservationOperation = if (hasSomePlace) ReservationOperation.SecondNew else ReservationOperation.New
        val hasActivePeriod =
            hasActiveReservationPeriod(
                periods,
                now,
                isEspooCitizen,
                BoatSpaceType.Storage,
                reservationOperation
            )

        if (!hasActivePeriod) {
            // If no period found, reservation is not possible
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        val endDate = getStorageEndDate(now)

        // Storage place reservations are always indefinite
        return ReservationResult.Success(
            ReservationResultSuccess(
                now,
                endDate,
                ReservationValidity.Indefinite
            )
        )
    }

    private fun canReserveANewTrailerSpace(reserverId: UUID): ReservationResult {
        val reserver =
            reserverRepo.getReserverById(reserverId) ?: return ReservationResult.Failure(
                ReservationResultErrorCode.NoReserver
            )
        val reservations = boatSpaceReservationRepo.getBoatSpaceReservationsForReserver(reserverId, BoatSpaceType.Trailer)
        val isEspooCitizen = reserver.isEspooCitizen()

        if (reservations.isNotEmpty()) {
            // Only one reservation allowed
            return ReservationResult.Failure(ReservationResultErrorCode.MaxReservations)
        }
        val periods = seasonalRepository.getReservationPeriods()

        val now = timeProvider.getCurrentDate()

        val hasActivePeriod =
            hasActiveReservationPeriod(
                periods,
                now,
                isEspooCitizen,
                BoatSpaceType.Trailer,
                ReservationOperation.New
            )

        if (!hasActivePeriod) {
            // If no period found, reservation is not possible
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        val validity = if (!isEspooCitizen) ReservationValidity.FixedTerm else ReservationValidity.Indefinite
        val endDate = getTrailerEndDate(now, validity)

        return ReservationResult.Success(
            ReservationResultSuccess(
                now,
                endDate,
                validity
            )
        )
    }

    private fun canReserveANewSlip(reserverID: UUID): ReservationResult {
        val reserver =
            reserverRepo.getReserverById(reserverID) ?: return ReservationResult.Failure(
                ReservationResultErrorCode.NoReserver
            )
        val reservations = boatSpaceReservationRepo.getBoatSpaceReservationsForReserver(reserverID, BoatSpaceType.Slip)
        val hasSomePlace = reservations.isNotEmpty()
        val hasIndefinitePlace = reservations.any { it.validity == ReservationValidity.Indefinite }
        val isEspooCitizen = reserver.isEspooCitizen()

        if (hasSomePlace && !isEspooCitizen) {
            // Non-Espoo citizens can only have one reservation
            return ReservationResult.Failure(ReservationResultErrorCode.MaxReservations)
        }
        val periods = seasonalRepository.getReservationPeriods()

        if (reservations.size >= 2) {
            // Only two reservations are allowed
            return ReservationResult.Failure(ReservationResultErrorCode.MaxReservations)
        }

        val now = timeProvider.getCurrentDate()

        val hasActivePeriod =
            hasActiveReservationPeriod(
                periods,
                now,
                isEspooCitizen,
                BoatSpaceType.Slip,
                if (hasSomePlace) ReservationOperation.SecondNew else ReservationOperation.New
            )

        if (!hasActivePeriod) {
            // If no period found, reservation is not possible
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        val validity = if (!isEspooCitizen || hasIndefinitePlace) ReservationValidity.FixedTerm else ReservationValidity.Indefinite
        val endDate = getSlipEndDate(now, validity)

        return ReservationResult.Success(
            ReservationResultSuccess(
                now,
                endDate,
                validity
            )
        )
    }

    fun getBoatSpaceReservationEndDateForRenew(
        boatSpaceType: BoatSpaceType,
        reservationValidity: ReservationValidity,
    ): LocalDate {
        val nowNextYear = timeProvider.getCurrentDate().plusYears(1)
        return when (boatSpaceType) {
            BoatSpaceType.Slip -> getSlipEndDate(nowNextYear, reservationValidity)
            BoatSpaceType.Winter -> getWinterEndDate(nowNextYear)
            BoatSpaceType.Storage -> getStorageEndDate(nowNextYear)
            BoatSpaceType.Trailer -> getTrailerEndDate(nowNextYear, reservationValidity)
        }
    }

    fun getBoatSpaceReservationEndDateForNew(
        boatSpaceType: BoatSpaceType,
        reservationValidity: ReservationValidity,
    ): LocalDate {
        val now = timeProvider.getCurrentDate()

        return when (boatSpaceType) {
            BoatSpaceType.Slip -> getSlipEndDate(now, reservationValidity)
            BoatSpaceType.Winter -> getWinterEndDate(now)
            BoatSpaceType.Storage -> getStorageEndDate(now)
            BoatSpaceType.Trailer -> getTrailerEndDate(now, reservationValidity)
        }
    }

    private fun canReserveANewWinterSpace(reserverID: UUID): ReservationResult {
        val reserver =
            reserverRepo.getReserverById(reserverID) ?: return ReservationResult.Failure(
                ReservationResultErrorCode.NoReserver
            )
        val isEspooCitizen = reserver.isEspooCitizen()

        if (!isEspooCitizen) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotEspooCitizen)
        }

        val reservations = boatSpaceReservationRepo.getBoatSpaceReservationsForReserver(reserverID, BoatSpaceType.Winter)
        val hasSomePlace = reservations.isNotEmpty()

        val periods = seasonalRepository.getReservationPeriods()

        if (reservations.size >= 2) {
            // Only two reservations are allowed
            return ReservationResult.Failure(ReservationResultErrorCode.MaxReservations)
        }

        val now = timeProvider.getCurrentDate()

        val hasActivePeriod =
            hasActiveReservationPeriod(
                periods,
                now,
                // always Espoo citizen
                true,
                BoatSpaceType.Winter,
                if (hasSomePlace) ReservationOperation.SecondNew else ReservationOperation.New
            )

        if (!hasActivePeriod) {
            // If no period found, reservation is not possible
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        val endDate = getWinterEndDate(now)

        return ReservationResult.Success(
            ReservationResultSuccess(
                now,
                endDate,
                ReservationValidity.Indefinite
            )
        )
    }
}
