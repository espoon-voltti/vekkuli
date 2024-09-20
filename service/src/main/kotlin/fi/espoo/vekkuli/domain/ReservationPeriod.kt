package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.utils.getFirstWeekdayOfMonth
import fi.espoo.vekkuli.utils.getLastDayOfNextYearsJanuary
import fi.espoo.vekkuli.utils.getLastDayOfYear
import fi.espoo.vekkuli.utils.isTimeWithinDateRange
import java.time.LocalDate
import java.time.LocalDateTime

enum class CurrentPlace {
    No,
    FixedTerm,
    Indefinite,
    Both
}

private fun periodForNewSlip(
    year: Int,
    isCitizenOfEspoo: Boolean
): Pair<LocalDate, LocalDate> {
    if (isCitizenOfEspoo) {
        return Pair(getFirstWeekdayOfMonth(year, 3), LocalDate.of(year, 9, 30))
    }
    return Pair(getFirstWeekdayOfMonth(year, 4), LocalDate.of(year, 9, 30))
}

private fun periodForSlipRenewal(year: Int,): Pair<LocalDate, LocalDate> = Pair(getFirstWeekdayOfMonth(year, 1), LocalDate.of(year, 1, 31))

private fun periodForSlipChange(year: Int,): Pair<LocalDate, LocalDate> = Pair(getFirstWeekdayOfMonth(year, 2), LocalDate.of(year, 9, 30))

private fun periodForSecondSlip(year: Int): Pair<LocalDate, LocalDate> = Pair(getFirstWeekdayOfMonth(year, 4), LocalDate.of(year, 9, 30))

data class ReservationPeriod(
    private val currentTime: LocalDateTime,
    private val periodYear: Int,
    private val isCitizenOfEspoo: Boolean,
    private val currentPlace: CurrentPlace,
) {
    // Returns the end date of the reservation if the reservation is allowed, otherwise null
    fun canReserveSlip(): LocalDate? {
        // Handle second slip restrictions
        if (currentPlace != CurrentPlace.No) {
            // person is reserving a second place
            if (!isCitizenOfEspoo) {
                // If not Espoo citizen, dont allow
                return null
            }

            val period = periodForSecondSlip(periodYear)
            if (currentPlace != CurrentPlace.FixedTerm || !isTimeWithinDateRange(currentTime, period.first, period.second)) {
                // Already has an indefinite reservation, or not in period for reserving second slip
                return null
            }
        }

        val period = periodForNewSlip(periodYear, isCitizenOfEspoo)

        if (!isTimeWithinDateRange(currentTime, period.first, period.second)) {
            // Not withing reservation period
            return null
        }

        return getEndDate()
    }

    // Returns the end date of the reservation if the reservation is allowed, otherwise null
    fun canRenewSlip(): LocalDate? {
        if (!isCitizenOfEspoo) {
            // Only Espoo citizens can renew a place
            return null
        }

        if (currentPlace != CurrentPlace.Indefinite) {
            // Only indefinite spaces can be renewed
            return null
        }

        val period = periodForSlipRenewal(periodYear)
        if (!isTimeWithinDateRange(currentTime, period.first, period.second)) {
            // Not withing reservation period
            return null
        }

        return getEndDate()
    }

    // Returns the end date of the reservation if the reservation is allowed, otherwise null
    fun canChangeSlip(): LocalDate? {
        if (!isCitizenOfEspoo || currentPlace == CurrentPlace.No) {
            // Only citizens of Espoo that have a place can change a place
            return null
        }
        val period = periodForSlipChange(periodYear)
        if (!isTimeWithinDateRange(currentTime, period.first, period.second)) {
            // Not withing reservation period
            return null
        }
        return getEndDate()
    }

    private fun getEndDate(): LocalDate =
        if (isCitizenOfEspoo) {
            if (currentPlace == CurrentPlace.No || currentPlace == CurrentPlace.FixedTerm) {
                // Allow indefinite if no place or has a fixed term place
                getLastDayOfNextYearsJanuary(periodYear)
            } else {
                getLastDayOfYear(periodYear)
            }
        } else {
            // Others than Espoo citizens get always only fixed term places
            getLastDayOfYear(periodYear)
        }
}
