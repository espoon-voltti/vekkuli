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
    NoReservations
}

data class ReservationResult(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val reservationValidity: ReservationValidity,
)

data class ReservationConditions(
    val isCitizenOfEspoo: Boolean,
    val hasExistingReservationsTypes: HasExistingReservationsTypes,
    val currentDate: LocalDateTime,
) {
    private fun reserveSlipForEspooResidentResult(
        hasExistingReservationsTypes: HasExistingReservationsTypes,
        currentDate: LocalDateTime,
    ): ReservationResult {
        // Handle second slip restrictions
        if (hasExistingReservationsTypes != HasExistingReservationsTypes.No) {
            return ReservationResult(
                currentDate.toLocalDate(),
                getLastDayOfNextYearsJanuary(currentDate.year),
                if (hasExistingReservationsTypes == HasExistingReservationsTypes.Indefinite) {
                    ReservationValidity.Indefinite
                } else {
                    ReservationValidity.FixedTerm
                }
            )
        }
        // First slip
        return ReservationResult(
            currentDate.toLocalDate(),
            getLastDayOfNextYearsJanuary(currentDate.year),
            ReservationValidity.Indefinite,
        )
    }

    private fun canReserveSlipForEspooResident(
        hasExistingReservationsTypes: HasExistingReservationsTypes,
        currentDate: LocalDateTime,
    ): ReservationResultErrorCode? {
        // Handle second slip restrictions
        if (hasExistingReservationsTypes != HasExistingReservationsTypes.No) {
            val period = periodForSecondSlip(currentDate.year)
            if (!isTimeWithinDateRange(currentDate, period.first, period.second)) {
                // Not a period for reserving second slip
                return ReservationResultErrorCode.NotWithinPeriod
            }
            if (hasExistingReservationsTypes == HasExistingReservationsTypes.Both) {
                // Already has an indefinite reservation
                return ReservationResultErrorCode.AlreadyHasReservation
            }
            return null
        }
        // First slip
        val period = periodForNewSlip(currentDate.year, true)

        if (isTimeWithinDateRange(currentDate, period.first, period.second)) {
            return null
        }
        return ReservationResultErrorCode.NotWithinPeriod
    }

    private fun reserveSlipForNonEspooResidentResult(currentDate: LocalDateTime,): ReservationResult =
        ReservationResult(
            currentDate.toLocalDate(),
            getLastDayOfYear(currentDate.year),
            ReservationValidity.FixedTerm
        )

    private fun canReserveSlipForNonEspooResident(
        hasExistingReservationsTypes: HasExistingReservationsTypes,
        currentDate: LocalDateTime,
    ): ReservationResultErrorCode? {
        // Second slip
        if (hasExistingReservationsTypes != HasExistingReservationsTypes.No) {
            return ReservationResultErrorCode.AlreadyHasReservation
        }
        // First slip
        val period = periodForNewSlip(currentDate.year, false)
        if (!isTimeWithinDateRange(currentDate, period.first, period.second)) {
            return ReservationResultErrorCode.NotWithinPeriod
        }

        return null
    }

    fun reserveSlipResult(): ReservationResult =
        if (isCitizenOfEspoo) {
            reserveSlipForEspooResidentResult(hasExistingReservationsTypes, currentDate)
        } else {
            reserveSlipForNonEspooResidentResult(currentDate)
        }

    fun canReserveSlip(): ReservationResultErrorCode? =
        if (isCitizenOfEspoo) {
            canReserveSlipForEspooResident(hasExistingReservationsTypes, currentDate)
        } else {
            canReserveSlipForNonEspooResident(hasExistingReservationsTypes, currentDate)
        }

    fun canRenewSlip(): ReservationResultErrorCode? {
        if (!isCitizenOfEspoo) return ReservationResultErrorCode.NotEspooCitizen
        if (hasExistingReservationsTypes != HasExistingReservationsTypes.Indefinite) {
            return ReservationResultErrorCode.NotIndefinite
        }
        val period = periodForSlipRenewalAndChange(currentDate.year)
        if (!isTimeWithinDateRange(currentDate, period.first, period.second)) {
            ReservationResultErrorCode.NotWithinPeriod
        }
        return null
    }

    fun canChangeSlip(): ReservationResultErrorCode? {
        if (hasExistingReservationsTypes == HasExistingReservationsTypes.No) return ReservationResultErrorCode.NoReservations
        val firstPeriod = periodForSlipChange(currentDate.year)
        val secondPeriod = periodForSlipRenewalAndChange(currentDate.year)

        // Check if the current date is within the change period
        if (!(
                isTimeWithinDateRange(currentDate, firstPeriod.first, firstPeriod.second) ||
                    isTimeWithinDateRange(currentDate, secondPeriod.first, secondPeriod.second)
            )
        ) {
            return ReservationResultErrorCode.NotWithinPeriod
        }
        return null
    }

    fun renewSlipResult(): ReservationResult =
        ReservationResult(
            currentDate.toLocalDate(),
            getLastDayOfNextYearsJanuary(currentDate.year),
            ReservationValidity.Indefinite
        )

    fun changeSlipResult(reservationType: ReservationValidity): ReservationResult {
        // Only citizens of Espoo that have a place can change a place
        return ReservationResult(
            currentDate.toLocalDate(),
            getLastDayOfYear(currentDate.year),
            reservationType
        )
    }
}
