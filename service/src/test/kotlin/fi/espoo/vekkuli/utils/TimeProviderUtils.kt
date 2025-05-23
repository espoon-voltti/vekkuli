package fi.espoo.vekkuli.utils

import fi.espoo.vekkuli.config.BoatSpaceConfig
import org.mockito.Mockito
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

fun mockTimeProvider(
    timeProvider: TimeProvider,
    date: LocalDateTime = LocalDateTime.of(2024, 4, 1, 12, 0, 0)
) {
    println("Mocking TimeProvider to: $date")
    // Mock the methods
    Mockito.`when`(timeProvider.getCurrentDateTime()).thenReturn(date)
    Mockito.`when`(timeProvider.getCurrentDate()).thenReturn(date.toLocalDate())
}

val startOfSlipRenewPeriod: LocalDateTime = LocalDateTime.of(2025, 1, 7, 12, 0, 0)
val startOfWinterSpaceRenewPeriod: LocalDateTime = LocalDateTime.of(2025, 8, 15, 12, 0, 0)
val startOfWinterSpaceRenewPeriod2026: LocalDateTime = LocalDateTime.of(2026, 8, 18, 12, 0, 0)

val startofTrailerRenewPeriod: LocalDateTime = LocalDateTime.of(2026, 4, 1, 12, 0, 0)
val startOfStorageRenewPeriod2026: LocalDateTime = LocalDateTime.of(2026, 8, 17, 12, 0, 0)

val endDateWithinMonthOfSlipRenewWindow: LocalDate = LocalDate.of(2025, 1, 31)

val startOfSlipReservationPeriod: LocalDateTime = LocalDateTime.of(2025, 4, 1, 12, 0, 0)
val startOfWinterReservationPeriod: LocalDateTime = LocalDateTime.of(2025, 9, 15, 12, 0, 0)
val startOfStorageReservationPeriod: LocalDateTime = LocalDateTime.of(2025, 9, 15, 12, 0, 0)
val startOfTrailerReservationPeriod: LocalDateTime = LocalDateTime.of(2025, 5, 1, 12, 0, 0)

val startOfSlipSwitchPeriodForEspooCitizen: LocalDateTime = LocalDateTime.of(2025, 1, 7, 12, 0, 0)
val endOfSlipSwitchPeriodForEspooCitizen: LocalDateTime = LocalDateTime.of(2025, 9, 30, 12, 0, 0)
val startOfWinterSwitchPeriodForEspooCitizen: LocalDateTime = LocalDateTime.of(2025, 8, 15, 12, 0, 0)
val startOfStorageSwitchPeriodForEspooCitizen: LocalDateTime = LocalDateTime.of(2025, 8, 18, 12, 0, 0)
val startOfTrailerSwitchPeriodForEspooCitizen: LocalDateTime = LocalDateTime.of(2025, 4, 1, 12, 0, 0)

// Others
val startOfStorageReservationPeriodForOthers: LocalDateTime = LocalDateTime.of(2025, 9, 15, 12, 0, 0)

val sessionDuration = Duration.ofSeconds(BoatSpaceConfig.SESSION_TIME_IN_SECONDS.toLong())
val moreThanSessionDuration = sessionDuration.plusSeconds(5L)
val lessThanSessionDuration = sessionDuration.minusSeconds(5L)
