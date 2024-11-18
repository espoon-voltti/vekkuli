package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

fun deleteAllReservations(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { handle ->
        handle.execute("DELETE FROM reservation_warning")
        handle.execute("DELETE FROM invoice")
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
        handle.execute("DELETE FROM invoice")
        handle.execute("DELETE FROM payment")
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
        handle.execute("DELETE FROM processed_message")
    }
}

data class CreateReservationParams(
    val timeProvider: TimeProvider,
    val citizenId: UUID,
    val boatSpaceId: Int = 1,
    val boatId: Int = 1,
    val validity: ReservationValidity = ReservationValidity.FixedTerm,
    val reserverId: UUID = citizenId,
    val status: ReservationStatus = ReservationStatus.Payment,
    val startDate: LocalDate = timeProvider.getCurrentDate(),
    val endDate: LocalDate = timeProvider.getCurrentDate().plusDays(365),
)

@Service
class TestUtils(
    private val reservationService: BoatReservationService
) {
    fun createReservationInConfirmedState(params: CreateReservationParams): BoatSpaceReservation {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                params.reserverId,
                params.citizenId,
                params.boatSpaceId,
                startDate = params.startDate,
                endDate = params.endDate,
            )
        reservationService.updateBoatInBoatSpaceReservation(
            madeReservation.id,
            params.boatId,
            params.reserverId,
            params.status,
            params.validity,
            startDate = params.startDate,
            endDate = params.endDate,
        )
        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(params.citizenId, "1", 1, 24.0, "1")
            )
        reservationService.handleReservationPaymentResult(payment.id, true)
        return madeReservation
    }

    fun createReservationInRenewState(params: CreateReservationParams): BoatSpaceReservation =
        createReservationWithBoat(
            reservationService,
            params.reserverId,
            params.citizenId,
            params.boatSpaceId,
            params.timeProvider,
            params.boatId,
            ReservationStatus.Renewal
        )

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
        invoiceService: BoatSpaceInvoiceService,
        reserverId: UUID,
        boatSpaceId: Int = 1,
        boatId: Int = 1,
        invoiceNumber: Int = 1,
    ): BoatSpaceReservation {
        val madeReservation =
            createReservationWithBoat(
                reservationService,
                reserverId,
                reserverId,
                boatSpaceId,
                timeProvider,
                boatId,
                ReservationStatus.Invoiced
            )
        val invoiceData = invoiceService.createInvoiceData(madeReservation.id, reserverId)

        val invoice =
            invoiceService.createInvoice(
                invoiceData!!,
                reserverId,
                madeReservation.id
            )
        return madeReservation
    }

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
        return reservationService.updateBoatInBoatSpaceReservation(
            madeReservation.id,
            boatId,
            reserverId,
            state,
            ReservationValidity.FixedTerm,
            startDate = timeProvider.getCurrentDate(),
            endDate = timeProvider.getCurrentDate().plusDays(365)
        )
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

    fun createInvoiceWithTestParameters(
        citizenService: CitizenService,
        invoiceService: BoatSpaceInvoiceService,
        timeProvider: TimeProvider,
        citizenId: UUID,
    ): Invoice {
        val citizen = citizenService.getCitizen(citizenId)!!
        val (invoice, payment) =
            invoiceService.createInvoice(
                InvoiceData(
                    invoiceNumber = 1L,
                    dueDate = timeProvider.getCurrentDate().plusDays(14),
                    ssn =
                        citizen.nationalId,
                    firstnames = citizen.firstName,
                    lastname = citizen.lastName,
                    street = citizen.streetAddress,
                    post = citizen.postOffice,
                    postalCode = citizen.postalCode,
                    mobilePhone = citizen.phone,
                    email = citizen.email,
                    priceCents = 100,
                    vat = 24,
                    startDate = LocalDate.of(2021, 1, 1),
                    endDate = LocalDate.of(2021, 12, 31),
                    description = "",
                    orgId = "",
                    registerNumber = "",
                    contactPerson = "",
                    language = "FI",
                ),
                citizenId,
                1
            )

        return invoice
    }
}
