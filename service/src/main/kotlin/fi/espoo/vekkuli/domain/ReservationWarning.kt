package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.config.ReservationWarningType
import java.util.UUID

data class ReservationWarning(
    val id: UUID,
    val reservationId: Int,
    val boatId: Int?,
    val trailerId: Int?,
    val invoiceNumber: Int?,
    val key: ReservationWarningType,
    val infoText: String?,
)
