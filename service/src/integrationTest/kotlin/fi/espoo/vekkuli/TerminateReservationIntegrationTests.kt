package fi.espoo.vekkuli
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.SentMessageRepository
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
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TerminateReservationIntegrationTests : IntegrationTestBase() {
    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
        deleteAllEmails(jdbi)
    }

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var terminateService: TerminateBoatSpaceReservationService

    @Autowired
    lateinit var citizenService: CitizenService

    @Autowired
    lateinit var messageRepository: SentMessageRepository

    @Autowired lateinit var emailEnv: EmailEnv

    @Test
    fun `should terminate the reservation and set ending date to now`() {
        val boatSpaceId = 1

        val oliviaCitizenId = UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49")
        val endDate = timeProvider.getCurrentDate().plusWeeks(2)
        val citizen = citizenService.getCitizen(oliviaCitizenId)

        // Keep this here to make sure Citizen is present
        assertNotNull(citizen, "Citizen is not null")

        val newReservation =
            reservationService.insertBoatSpaceReservation(
                citizen.id,
                citizen.id,
                boatSpaceId,
                startDate = timeProvider.getCurrentDate().minusWeeks(2),
                endDate = endDate
            )

        reservationService.reserveBoatSpace(
            this.citizenIdLeo,
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

        terminateService.terminateBoatSpaceReservation(newReservation.id, citizen)

        val terminatedReservation = reservationService.getBoatSpaceReservation(newReservation.id)

        assertEquals(ReservationStatus.Cancelled, terminatedReservation?.status, "Reservation is marked as Cancelled")
        assertEquals(timeProvider.getCurrentDate(), terminatedReservation?.endDate, "End date is set to now")
    }

    @Test
    fun `should send email notice to person terminating the reservation`() {
        val citizen = citizenService.getCitizen(this.citizenIdOlivia)
        val reservation = createReservationInConfirmedState(timeProvider, reservationService, this.citizenIdOlivia, 1, 1)

        // Keep this here to make sure Citizen is present
        assertNotNull(citizen, "Citizen is not null")
        terminateService.terminateBoatSpaceReservation(reservation.id, citizen)
        val sentEmails = messageRepository.getUnsentEmailsAndSetToProcessing()
        assertTrue(
            sentEmails.any { it.recipientAddress == citizen.email },
            "Email is set to be sent to the citizen"
        )
    }

    @Test
    fun `should send email notice to employee email`() {
        val citizen = citizenService.getCitizen(this.citizenIdOlivia)
        val reservation = createReservationInConfirmedState(timeProvider, reservationService, this.citizenIdOlivia, 1, 1)

        // Keep this here to make sure Citizen is present
        assertNotNull(citizen, "Citizen is not null")
        terminateService.terminateBoatSpaceReservation(reservation.id, citizen)
        val sentEmails = messageRepository.getUnsentEmailsAndSetToProcessing()
        assertTrue(
            sentEmails.any { it.recipientAddress == emailEnv.employeeAddress },
            "Email is set to be sent to the employee address"
        )
    }
}
