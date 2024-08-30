package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
class IntegrationTest : IntegrationTestBase() {
    @Test
    fun `should get correct reservation with citizen`() {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                UUID.fromString("f5d377ea-5547-11ef-a1c7-7f2b94cf9afd"),
                1,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                status = ReservationStatus.Info
            )
        val result = reservationService.getReservationWithCitizen(madeReservation.id)
        assertEquals("reservation is the same", madeReservation.id, result?.id)
        assertEquals("citizen is the same", madeReservation.citizenId, result?.citizenId)
    }

    @Test
    fun `should update boat in reservation`() {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                UUID.fromString("f5d377ea-5547-11ef-a1c7-7f2b94cf9afd"),
                1,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                status = ReservationStatus.Info
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
                UUID.fromString("f5d377ea-5547-11ef-a1c7-7f2b94cf9afd"),
                1,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                status = ReservationStatus.Info
            )
        val reservation = reservationService.getReservationForCitizen(UUID.fromString("f5d377ea-5547-11ef-a1c7-7f2b94cf9afd"))
        assertEquals("reservation is the same", madeReservation.id, reservation?.id)
    }
}
