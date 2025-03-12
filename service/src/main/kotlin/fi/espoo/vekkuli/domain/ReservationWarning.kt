package fi.espoo.vekkuli.domain

import java.util.UUID

data class ReservationWarning(
    val id: UUID,
    val reservationId: Int,
    val boatId: Int?,
    val trailerId: Int?,
    val invoiceNumber: Int?,
    val key: String,
    val infoText: String?,
)
