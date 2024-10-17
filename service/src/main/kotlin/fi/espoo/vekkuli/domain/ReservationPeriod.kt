package fi.espoo.vekkuli.domain

data class ReservationPeriod(
    val id: String,
    val startMonth: Int,
    val startDay: Int,
    val endMonth: Int,
    val endDay: Int,
)
