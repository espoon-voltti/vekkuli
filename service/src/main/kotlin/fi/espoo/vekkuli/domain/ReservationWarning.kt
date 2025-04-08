package fi.espoo.vekkuli.domain

import java.util.UUID

enum class ReservationWarningType(
    val category: Category
) {
    BoatWidth(Category.Boat),
    BoatLength(Category.Boat),
    BoatFutureOwner(Category.Boat),
    BoatCoOwner(Category.Boat),
    BoatOwnershipChange(Category.Boat),
    BoatRegistrationCodeChange(Category.Boat),
    BoatWeight(Category.Boat),
    BoatType(Category.Boat),
    TrailerWidth(Category.Trailer),
    TrailerLength(Category.Trailer),
    GeneralReservationWarning(Category.General),
    InvoicePayment(Category.General),
    RegistrationCodeNotUnique(Category.Boat);

    enum class Category {
        Boat,
        Trailer,
        General
    }
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
