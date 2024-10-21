package fi.espoo.vekkuli.domain

data class ReservationPeriod(
    val isEspooCitizen: Boolean,
    val operation: ReservationOperation,
    val boatSpaceType: BoatSpaceType,
    val startMonth: Int,
    val startDay: Int,
    val endMonth: Int,
    val endDay: Int,
)
