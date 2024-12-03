package fi.espoo.vekkuli.utils

import org.mockito.Mockito
import java.time.LocalDate
import java.time.LocalDateTime

fun mockTimeProvider(
    timeProvider: TimeProvider,
    date: LocalDateTime = LocalDateTime.of(2024, 4, 1, 0, 0, 0)
) {
    // Mock the methods
    Mockito.`when`(timeProvider.getCurrentDateTime()).thenReturn(date)
    Mockito.`when`(timeProvider.getCurrentDate()).thenReturn(date.toLocalDate())
}

val startOfRenewPeriod: LocalDateTime = LocalDateTime.of(2024, 1, 7, 12, 0, 0)

val endDateWithinMonthOfRenewWindow: LocalDate = LocalDate.of(2025, 1, 31)

val startOfSlipReservationPeriod: LocalDate = LocalDate.of(2024, 4, 1)
