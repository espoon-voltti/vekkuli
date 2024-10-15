package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.utils.getFirstWeekdayOfMonth
import fi.espoo.vekkuli.utils.getLastDayOfNextYearsJanuary
import fi.espoo.vekkuli.utils.getLastDayOfYear
import fi.espoo.vekkuli.utils.isTimeWithinDateRange
import java.time.LocalDate
import java.time.LocalDateTime

enum class HasExistingReservationsTypes {
    No,
    FixedTerm,
    Indefinite,
    Both
}
// Slip period restrictions

private fun periodForSlipRenewalAndChange(year: Int): Pair<LocalDate, LocalDate> =
    Pair(getFirstWeekdayOfMonth(year, 1), LocalDate.of(year, 1, 31))

private fun periodForSlipChange(year: Int): Pair<LocalDate, LocalDate> = Pair(getFirstWeekdayOfMonth(year, 3), LocalDate.of(year, 9, 30))

private fun periodForNewSlip(
    year: Int,
    isCitizenOfEspoo: Boolean
): Pair<LocalDate, LocalDate> =
    if (isCitizenOfEspoo) {
        Pair(getFirstWeekdayOfMonth(year, 3), LocalDate.of(year, 9, 30))
    } else {
        Pair(getFirstWeekdayOfMonth(year, 4), LocalDate.of(year, 9, 30))
    }

private fun periodForSecondSlip(year: Int): Pair<LocalDate, LocalDate> = Pair(getFirstWeekdayOfMonth(year, 4), LocalDate.of(year, 9, 30))

enum class ReservationResultErrorCode {
    NotWithinPeriod,
    AlreadyHasReservation,
    NotEspooCitizen,
    NotIndefinite,
}

sealed class ReservationResult {
    data class Success(
        val startDate: LocalDate,
        val endDate: LocalDate,
        val hasExistingReservationsTypes: HasExistingReservationsTypes,
    ) : ReservationResult()

    data class Failure(
        val errorCode: ReservationResultErrorCode
    ) : ReservationResult()
}

data class ReservationConditions(
    val isCitizenOfEspoo: Boolean,
    val hasExistingReservationsTypes: HasExistingReservationsTypes,
    val currentDate: LocalDateTime,
) {
    // Returns the end date of the reservation if the reservation is allowed, otherwise null
    private fun canReserveSlipForEspooResident(
        hasExistingReservationsTypes: HasExistingReservationsTypes,
        currentDate: LocalDateTime,
    ): ReservationResult {
        // Handle second slip restrictions
        if (hasExistingReservationsTypes != HasExistingReservationsTypes.No) {
            val period = periodForSecondSlip(currentDate.year)
            if (!isTimeWithinDateRange(currentDate, period.first, period.second)) {
                // Not a period for reserving second slip
                return ReservationResult.Failure(ReservationResultErrorCode.NotWithinPeriod)
            }
            if (hasExistingReservationsTypes == HasExistingReservationsTypes.Both) {
                // Already has an indefinite reservation
                return ReservationResult.Failure(ReservationResultErrorCode.AlreadyHasReservation)
            }
            return ReservationResult.Success(
                currentDate.toLocalDate(),
                getLastDayOfNextYearsJanuary(currentDate.year),
                hasExistingReservationsTypes,
            )
        }
        // First slip
        val period = periodForNewSlip(currentDate.year, true)

        if (isTimeWithinDateRange(currentDate, period.first, period.second)) {
            return ReservationResult.Success(
                currentDate.toLocalDate(),
                getLastDayOfNextYearsJanuary(currentDate.year),
                HasExistingReservationsTypes.Indefinite
            )
        }
        return ReservationResult.Failure(ReservationResultErrorCode.NotWithinPeriod)
    }

    private fun canReserveSlipForNonEspooResident(
        hasExistingReservationsTypes: HasExistingReservationsTypes,
        currentDate: LocalDateTime,
    ): ReservationResult? {
        // Handle first slip restrictions
        if (hasExistingReservationsTypes == HasExistingReservationsTypes.No) {
            // First slip
            val period = periodForNewSlip(currentDate.year, false)

            if (isTimeWithinDateRange(currentDate, period.first, period.second)) {
                return ReservationResult.Success(
                    currentDate.toLocalDate(),
                    getLastDayOfYear(currentDate.year),
                    HasExistingReservationsTypes.FixedTerm
                )
            } else {
                return ReservationResult.Failure(ReservationResultErrorCode.NotWithinPeriod)
            }
        }
        return ReservationResult.Failure(ReservationResultErrorCode.AlreadyHasReservation)
    }

    fun canReserveSlip(): ReservationResult? =
        if (isCitizenOfEspoo) {
            canReserveSlipForEspooResident(hasExistingReservationsTypes, currentDate)
        } else {
            canReserveSlipForNonEspooResident(hasExistingReservationsTypes, currentDate)
        }

    // Returns the end date of the reservation if the reservation is allowed, otherwise null
    fun canRenewSlip(): ReservationResult? {
        // Only Espoo citizens can renew a place
        // Only indefinite spaces can be renewed
        if (isCitizenOfEspoo) {
            return if (hasExistingReservationsTypes == HasExistingReservationsTypes.Indefinite) {
                val period = periodForSlipRenewalAndChange(currentDate.year)
                if (isTimeWithinDateRange(currentDate, period.first, period.second)) {
                    ReservationResult.Success(
                        currentDate.toLocalDate(),
                        getLastDayOfNextYearsJanuary(currentDate.year),
                        HasExistingReservationsTypes.Indefinite
                    )
                } else {
                    ReservationResult.Failure(ReservationResultErrorCode.NotWithinPeriod)
                }
            } else {
                ReservationResult.Failure(ReservationResultErrorCode.NotIndefinite)
            }
        }
        return ReservationResult.Failure(ReservationResultErrorCode.NotEspooCitizen)
    }

    // Returns the end date of the reservation if the reservation is allowed, otherwise null
    fun canChangeSlip(): ReservationResult? {
        // Only citizens of Espoo that have a place can change a place
        if (isCitizenOfEspoo && hasExistingReservationsTypes == HasExistingReservationsTypes.Indefinite) {
            val firstPeriod = periodForSlipChange(currentDate.year)
            val secondPeriod = periodForSlipRenewalAndChange(currentDate.year)

            // Check if the current date is within the change period
            if (isTimeWithinDateRange(currentDate, firstPeriod.first, firstPeriod.second) ||
                isTimeWithinDateRange(currentDate, secondPeriod.first, secondPeriod.second)
            ) {
                return ReservationResult.Success(
                    currentDate.toLocalDate(),
                    getLastDayOfYear(currentDate.year),
                    HasExistingReservationsTypes.Indefinite
                )
            }
        }
        return ReservationResult.Failure(ReservationResultErrorCode.NotWithinPeriod)
    }
}
