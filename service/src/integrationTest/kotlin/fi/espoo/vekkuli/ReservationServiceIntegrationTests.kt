package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatReservationService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.util.*
import kotlin.test.DefaultAsserter.assertEquals

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationServiceIntegrationTests : IntegrationTestBase() {
    @Autowired
    lateinit var reservationService: BoatReservationService

    @Test
    fun `should get correct reservation with citizen`() {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                citizenId,
                1,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
            )
        val result = reservationService.getReservationWithCitizen(madeReservation.id)
        assertEquals("reservation is the same", madeReservation.id, result?.id)
        assertEquals("citizen is the same", madeReservation.citizenId, result?.citizenId)
    }

    @Test
    fun `should update boat in reservation`() {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
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
            )
        val reservation = reservationService.getReservationWithCitizen(madeReservation.id)
        assertEquals("reservation is the same", madeReservation.id, updatedReservation.id)
        assertEquals("citizen is the same", madeReservation.citizenId, updatedReservation.citizenId)
        assertEquals("boat is updated", boatId, reservation?.boatId)
    }

    @Test
    fun `should get correct reservation for citizen`() {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                citizenId,
                1,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
            )
        val reservation = reservationService.getReservationForCitizen(citizenId)
        assertEquals("reservation is the same", madeReservation.id, reservation?.id)
    }

    @Test
    fun `should handle payment result`() {
        val madeReservation = createReservationInPaymentState(reservationService, citizenId)

        val (payment, reservation) =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = citizenId,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1"
                )
            )
        assertEquals("reservation is the same", madeReservation.id, reservation?.id)
        assertEquals("payment is added for correct citizen", payment.citizenId, madeReservation?.citizenId)
        assertEquals("payment is added to the reservation", reservation?.paymentId, payment.id)
    }

    @Test
    fun `should add reservation warnings on successful payment with issues`() {
        val madeReservation = createReservationInPaymentState(reservationService, citizenId)

        val paymentParams = CreatePaymentParams(citizenId, "1", 1, 24.0, "1")

        val (payment, _) = reservationService.addPaymentToReservation(madeReservation.id, paymentParams)

        reservationService.updateBoatInBoatSpaceReservation(madeReservation.id, 3)

        reservationService.handlePaymentResult(
            mapOf("checkout-stamp" to payment.id.toString()),
            success = true
        )

        val reservation =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    sortBy = BoatSpaceFilterColumn.PLACE,
                    ascending = true,
                )
            ).first()

        assertEquals("Warnings should be present", 3, reservation.warnings.size) // Assuming 2 specific warnings should be added
        assertEquals(
            "Correct warnings should be present",
            listOf("BoatLength", "BoatOwnership", "BoatWidth"),
            reservation.warnings.sorted()
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

        assertEquals("reservations are out filtered correctly", 4, reservations.size)
        assertEquals("correct reservations are returned", listOf(177, 200, 2, 1), reservations.map { it.boatSpaceId })
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

        assertEquals("reservations are filtered correctly", 3, reservations.size)
        assertEquals("reservations are sorted by place and amenity", listOf(1, 2, 177), reservations.map { it.boatSpaceId })
    }
}
