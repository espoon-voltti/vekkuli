package fi.espoo.vekkuli.domain

import java.time.LocalDateTime

data class ReservationWarning(
    val reservationId: Int,
    val key: String,
    val created: LocalDateTime,
)
