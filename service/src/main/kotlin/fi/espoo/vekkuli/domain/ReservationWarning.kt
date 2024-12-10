package fi.espoo.vekkuli.domain

data class ReservationWarning(
    val reservationId: Int,
    val boatId: Int?,
    val trailerId: Int?,
    val key: String,
)
