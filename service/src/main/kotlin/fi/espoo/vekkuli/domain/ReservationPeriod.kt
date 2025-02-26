package fi.espoo.vekkuli.domain

import java.time.LocalDate

data class ReservationPeriod(
    val isEspooCitizen: Boolean,
    val operation: ReservationOperation,
    val boatSpaceType: BoatSpaceType,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
