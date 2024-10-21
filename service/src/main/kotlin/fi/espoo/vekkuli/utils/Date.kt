package fi.espoo.vekkuli.utils

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.*
import java.time.format.DateTimeFormatter

interface TimeProvider {
    fun getCurrentDate(): LocalDateTime
}

@Profile("local")
@Service
class LocalTimeProvider : TimeProvider {
    override fun getCurrentDate(): LocalDateTime = LocalDateTime.of(2024, 1, 1, 0, 0)
}

@Profile("!local")
@Service
class SystemTimeProvider : TimeProvider {
    override fun getCurrentDate(): LocalDateTime = LocalDateTime.now()
}

val shortFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyy")
val datePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun dateToShortString(date: LocalDate): String = date.format(shortFormat)

fun dateToString(date: LocalDate): String = date.format(datePattern)

fun getFirstWeekdayOfMonth(
    year: Int,
    month: Int
): LocalDate {
    val firstDay = LocalDate.of(year, month, 1)
    return when (firstDay.dayOfWeek) {
        DayOfWeek.SATURDAY -> firstDay.plusDays(2)
        DayOfWeek.SUNDAY -> firstDay.plusDays(1)
        else -> firstDay
    }
}

fun getLastDayOfYear(year: Int): LocalDate = LocalDate.of(year, 12, 31)

fun getLastDayOfNextYearsJanuary(year: Int): LocalDate = LocalDate.of(year + 1, 1, 31)

fun isTimeWithinDateRange(
    dateTime: LocalDateTime,
    startDate: LocalDate,
    endDate: LocalDate
): Boolean {
    val startDateTime = startDate.atStartOfDay() // Start of the day (00:00:00)
    val endDateTime = endDate.atTime(LocalTime.MAX) // End of the day (23:59:999)
    return !dateTime.isBefore(startDateTime) && !dateTime.isAfter(endDateTime)
}

fun isMonthDayWithinRange(
    today: MonthDay,
    startDate: MonthDay,
    endDate: MonthDay,
): Boolean {
    if (startDate <= endDate) {
        // Period does not cross the year
        return today in startDate..endDate
    }
    // Period crosses the year
    return today >= startDate || today <= endDate
}

fun getCurrentDate(): LocalDateTime = LocalDateTime.now()
