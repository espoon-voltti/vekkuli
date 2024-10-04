package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.ReserveBoatSpaceInput
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
    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
    }

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Test
    fun `should get correct reservation with citizen`() {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                citizenId,
                citizenId,
                1,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
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
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
            )
        val boatId = 1
        val updatedReservation =
            reservationService.updateBoatInBoatSpaceReservation(
                madeReservation.id,
                boatId,
                citizenId,
                ReservationStatus.Payment,
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
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
            )
        val reservation = reservationService.getUnfinishedReservationForCitizen(citizenId)
        assertEquals(madeReservation.id, reservation?.id, "reservation is the same")
    }

    @Test
    fun `should handle payment result`() {
        val madeReservation = createReservationInPaymentState(reservationService, citizenId)

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
        val madeReservation = createReservationInPaymentState(reservationService, citizenId, 1)

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
        createReservationInConfirmedState(reservationService, citizenId, 1, 1)
        createReservationInPaymentState(reservationService, UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49"), 2, 3)
        createReservationInInfoState(reservationService, citizenId, 3)
        // Location id 2 and amenity type walk beam
        createReservationInConfirmedState(reservationService, UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"), 200, 2)
        // Location id 2 and amenity type beam
        createReservationInConfirmedState(reservationService, UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"), 177, 2)
        // Location id 4 and amenity type walk beam
        createReservationInConfirmedState(reservationService, UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"), 725, 2)

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
        createReservationInConfirmedState(reservationService, citizenId, 1, 1)
        createReservationInPaymentState(reservationService, UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49"), 2, 3)

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
        createReservationInConfirmedState(reservationService, citizenId, 1, 1)
        createReservationInPaymentState(reservationService, UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49"), 2, 3)
        createReservationInConfirmedState(reservationService, UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"), 3, 2)

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
        createReservationInConfirmedState(reservationService, citizenId, 1, 1)
        createReservationInPaymentState(reservationService, UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49"), 2, 3)

        val madeReservation = createReservationInPaymentState(reservationService, citizenId, 3)

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
        createReservationInConfirmedState(reservationService, citizenId, spaceInSectionB, 1)
        createReservationInConfirmedState(reservationService, citizenId, spaceInSectionD, 2)
        createReservationInConfirmedState(reservationService, citizenId, spaceInSectionE, 3)

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
        createReservationInConfirmedState(reservationService, citizenId, 1, 1)
        createReservationInConfirmedState(reservationService, UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"), 177, 2)
        createReservationInPaymentState(reservationService, UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49"), 2, 3)

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
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
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
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
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
            ReservationStatus.Invoiced
        )

        reservationService.markInvoicePaid(newReservation.id, LocalDate.now(), "")

        val reservation = reservationService.getBoatSpaceReservation(newReservation.id)

        assertEquals(ReservationStatus.Confirmed, reservation?.status, "Reservation is marked as paid")
    }
}
