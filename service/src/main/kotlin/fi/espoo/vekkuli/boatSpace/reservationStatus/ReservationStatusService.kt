package fi.espoo.vekkuli.boatSpace.reservationStatus

import fi.espoo.vekkuli.domain.BoatSpaceReservation
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class ReservationStatusService(
    private val timeProvider: TimeProvider
) {
    // If a reservation should be visible for users and admins
    fun isReservationActive(
        status: ReservationStatus,
        endDate: LocalDate,
        terminationTimestamp: LocalDateTime? = null,
    ): Boolean {
        // Reservation is terminated and the end date is tomorrow or after
        if (terminationTimestamp != null && endDate.isAfter(timeProvider.getCurrentDate())) {
            return true
        }

        // Reservation is in active state and the end date is today or after
        if ((status in listOf(ReservationStatus.Confirmed, ReservationStatus.Invoiced)) &&
            endDate.isAfter(timeProvider.getCurrentDate().minusDays(1))
        ) {
            return true
        }
        return false
    }

    fun isReservationActive(reservation: BoatSpaceReservationDetails): Boolean =
        isReservationActive(reservation.status, reservation.endDate, reservation.terminationTimestamp)

    fun isReservationActive(reservation: BoatSpaceReservation): Boolean =
        isReservationActive(reservation.status, reservation.endDate, reservation.terminationTimestamp)

    fun isReservationTerminated(terminationTimestamp: LocalDateTime?): Boolean = terminationTimestamp != null

    fun isReservationTerminated(reservation: BoatSpaceReservationDetails): Boolean =
        isReservationTerminated(reservation.terminationTimestamp)
}
