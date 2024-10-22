package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.util.*
import kotlin.test.assertContains
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationServiceIntegrationTests : IntegrationTestBase() {
    val espooCitizenId = UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49")
    val helsinkiCitizenId = UUID.fromString("1128bd21-fbbc-4e9a-8658-dc2044a64a58")

    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
    }

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var citizenService: CitizenService

    @Test
    fun `first place should be indefinite for Espoo citizens`() {
        val result = reservationService.canReserveANewSlip(espooCitizenId)
        if (result is ReservationResult.Success) {
            assertEquals(LocalDate.of(2025, 1, 31), result.data.endDate)
            assertEquals(ReservationValidity.Indefinite, result.data.reservationValidity)
        } else {
            throw AssertionError("canReserveANewSlip failed")
        }
    }

    @Test
    fun `second place should be fixed term for Espoo citizens`() {
        val madeReservation = createReservationInPaymentState(timeProvider, reservationService, espooCitizenId, 1)
        reservationService.reserveBoatSpace(
            espooCitizenId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = ""
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDateTime().toLocalDate().minusWeeks(1),
            timeProvider.getCurrentDateTime().toLocalDate().plusWeeks(1),
        )
        val result = reservationService.canReserveANewSlip(espooCitizenId)
        if (result is ReservationResult.Success) {
            assertEquals(LocalDate.of(2024, 12, 31), result.data.endDate)
            assertEquals(ReservationValidity.FixedTerm, result.data.reservationValidity)
        } else {
            throw AssertionError("canReserveANewSlip failed")
        }
    }

    @Test
    fun `third place should fail for Espoo citizens`() {
        val madeReservation1 = createReservationInPaymentState(timeProvider, reservationService, espooCitizenId, 1)
        reservationService.reserveBoatSpace(
            espooCitizenId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation1.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = ""
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDateTime().toLocalDate().minusWeeks(1),
            timeProvider.getCurrentDateTime().toLocalDate().plusWeeks(1),
        )
        val madeReservation2 = createReservationInPaymentState(timeProvider, reservationService, espooCitizenId, 1)
        reservationService.reserveBoatSpace(
            espooCitizenId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation2.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = ""
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDateTime().toLocalDate().minusWeeks(1),
            timeProvider.getCurrentDateTime().toLocalDate().plusWeeks(1),
        )
        val result = reservationService.canReserveANewSlip(espooCitizenId)
        if (result is ReservationResult.Failure) {
            assertEquals(ReservationResultErrorCode.MaxReservations, result.errorCode)
        } else {
            throw AssertionError("canReserveANewSlip succeeded, but it should fail")
        }
    }

    @Test
    fun `first place should be fixed term for Helsinki citizens`() {
        val result = reservationService.canReserveANewSlip(helsinkiCitizenId)
        if (result is ReservationResult.Success) {
            assertEquals(LocalDate.of(2024, 12, 31), result.data.endDate)
            assertEquals(ReservationValidity.FixedTerm, result.data.reservationValidity)
        } else {
            throw AssertionError("canReserveANewSlip failed")
        }
    }

    @Test
    fun `should not allow second place for Helsinki citizens`() {
        val madeReservation = createReservationInPaymentState(timeProvider, reservationService, helsinkiCitizenId, 1)
        reservationService.reserveBoatSpace(
            helsinkiCitizenId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = ""
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDateTime().toLocalDate().minusWeeks(1),
            timeProvider.getCurrentDateTime().toLocalDate().plusWeeks(1),
        )
        val result = reservationService.canReserveANewSlip(helsinkiCitizenId)
        if (result is ReservationResult.Failure) {
            assertEquals(ReservationResultErrorCode.MaxReservations, result.errorCode)
        } else {
            throw AssertionError("canReserveANewSlip succeeded, but it should fail")
        }
    }

    @Test
    fun `should get correct reservation with citizen`() {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                citizenId,
                citizenId,
                1,
                startDate = timeProvider.getCurrentDateTime().toLocalDate(),
                endDate = timeProvider.getCurrentDateTime().toLocalDate(),
            )
        val result = reservationService.getReservationWithReserver(madeReservation.id)
        assertEquals(madeReservation.id, result?.id, "reservation is the same")
        assertEquals(madeReservation.reserverId, result?.reserverId, "citizen is the same")
    }

    @Test
    fun `should update boat in reservation`() {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                citizenId,
                citizenId,
                1,
                startDate = timeProvider.getCurrentDateTime().toLocalDate(),
                endDate = timeProvider.getCurrentDateTime().toLocalDate(),
            )
        val boatId = 1
        val updatedReservation =
            reservationService.updateBoatInBoatSpaceReservation(
                madeReservation.id,
                boatId,
                citizenId,
                ReservationStatus.Payment,
                ReservationValidity.Indefinite,
                timeProvider.getCurrentDateTime().toLocalDate(),
                timeProvider.getCurrentDateTime().toLocalDate(),
            )
        val reservation = reservationService.getReservationWithReserver(madeReservation.id)
        assertEquals(madeReservation.id, updatedReservation.id, "reservation is the same")
        assertEquals(madeReservation.reserverId, updatedReservation.reserverId, "citizen is the same")
        assertEquals(boatId, reservation?.boatId, "boat is updated")
    }

    @Test
    fun `should get correct reservation for citizen`() {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                citizenId,
                citizenId,
                1,
                startDate = timeProvider.getCurrentDateTime().toLocalDate(),
                endDate = timeProvider.getCurrentDateTime().toLocalDate(),
            )
        val reservation = reservationService.getUnfinishedReservationForCitizen(citizenId)
        assertEquals(madeReservation.id, reservation?.id, "reservation is the same")
    }

    @Test
    fun `should handle payment result`() {
        val madeReservation = createReservationInPaymentState(timeProvider, reservationService, citizenId)

        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = citizenId,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1",
                )
            )
        val reservation = reservationService.getBoatSpaceReservation(madeReservation.id)
        assertNotNull(reservation, "reservation is found")
        assertEquals(madeReservation.id, reservation.id, "reservation is the same")
        assertEquals(payment.citizenId, madeReservation.reserverId, "payment is added for correct citizen")
        assertEquals(reservation.status, ReservationStatus.Payment, "reservation status is correct")
        assertEquals(payment.reservationId, madeReservation.id, "payment is linked to the reservation")
    }

    @Test
    fun `should add reservation warnings on reservation with issues`() {
        val madeReservation = createReservationInPaymentState(timeProvider, reservationService, citizenId, 1)

        reservationService.reserveBoatSpace(
            citizenId,
            ReserveBoatSpaceInput(
                madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                email = "email@email.com",
                phone = "0403849283",
            ),
            ReservationStatus.Payment,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDateTime().toLocalDate(),
            timeProvider.getCurrentDateTime().toLocalDate()
        )
        val reservation =
            reservationService
                .getBoatSpaceReservations(
                    BoatSpaceReservationFilter(
                        sortBy = BoatSpaceFilterColumn.PLACE,
                        ascending = true,
                    )
                ).first()

        assertEquals(3, reservation.warnings.size, "Warnings should be present")
        assertEquals(
            listOf("BoatFutureOwner", "BoatLength", "BoatWidth"),
            reservation.warnings.sorted(),
            "Correct warnings should be present"
        )
    }

    @Test
    fun `should get correct reservations with filter`() {
        // Location id 1 and amenity type beam
        createReservationInConfirmedState(timeProvider, reservationService, citizenId, 1, 1)
        createReservationInPaymentState(timeProvider, reservationService, UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49"), 2, 3)
        createReservationInInfoState(timeProvider, reservationService, citizenId, 3)
        // Location id 2 and amenity type walk beam
        createReservationInConfirmedState(timeProvider, reservationService, UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"), 200, 2)
        // Location id 2 and amenity type beam
        createReservationInConfirmedState(timeProvider, reservationService, UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"), 177, 2)
        // Location id 4 and amenity type walk beam
        createReservationInConfirmedState(timeProvider, reservationService, UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"), 725, 2)

        val reservations =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    harbor = listOf(1, 2),
                    amenity = listOf(BoatSpaceAmenity.Beam, BoatSpaceAmenity.WalkBeam)
                )
            )

        assertEquals(4, reservations.size, "reservations are out filtered correctly")
        assertEquals(listOf(177, 200, 2, 1), reservations.map { it.boatSpaceId }, "correct reservations are returned")
    }

    @Test
    fun `should filter by payment status`() {
        createReservationInConfirmedState(timeProvider, reservationService, citizenId, 1, 1)
        createReservationInPaymentState(timeProvider, reservationService, UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49"), 2, 3)

        val unfilteredReservations =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter()
            )

        val unpaidReservations =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    payment = listOf(PaymentFilter.UNPAID)
                )
            )

        val paidReservations =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    payment = listOf(PaymentFilter.PAID)
                )
            )
        assertEquals(2, unfilteredReservations.size, "reservations are filtered correctly")

        assertEquals(1, unpaidReservations.size, "reservations are filtered correctly")
        assertEquals(2, unpaidReservations.first().boatSpaceId, "correct reservation is returned")

        assertEquals(1, paidReservations.size, "reservations are filtered correctly")
        assertEquals(1, paidReservations.first().boatSpaceId, "correct reservation is returned")
    }

    @Test
    fun `should filter by name search`() {
        createReservationInConfirmedState(timeProvider, reservationService, citizenId, 1, 1)
        createReservationInPaymentState(timeProvider, reservationService, UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49"), 2, 3)
        createReservationInConfirmedState(timeProvider, reservationService, UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"), 3, 2)

        val reservationsByFirstName =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    nameSearch = "leo"
                )
            )

        assertEquals(1, reservationsByFirstName.size, "reservations are filtered correctly")
        assertEquals("Leo Korhonen", reservationsByFirstName.first().name, "correct reservation is returned")

        val reservationsByLastName =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    nameSearch = "VIRTA"
                )
            )

        assertEquals(2, reservationsByLastName.size, "reservations are filtered correctly")
        val reservationsNames = reservationsByLastName.map { "${it.name}" }
        assertContains(reservationsNames, "Mikko Virtanen")
        assertContains(reservationsNames, "Olivia Virtanen")
    }

    @Test
    fun `should filter reservations that have warnings`() {
        createReservationInConfirmedState(timeProvider, reservationService, citizenId, 1, 1)
        createReservationInPaymentState(timeProvider, reservationService, UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49"), 2, 3)

        val madeReservation = createReservationInPaymentState(timeProvider, reservationService, citizenId, 3)

        reservationService.reserveBoatSpace(
            citizenId,
            ReserveBoatSpaceInput(
                madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                email = "email@email.com",
                phone = "0403849283"
            ),
            ReservationStatus.Payment,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDateTime().toLocalDate(),
            timeProvider.getCurrentDateTime().toLocalDate()
        )

        val reservationsWithWarnings =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    warningFilter = true
                )
            )

        assertEquals(1, reservationsWithWarnings.size, "reservations are filtered correctly")
        assertEquals(3, reservationsWithWarnings.first().boatSpaceId, "correct reservation is returned")
    }

    @Test
    fun `should filter by section`() {
        val spaceInSectionB = 1
        val spaceInSectionD = 64
        val spaceInSectionE = 85
        createReservationInConfirmedState(timeProvider, reservationService, citizenId, spaceInSectionB, 1)
        createReservationInConfirmedState(timeProvider, reservationService, citizenId, spaceInSectionD, 2)
        createReservationInConfirmedState(timeProvider, reservationService, citizenId, spaceInSectionE, 3)

        val reservationsBySection =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    sectionFilter = listOf("B", "D")
                )
            )
        val sections = reservationsBySection.map { it.section }
        assertEquals(2, reservationsBySection.size, "reservations are filtered correctly")
        assertContains(sections, "B")
        assertContains(sections, "D")
    }

    @Test
    fun `should sort reservations correctly`() {
        createReservationInConfirmedState(timeProvider, reservationService, citizenId, 1, 1)
        createReservationInConfirmedState(timeProvider, reservationService, UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"), 177, 2)
        createReservationInPaymentState(timeProvider, reservationService, UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49"), 2, 3)

        val reservations =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    sortBy = BoatSpaceFilterColumn.PLACE,
                    ascending = true,
                )
            )

        assertEquals(3, reservations.size, "reservations are filtered correctly")
        assertEquals(listOf(1, 2, 177), reservations.map { it.boatSpaceId }, "reservations are sorted by place and amenity")
    }

    @Test
    fun `should return boat space related to reservation`() {
        val boatSpaceId = 1
        val newReservation =
            reservationService.insertBoatSpaceReservation(
                citizenId,
                citizenId,
                boatSpaceId,
                startDate = timeProvider.getCurrentDateTime().toLocalDate(),
                endDate = timeProvider.getCurrentDateTime().toLocalDate(),
            )
        val boatSpace = reservationService.getBoatSpaceRelatedToReservation(newReservation.id)
        assertEquals(boatSpaceId, boatSpace?.id, "Correct boat space is fetched")
    }

    @Test
    fun `should mark the reservation as paid`() {
        val boatSpaceId = 1

        val employeeId = UUID.fromString("94833b54-132b-4ab8-b841-60df45809b3e")

        val newReservation =
            reservationService.insertBoatSpaceReservationAsEmployee(
                employeeId,
                boatSpaceId,
                startDate = timeProvider.getCurrentDateTime().toLocalDate(),
                endDate = timeProvider.getCurrentDateTime().toLocalDate(),
            )

        reservationService.reserveBoatSpace(
            citizenId,
            ReserveBoatSpaceInput(
                newReservation.id,
                boatId = 0,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                email = "test@email.com",
                phone = "1234567890"
            ),
            ReservationStatus.Invoiced,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDateTime().toLocalDate(),
            timeProvider.getCurrentDateTime().toLocalDate()
        )

        reservationService.markInvoicePaid(newReservation.id, timeProvider.getCurrentDateTime().toLocalDate(), "")

        val reservation = reservationService.getBoatSpaceReservation(newReservation.id)
        assertEquals(ReservationStatus.Confirmed, reservation?.status, "Reservation is marked as paid")
    }

    @Test
    fun `should terminate the reservation and set ending date to now`() {
        val boatSpaceId = 1

        val oliviaCitizenId = UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49")
        val endDate = timeProvider.getCurrentDateTime().toLocalDate().plusWeeks(2)
        val citizen = citizenService.getCitizen(oliviaCitizenId)

        // Keep this here to make sure Citizen is present
        assertNotNull(citizen, "Citizen is not null")

        val newReservation =
            reservationService.insertBoatSpaceReservation(
                citizen.id,
                citizen.id,
                boatSpaceId,
                startDate = timeProvider.getCurrentDateTime().toLocalDate().minusWeeks(2),
                endDate = endDate
            )

        reservationService.reserveBoatSpace(
            citizenId,
            ReserveBoatSpaceInput(
                newReservation.id,
                boatId = 0,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                email = "test@email.com",
                phone = "1234567890"
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.FixedTerm,
            newReservation.startDate,
            newReservation.endDate
        )

        val originalReservation = reservationService.getBoatSpaceReservation(newReservation.id)

        assertEquals(ReservationStatus.Confirmed, originalReservation?.status, "Reservation starts as Confirmed")
        assertEquals(endDate, originalReservation?.endDate, "Reservation endDate is $endDate")

        reservationService.terminateBoatSpaceReservation(newReservation.id, citizen)

        val terminatedReservation = reservationService.getBoatSpaceReservation(newReservation.id)

        assertEquals(ReservationStatus.Cancelled, terminatedReservation?.status, "Reservation is marked as Cancelled")
        assertEquals(timeProvider.getCurrentDateTime().toLocalDate(), terminatedReservation?.endDate, "End date is set to now")
    }

    @Test
    fun `should return expired reservations`() {
        val reservation = createReservationInConfirmedState(timeProvider, reservationService, citizenId, 1, 1)
        val citizen = citizenService.getCitizen(citizenId)
        // Keep this here to make sure Citizen is present
        assertNotNull(citizen, "Citizen is not null")

        val noExpiredReservations = reservationService.getExpiredBoatSpaceReservationsForCitizen(citizenId)
        assertEquals(0, noExpiredReservations.size)

        reservationService.terminateBoatSpaceReservation(reservation.id, citizen)

        val expiredReservations = reservationService.getExpiredBoatSpaceReservationsForCitizen(citizenId)
        assertEquals(1, expiredReservations.size)
        assertEquals(ReservationStatus.Cancelled, expiredReservations.first().status, "Reservation is marked as Cancelled")
        assertEquals(LocalDate.now(), expiredReservations.first().endDate, "End date is set to now")
    }
}
