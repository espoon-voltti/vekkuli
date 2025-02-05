package fi.espoo.vekkuli.config

import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.ReservationStatus
import java.time.LocalDateTime

fun validateReservationIsActive(
    reservation: BoatSpaceReservationDetails,
    time: LocalDateTime
): Boolean = validateReservationIsActive(reservation.status, reservation.endDate, time)

fun validateReservationIsActive(
    status: ReservationStatus,
    endDate: LocalDateTime,
    now: LocalDateTime
): Boolean {
    return when (status) {
        ReservationStatus.Confirmed, ReservationStatus.Invoiced -> {
            return (endDate > now)
        }
        ReservationStatus.Cancelled -> {
            return (endDate > now)
        }
        else -> false
    }
}
