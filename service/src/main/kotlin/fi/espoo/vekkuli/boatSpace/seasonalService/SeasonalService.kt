package fi.espoo.vekkuli.boatSpace.seasonalService

import fi.espoo.vekkuli.config.BoatSpaceConfig.DAYS_BEFORE_RESERVATION_EXPIRY_NOTICE
import fi.espoo.vekkuli.config.DomainConstants.ESPOO_MUNICIPALITY_CODE
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.repository.ReserverRepository
import fi.espoo.vekkuli.service.ReservationResult
import fi.espoo.vekkuli.service.ReservationResultErrorCode
import fi.espoo.vekkuli.service.ReservationResultSuccess
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.getLastDayOfNextYearsJanuary
import fi.espoo.vekkuli.utils.getLastDayOfYear
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

    fun canRenewAReservation(
        oldValidity: ReservationValidity,
        oldEndDate: LocalDate,
        boatSpaceType: BoatSpaceType
    ): ReservationResult {
        val periods = seasonalRepository.getReservationPeriods()
        return canRenewAReservation(periods, oldValidity, oldEndDate, boatSpaceType)
    }

    fun canRenewAReservation(
        periods: List<ReservationPeriod>,
        oldValidity: ReservationValidity,
        oldEndDate: LocalDate,
        boatSpaceType: BoatSpaceType
    ): ReservationResult {
        if (oldValidity == ReservationValidity.FixedTerm) {
            // Fixed term reservations cannot be renewed
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        val now = timeProvider.getCurrentDate()

        if (now.isBefore(oldEndDate.minusDays(DAYS_BEFORE_RESERVATION_EXPIRY_NOTICE.toLong())) || now.isAfter(oldEndDate)) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        val hasActivePeriod =
            hasActiveReservationPeriod(
                periods,
                now,
                true,
                boatSpaceType,
                ReservationOperation.Renew
            )

        if (!hasActivePeriod) {
            // If no period found, reservation is not possible
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        return ReservationResult.Success(
            ReservationResultSuccess(
                now,
                getLastDayOfNextYearsJanuary(now.year),
                ReservationValidity.Indefinite
            )
        )
    }

    fun canSwitchAReservation(
        reservation: BoatSpaceReservationDetails,
        periods: List<ReservationPeriod>,
        isEspooCitizen: Boolean,
        boatSpaceType: BoatSpaceType
    ): ReservationResult {
        val now = timeProvider.getCurrentDate()

        val hasActivePeriod =
            hasActiveReservationPeriod(
                periods,
                now,
                isEspooCitizen,
                boatSpaceType,
                ReservationOperation.Change
            )

        if (!hasActivePeriod) {
            // If no period found, reservation is not possible
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        return ReservationResult.Success(
            ReservationResultSuccess(
                reservation.startDate,
                reservation.endDate,
                reservation.validity
            )
        )
    }

    fun getReservationPeriods(): List<ReservationPeriod> = seasonalRepository.getReservationPeriods()

    fun addPeriodInformationToReservation(
        reserverID: UUID,
        reservations: List<BoatSpaceReservationDetails>,
    ): List<BoatSpaceReservationDetails> {
        val reserver = reserverRepo.getReserverById(reserverID) ?: throw java.lang.IllegalArgumentException("Reserver not found")
        val isEspooCitizen = reserver.municipalityCode == ESPOO_MUNICIPALITY_CODE
        if (!isEspooCitizen) {
            // Only Espoo citizens can renew reservations
            return reservations
        }
        val periods = getReservationPeriods()
        return reservations.map { reservation ->
            val canRenewResult = canRenewAReservation(periods, reservation.validity, reservation.endDate, reservation.type)
            val canSwitchResult = canSwitchAReservation(reservation, periods, isEspooCitizen, reservation.type)
            reservation.copy(
                canRenew = canRenewResult.success,
                canSwitch = canSwitchResult.success,
            )
        }
    }

    fun canReserveANewSlip(
        reserverID: UUID,
        boatSpaceType: BoatSpaceType
    ): ReservationResult {
        val reserver =
            reserverRepo.getReserverById(reserverID) ?: return ReservationResult.Failure(
                ReservationResultErrorCode.NoReserver
            )
        val reservations = boatSpaceReservationRepo.getBoatSpaceReservationsForCitizen(reserverID, boatSpaceType)
        val hasSomePlace = reservations.isNotEmpty()
        val hasIndefinitePlace = reservations.any { it.validity == ReservationValidity.Indefinite }
        val isEspooCitizen = reserver.municipalityCode == ESPOO_MUNICIPALITY_CODE
        val periods = seasonalRepository.getReservationPeriods()

        if (hasSomePlace && !isEspooCitizen) {
            // Non-Espoo citizens can only have one reservation
            return ReservationResult.Failure(ReservationResultErrorCode.MaxReservations)
        }

        if (reservations.size >= 2) {
            // Only two reservations are allowed
            return return ReservationResult.Failure(ReservationResultErrorCode.MaxReservations)
        }

        val now = timeProvider.getCurrentDate()

        val hasActivePeriod =
            hasActiveReservationPeriod(
                periods,
                now,
                isEspooCitizen,
                boatSpaceType,
                if (hasSomePlace) ReservationOperation.SecondNew else ReservationOperation.New
            )

        if (!hasActivePeriod) {
            // If no period found, reservation is not possible
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }

        val validity =
            if (!isEspooCitizen || hasIndefinitePlace) ReservationValidity.FixedTerm else ReservationValidity.Indefinite
        val endDate =
            if (validity == ReservationValidity.Indefinite) {
                getLastDayOfNextYearsJanuary(now.year)
            } else {
                getLastDayOfYear(
                    now.year
                )
            }

        return ReservationResult.Success(
            ReservationResultSuccess(
                now,
                endDate,
                validity
            )
        )
    }
}
