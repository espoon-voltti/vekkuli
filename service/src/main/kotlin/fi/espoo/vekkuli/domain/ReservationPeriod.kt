package fi.espoo.vekkuli.domain

import java.time.LocalDate
import java.time.LocalTime

data class ReservationPeriod(
    val isEspooCitizen: Boolean,
    val operation: ReservationOperation,
    val boatSpaceType: BoatSpaceType,
    val startDate: LocalDate,
    val startTime: LocalTime,
    val endDate: LocalDate,
)
