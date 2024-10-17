package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.utils.getFirstWeekdayOfMonth
import fi.espoo.vekkuli.utils.getLastDayOfNextYearsJanuary
import fi.espoo.vekkuli.utils.getLastDayOfYear
import fi.espoo.vekkuli.utils.isTimeWithinDateRange
import java.time.LocalDate
import java.time.LocalDateTime

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

data class ReservationResult(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val existingReservations: ExistingReservations
)

data class ReservationConditions(
    val isCitizenOfEspoo: Boolean,
    val existingReservations: ExistingReservations,
    val currentYear: Int,
    val currentDate: LocalDateTime,
) {
    // Returns the end date of the reservation if the reservation is allowed, otherwise null
    private fun canReserveSlipForEspooResident(
        existingReservations: ExistingReservations,
        currentYear: Int,
        currentDate: LocalDateTime,
    ): ReservationResult? {
        // Handle second slip restrictions
        if (existingReservations != ExistingReservations.No) {
            val period = periodForSecondSlip(currentYear)
            if (!isTimeWithinDateRange(currentDate, period.first, period.second)) {
                // Not a period for reserving second slip
                return null
            }
            if (existingReservations == ExistingReservations.Both) {
                // Already has an indefinite reservation
                return null
            }
            return ReservationResult(
                currentDate.toLocalDate(),
                getLastDayOfNextYearsJanuary(currentYear),
                existingReservations
            )
        }
        // First slip
        val period = periodForNewSlip(currentYear, true)

        if (isTimeWithinDateRange(currentDate, period.first, period.second)) {
            return ReservationResult(
                currentDate.toLocalDate(),
                getLastDayOfNextYearsJanuary(currentYear),
                ExistingReservations.Indefinite
            )
        }
        return null
    }

    private fun canReserveSlipForNonEspooResident(
        existingReservations: ExistingReservations,
        currentYear: Int,
        currentDate: LocalDateTime,
    ): ReservationResult? {
        // Handle first slip restrictions
        if (existingReservations == ExistingReservations.No) {
            // First slip
            val period = periodForNewSlip(currentYear, false)

            if (isTimeWithinDateRange(currentDate, period.first, period.second)) {
                return ReservationResult(
                    currentDate.toLocalDate(),
                    getLastDayOfYear(currentYear),
                    ExistingReservations.FixedTerm
                )
            }
        }
        return null
    }

    fun canReserveSlip(): ReservationResult? =
        if (isCitizenOfEspoo) {
            canReserveSlipForEspooResident(existingReservations, currentYear, currentDate)
        } else {
            canReserveSlipForNonEspooResident(existingReservations, currentYear, currentDate)
        }

    // Returns the end date of the reservation if the reservation is allowed, otherwise null
    fun canRenewSlip(): ReservationResult? {
        // Only Espoo citizens can renew a place
        // Only indefinite spaces can be renewed
        if (isCitizenOfEspoo && existingReservations == ExistingReservations.Indefinite) {
            val period = periodForSlipRenewalAndChange(currentYear)
            if (isTimeWithinDateRange(currentDate, period.first, period.second)) {
                return ReservationResult(
                    currentDate.toLocalDate(),
                    getLastDayOfNextYearsJanuary(currentYear),
                    ExistingReservations.Indefinite
                )
            }
        }
        return null
    }

    // Returns the end date of the reservation if the reservation is allowed, otherwise null
    fun canChangeSlip(): ReservationResult? {
        // Only citizens of Espoo that have a place can change a place
        if (isCitizenOfEspoo && existingReservations == ExistingReservations.Indefinite) {
            val firstPeriod = periodForSlipChange(currentYear)
            val secondPeriod = periodForSlipRenewalAndChange(currentYear)

            // Check if the current date is within the change period
            if (isTimeWithinDateRange(currentDate, firstPeriod.first, firstPeriod.second) ||
                isTimeWithinDateRange(currentDate, secondPeriod.first, secondPeriod.second)
            ) {
                return ReservationResult(
                    currentDate.toLocalDate(),
                    getLastDayOfYear(currentYear),
                    ExistingReservations.Indefinite
                )
            }
        }
        return null
    }
}
