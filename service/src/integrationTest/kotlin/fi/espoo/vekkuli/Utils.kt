package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.BoatSpaceReservation
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import java.util.*

fun deleteAllReservations(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { handle ->
        handle.execute("DELETE FROM reservation_warning")
        handle.execute("DELETE FROM payment")
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
        handle.execute("DELETE FROM invoice")
    }
}

fun deleteAllOrganizationMembers(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { handle ->
        handle.execute("DELETE FROM organization_member")
    }
}

fun deleteAllInvoices(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { handle ->
        handle.execute("DELETE FROM invoice")
    }
}

fun deleteAllEmails(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { handle ->
        handle.execute("DELETE FROM sent_message")
    }
}

fun createReservationInConfirmedState(
    timeProvider: TimeProvider,
    reservationService: BoatReservationService,
    citizenId: UUID,
    boatSpaceId: Int,
    boatId: Int,
    validity: ReservationValidity = ReservationValidity.FixedTerm
): BoatSpaceReservation {
    val madeReservation =
        reservationService.insertBoatSpaceReservation(
            citizenId,
            citizenId,
            boatSpaceId,
            startDate = timeProvider.getCurrentDate(),
            endDate = timeProvider.getCurrentDate().plusDays(365),
        )
    reservationService.updateBoatInBoatSpaceReservation(
        madeReservation.id,
        boatId,
        citizenId,
        ReservationStatus.Payment,
        validity,
        startDate = timeProvider.getCurrentDate(),
        endDate = timeProvider.getCurrentDate().plusDays(365),
    )
    val payment =
        reservationService.addPaymentToReservation(
            madeReservation.id,
            CreatePaymentParams(citizenId, "1", 1, 24.0, "1")
        )
    reservationService.handleReservationPaymentResult(payment.id, true)
    return madeReservation
}

fun createReservationInPaymentState(
    timeProvider: TimeProvider,
    reservationService: BoatReservationService,
    reserverId: UUID,
    citizenId: UUID = reserverId,
    boatSpaceId: Int = 1,
    boatId: Int = 1
): BoatSpaceReservation = createReservationWithBoat(reservationService, reserverId, citizenId, boatSpaceId, timeProvider, boatId)

fun createReservationInPaymentState(
    timeProvider: TimeProvider,
    reservationService: BoatReservationService,
    reserverId: UUID,
    boatSpaceId: Int = 1,
    boatId: Int = 1
): BoatSpaceReservation = createReservationWithBoat(reservationService, reserverId, reserverId, boatSpaceId, timeProvider, boatId)

fun createReservationInInvoiceState(
    timeProvider: TimeProvider,
    reservationService: BoatReservationService,
    reserverId: UUID,
    boatSpaceId: Int = 1,
    boatId: Int = 1
): BoatSpaceReservation =
    createReservationWithBoat(reservationService, reserverId, reserverId, boatSpaceId, timeProvider, boatId, ReservationStatus.Invoiced)

private fun createReservationWithBoat(
    reservationService: BoatReservationService,
    reserverId: UUID,
    citizenId: UUID,
    boatSpaceId: Int,
    timeProvider: TimeProvider,
    boatId: Int,
    state: ReservationStatus = ReservationStatus.Payment,
): BoatSpaceReservation {
    val madeReservation =
        reservationService.insertBoatSpaceReservation(
            reserverId,
            citizenId,
            boatSpaceId,
            startDate = timeProvider.getCurrentDate(),
            endDate = timeProvider.getCurrentDate().plusDays(365),
        )
    reservationService.updateBoatInBoatSpaceReservation(
        madeReservation.id,
        boatId,
        reserverId,
        state,
        ReservationValidity.FixedTerm,
        startDate = timeProvider.getCurrentDate(),
        endDate = timeProvider.getCurrentDate().plusDays(365)
    )
    return madeReservation
}

fun createReservationInInfoState(
    timeProvider: TimeProvider,
    reservationService: BoatReservationService,
    citizenId: UUID,
    boatSpaceId: Int = 1,
): BoatSpaceReservation {
    val madeReservation =
        reservationService.insertBoatSpaceReservation(
            citizenId,
            citizenId,
            boatSpaceId,
            startDate = timeProvider.getCurrentDate(),
            endDate = timeProvider.getCurrentDate().plusDays(365),
        )
    return madeReservation
}
