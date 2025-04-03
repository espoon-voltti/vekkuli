package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.invoice.BoatSpaceInvoiceService
import fi.espoo.vekkuli.boatSpace.invoice.InvoiceData
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.config.BoatSpaceConfig.getInvoiceDueDate
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.LocalDateRange
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
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

fun deleteAllBoatSpaces(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { handle ->
        handle.execute("DELETE FROM boat_space")
    }
}

fun deleteAllPayments(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { handle ->
        handle.execute("DELETE FROM invoice")
        handle.execute("DELETE FROM payment")
    }
}

fun deleteAllOrganizations(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { handle ->
        handle.execute("DELETE FROM organization")
    }
    deleteAllOrganizationMembers(jdbi)
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
    private val reservationService: BoatReservationService,
    private val timeProvider: TimeProvider,
    private var seasonalService: SeasonalService
) {
    fun createReservationInConfirmedState(params: CreateReservationParams): BoatSpaceReservationDetails {
        var madeReservation =
            reservationService.insertBoatSpaceReservation(
                params.reserverId,
                params.citizenId,
                params.boatSpaceId,
                CreationType.New,
                startDate = params.startDate,
                endDate = params.endDate,
                validity = ReservationValidity.FixedTerm,
            )
        madeReservation =
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
            reservationService.upsertCreatedPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(params.citizenId, "1", 1, 24.0, "1", PaymentType.OnlinePayment)
            )
        reservationService.handlePaymentResult(payment.id, true)
        return reservationService.getBoatSpaceReservation(madeReservation.id) ?: throw IllegalStateException("Reservation not found")
    }

    fun createReservationInRenewState(params: CreateReservationParams): BoatSpaceReservation =
        createReservationWithBoat(
            reservationService,
            params.reserverId,
            params.citizenId,
            params.boatSpaceId,
            params.timeProvider,
            params.boatId,
            ReservationStatus.Info
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
        creationType: CreationType = CreationType.New,
    ): BoatSpaceReservation {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                reserverId,
                citizenId,
                boatSpaceId,
                creationType,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate().plusDays(365),
                validity = ReservationValidity.FixedTerm,
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
        citizenId: UUID,
        boatSpaceId: Int = 1,
        creationType: CreationType = CreationType.New
    ): BoatSpaceReservation {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                citizenId,
                citizenId,
                boatSpaceId,
                creationType,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate().plusDays(365),
                validity = ReservationValidity.FixedTerm,
            )
        return madeReservation
    }

    fun createInvoiceWithTestParameters(
        reserverService: ReserverService,
        invoiceService: BoatSpaceInvoiceService,
        timeProvider: TimeProvider,
        citizenId: UUID,
        reservationId: Int = 1
    ): Invoice {
        val citizen = reserverService.getCitizen(citizenId)!!
        val (invoice, payment) =
            invoiceService.createInvoice(
                InvoiceData(
                    invoiceNumber = 1L,
                    dueDate = getInvoiceDueDate(timeProvider),
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
                    startDate = LocalDate.of(2021, 1, 1),
                    endDate = LocalDate.of(2021, 12, 31),
                    description = "",
                    orgId = "",
                    language = "fi",
                    type = BoatSpaceType.Slip,
                    orgName = null
                ),
                citizenId,
                reservationId
            )

        return invoice
    }

    fun createOrganization(
        name: String,
        organizationService: OrganizationService,
    ): Organization =
        organizationService.insertOrganization(
            businessId = "1234567890",
            name = name,
            phone = "",
            email = "",
            streetAddress = "",
            streetAddressSv = "",
            postalCode = "",
            postOffice = "",
            municipalityCode = 1,
            billingName = "",
            billingStreetAddress = "",
            billingPostalCode = "",
            billingPostOffice = "",
            postOfficeSv = ""
        )

    fun moveTimeToNextReservationPeriodStart(
        boatSpaceType: BoatSpaceType,
        operation: ReservationOperation,
        isEspooCitizen: Boolean = true,
        addDays: Long? = null
    ) {
        val today = timeProvider.getCurrentDate()
        val period =
            getReservationPeriod(isEspooCitizen, boatSpaceType, operation).firstOrNull { it.startDate.isAfter(today) }
                ?: throw IllegalStateException("No future period found")
        moveTimeToPeriod(period.startDate.month.value, period.startDate.dayOfMonth, period.startDate.year, addDays)
    }

    fun moveTimeToReservationPeriodEnd(
        boatSpaceType: BoatSpaceType,
        operation: ReservationOperation,
        isEspooCitizen: Boolean = true,
        addDays: Long? = null
    ) {
        val year = if (operation == ReservationOperation.Renew) 2026 else 2025
        val period = getReservationPeriod(isEspooCitizen, boatSpaceType, operation).first()
        moveTimeToPeriod(period.endDate.month.value, period.endDate.dayOfMonth, year, addDays)
    }

    private fun moveTimeToPeriod(
        month: Int,
        day: Int,
        year: Int,
        addDays: Long? = null
    ) {
        var date = LocalDateTime.of(year, month, day, 12, 0, 0)
        if (addDays != null) {
            date = date.plusDays(addDays)
        }
        mockTimeProvider(timeProvider, date)
    }

    fun getReservationPeriod(
        isEspooCitizen: Boolean,
        boatSpaceType: BoatSpaceType,
        operation: ReservationOperation
    ): List<ReservationPeriod> {
        val periods = seasonalService.getReservationPeriods()
        return periods.filter {
            it.boatSpaceType == boatSpaceType &&
                it.operation == operation &&
                it.isEspooCitizen == isEspooCitizen
        }
    }

    fun getNextReservationPeriodDateRange(
        isEspooCitizen: Boolean,
        boatSpaceType: BoatSpaceType,
        operation: ReservationOperation,
    ): LocalDateRange {
        val today = timeProvider.getCurrentDate()
        val period =
            getReservationPeriod(isEspooCitizen, boatSpaceType, operation).firstOrNull { it.startDate.isAfter(today) }
                ?: throw IllegalStateException("No future period found")
        return LocalDateRange(period.startDate, period.endDate)
    }
}
