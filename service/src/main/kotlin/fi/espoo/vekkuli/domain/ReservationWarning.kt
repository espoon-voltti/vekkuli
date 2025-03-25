package fi.espoo.vekkuli.domain

import java.util.UUID

enum class ReservationWarningType {
    BoatWidth,
    BoatLength,
    BoatFutureOwner,
    BoatCoOwner,
    BoatOwnershipChange,
    BoatRegistrationCodeChange,
    BoatWeight,
    BoatType,
    TrailerWidth,
    TrailerLength,
    GeneralReservationWarning,
    InvoicePayment,
    RegistrationCodeNotUnique
}

data class ReservationWarning(
    val id: UUID,
    val reservationId: Int,
    val boatId: Int?,
    val trailerId: Int?,
    val invoiceNumber: Int?,
    val key: ReservationWarningType,
    val infoText: String?,
)
