package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.BoatSpaceReservation
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.service.BoatReservationService
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import java.time.LocalDate
import java.util.*

fun deleteAllReservations(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { handle ->
        handle.execute("DELETE FROM reservation_warning")
        handle.execute("DELETE FROM boat_space_reservation")
    }
}

fun deleteAllBoats(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { handle ->
        handle.execute("DELETE FROM boat")
    }
}

fun deleteAllPayments(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { handle ->
        handle.execute("DELETE FROM payment")
    }
}

fun deleteAllOrganizationMembers(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { handle ->
        handle.execute("DELETE FROM organization_member")
    }
}

fun createReservationInConfirmedState(
    reservationService: BoatReservationService,
    citizenId: UUID,
    boatSpaceId: Int,
    boatId: Int
): BoatSpaceReservation {
    val madeReservation =
        reservationService.insertBoatSpaceReservation(
            citizenId,
            boatSpaceId,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(365),
        )
    reservationService.updateBoatInBoatSpaceReservation(madeReservation.id, boatId, citizenId, ReservationStatus.Payment)
    val payment =
        reservationService.addPaymentToReservation(
            madeReservation.id,
            CreatePaymentParams(citizenId, "1", 1, 24.0, "1")
        )
    reservationService.handleReservationPaymentResult(payment.id, true)
    return madeReservation
}

fun createReservationInPaymentState(
    reservationService: BoatReservationService,
    citizenId: UUID,
    boatSpaceId: Int = 1,
    boatId: Int = 1
): BoatSpaceReservation {
    val madeReservation =
        reservationService.insertBoatSpaceReservation(
            citizenId,
            boatSpaceId,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(365),
        )
    reservationService.updateBoatInBoatSpaceReservation(madeReservation.id, boatId, citizenId, ReservationStatus.Payment)
    return madeReservation
}

fun createReservationInInfoState(
    reservationService: BoatReservationService,
    citizenId: UUID,
    boatSpaceId: Int = 1,
): BoatSpaceReservation {
    val madeReservation =
        reservationService.insertBoatSpaceReservation(
            citizenId,
            boatSpaceId,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(365),
        )
    return madeReservation
}
