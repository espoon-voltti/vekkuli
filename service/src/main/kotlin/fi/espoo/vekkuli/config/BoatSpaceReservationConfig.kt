package fi.espoo.vekkuli.config

import fi.espoo.vekkuli.domain.BoatSpaceReservation
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.ReservationStatus
import java.time.LocalDate
import java.time.LocalDateTime

fun validateReservationIsActive(
    reservation: BoatSpaceReservationDetails,
    time: LocalDateTime
): Boolean = validateReservationIsActive(reservation.status, reservation.endDate, time.toLocalDate())

fun validateReservationIsActive(
    reservation: BoatSpaceReservation,
    time: LocalDateTime
): Boolean = validateReservationIsActive(reservation.status, reservation.endDate, time.toLocalDate())

fun validateReservationIsActive(
    status: ReservationStatus,
    endDate: LocalDate,
    now: LocalDate
): Boolean {
    return when (status) {
        ReservationStatus.Confirmed, ReservationStatus.Invoiced -> {
            return (endDate >= now)
        }
        ReservationStatus.Cancelled -> {
            return (endDate > now)
        }
        else -> false
    }
}
