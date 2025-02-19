package fi.espoo.vekkuli.utils

import fi.espoo.vekkuli.service.VariableService
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.*
import java.time.format.DateTimeFormatter

abstract class TimeProvider {
    abstract fun getCurrentDateTime(): LocalDateTime

    abstract fun isOverwritten(): Boolean

    fun getCurrentDate(): LocalDate = getCurrentDateTime().toLocalDate()
}

@Profile("staging || local || dev")
@Service
class StagingTimeProvider(
    private val variable: VariableService,
) : TimeProvider() {
    override fun getCurrentDateTime(): LocalDateTime {
        val dateTimeVariable = variable.get("current_system_staging_datetime")
        return if (dateTimeVariable != null && isValidDateTime(dateTimeVariable.value)) {
            LocalDateTime.of(LocalDateTime.parse(dateTimeVariable.value).toLocalDate(), LocalTime.now())
        } else {
            LocalDateTime.now()
        }
    }

    override fun isOverwritten(): Boolean = variable.get("current_system_staging_datetime") != null
}

@Profile("!local & !staging & !dev")
@Service
class SystemTimeProvider : TimeProvider() {
    override fun getCurrentDateTime(): LocalDateTime = LocalDateTime.now()

    override fun isOverwritten(): Boolean = false
}

val shortDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyy")
val fullDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
val shortYearDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy")
val fullDateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

// For filling dates in playwright tests
val testDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun formatAsShortDate(date: LocalDate): String = date.format(shortDateFormat)

fun formatAsFullDate(date: LocalDate?): String = if (date == null) "-" else date.format(fullDateFormat)

fun formatAsShortYearDate(date: LocalDate?): String = if (date == null) "-" else date.format(shortYearDateFormat)

fun formatAsFullDateTime(date: LocalDateTime): String = date.format(fullDateTimeFormat)

fun formatAsTestDate(date: LocalDate): String = date.format(testDateFormat)

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

fun isValidDateTime(dateTimeString: String?): Boolean =
    !dateTimeString.isNullOrBlank() && runCatching { LocalDateTime.parse(dateTimeString) }.isSuccess

// Create a custom serializer for LocalDate
object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: LocalDate
    ) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString(), formatter)
}

fun getNextDate(
    currentDate: LocalDate,
    targetMonth: Int,
    targetDay: Int
): LocalDate {
    val currentYear = currentDate.year

    // Create the target date for the current year
    val targetDateThisYear = LocalDate.of(currentYear, targetMonth, targetDay)

    // Check if the target date is in the future
    return if (!currentDate.isAfter(targetDateThisYear)) {
        targetDateThisYear
    } else {
        // If the target date is in the past, calculate it for the next year
        LocalDate.of(currentYear + 1, targetMonth, targetDay)
    }
}

data class SecondsRemaining(
    val value: Int
)

data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
)
