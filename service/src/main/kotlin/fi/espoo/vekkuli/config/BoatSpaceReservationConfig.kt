package fi.espoo.vekkuli.config

import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.ReservationStatus
import java.time.LocalDateTime

fun validateReservationIsActive(
    reservation: BoatSpaceReservationDetails,
    time: LocalDateTime
): Boolean {
    val now = time.toLocalDate()

    return when (reservation.status) {
        ReservationStatus.Confirmed, ReservationStatus.Invoiced -> {
            return (reservation.endDate >= now)
        }
        ReservationStatus.Cancelled -> {
            return (reservation.endDate > now)
        }
        else -> false
    }
}
