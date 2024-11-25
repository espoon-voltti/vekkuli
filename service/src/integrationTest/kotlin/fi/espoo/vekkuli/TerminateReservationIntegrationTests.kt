package fi.espoo.vekkuli
import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReason
import fi.espoo.vekkuli.boatSpace.terminateReservation.TerminateReservationService
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.SentMessageRepository
import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertNotEquals
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
    private lateinit var messageService: MessageService

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var terminateService: TerminateReservationService

    @Autowired
    lateinit var citizenService: CitizenService

    @Autowired
    lateinit var messageRepository: SentMessageRepository

    @Autowired lateinit var emailEnv: EmailEnv

    @Test
    fun `should terminate the reservation for the owner and set ending date to now`() {
        val boatSpaceId = 1

        val endDate = timeProvider.getCurrentDate().plusWeeks(2)

        val newReservation =
            reservationService.insertBoatSpaceReservation(
                citizenIdOlivia,
                citizenIdOlivia,
                boatSpaceId,
                startDate = timeProvider.getCurrentDate().minusWeeks(2),
                endDate = endDate
            )

        reservationService.reserveBoatSpace(
            citizenIdOlivia,
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
        terminateService.terminateBoatSpaceReservationAsOwner(newReservation.id, citizenIdOlivia)
        val terminatedReservation = reservationService.getBoatSpaceReservation(newReservation.id)

        assertEquals(ReservationStatus.Confirmed, originalReservation?.status, "Reservation starts as Confirmed")
        assertEquals(endDate, originalReservation?.endDate, "Reservation endDate is $endDate")
        assertEquals(ReservationStatus.Cancelled, terminatedReservation?.status, "Reservation is marked as Cancelled")
        assertEquals(timeProvider.getCurrentDate(), terminatedReservation?.endDate, "End date is set to now")
    }

    @Test
    fun `should send email notice to person terminating the reservation`() {
        val citizen = citizenService.getCitizen(this.citizenIdOlivia)
        val reservation = testUtils.createReservationInConfirmedState(CreateReservationParams(timeProvider, this.citizenIdOlivia, 1, 1))

        // Keep this here to make sure Citizen is present
        assertNotNull(citizen, "Citizen is not null")
        terminateService.terminateBoatSpaceReservationAsOwner(reservation.id, citizen.id)
        val sentEmails = messageRepository.getUnsentEmailsAndSetToProcessing()
        assertTrue(
            sentEmails.any { it.recipientAddress == citizen.email },
            "Email is set to be sent to the citizen"
        )
    }

    @Test
    fun `should send email notice to employee email when terminating your own reservation`() {
        val reservation = testUtils.createReservationInConfirmedState(CreateReservationParams(timeProvider, this.citizenIdOlivia, 1, 1))

        terminateService.terminateBoatSpaceReservationAsOwner(reservation.id, citizenIdOlivia)
        val sentEmails = messageRepository.getUnsentEmailsAndSetToProcessing()
        assertTrue(
            sentEmails.any { it.recipientAddress == emailEnv.employeeAddress },
            "Email is set to be sent to the employee address"
        )
    }

    @Test
    fun `should not be able to terminate another citizen reservation as a citizen`() {
        // create the reservation for Leo
        val reservation = testUtils.createReservationInConfirmedState(CreateReservationParams(timeProvider, citizenIdLeo, 1, 1))

        // Try to terminate the reservation as olivia
        val exception =
            assertThrows(Unauthorized::class.java) {
                terminateService.terminateBoatSpaceReservationAsOwner(reservation.id, citizenIdOlivia)
            }
        assertEquals("Unauthorized", exception.message, "termination throws unauthorized exception")
        val terminatedReservation = reservationService.getBoatSpaceReservation(reservation.id)
        assertEquals(ReservationStatus.Confirmed, terminatedReservation?.status, "reservation was not terminated")
    }

    @Test
    fun `should be able to terminate organization reservation as a member of the organisation`() {
        // create the reservation acting as Leo, but using the organization as reserver
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                    1,
                    ReservationValidity.FixedTerm,
                    organizationId
                )
            )

        // Try to terminate the reservation as olivia, Olivia is a member of the test organization (seed.sql)
        terminateService.terminateBoatSpaceReservationAsOwner(reservation.id, citizenIdOlivia)
        val terminatedReservation = reservationService.getBoatSpaceReservation(reservation.id)

        assertEquals(ReservationStatus.Cancelled, terminatedReservation?.status, "reservation was terminated")
    }

    @Test
    fun `should be able to terminate reservation as an employee with a reason, end date and comment and a message`() {
        // Two different scenarios - with different end dates, reasons and comments
        val employeeTerminatorId = userId
        val oliviaEndDate = timeProvider.getCurrentDate().plusWeeks(2)
        val oliviaTerminationReason = ReservationTerminationReason.RuleViolation
        val oliviaTerminationComment = "Olivia's comment"
        val oliviaMessageTitle = "Olivia's message title"
        val oliviaMessageContent = "Olivia's message content"

        val reservationOfOlivia =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                    1,
                    1
                )
            )
        val leoEndDate = timeProvider.getCurrentDate().minusDays(1)
        val leoTerminationReason = ReservationTerminationReason.PaymentViolation
        val reservationOfLeo =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    2,
                    2
                )
            )

        val originalOliviaReservation = reservationService.getBoatSpaceReservation(reservationOfOlivia.id)
        val originalLeoReservation = reservationService.getBoatSpaceReservation(reservationOfLeo.id)

        terminateService.terminateBoatSpaceReservationAsEmployee(
            reservationOfOlivia.id,
            employeeTerminatorId,
            oliviaTerminationReason,
            oliviaEndDate,
            oliviaTerminationComment,
            oliviaMessageTitle,
            oliviaMessageContent
        )

        terminateService.terminateBoatSpaceReservationAsEmployee(
            reservationId = reservationOfLeo.id,
            terminatorId = employeeTerminatorId,
            terminationReason = leoTerminationReason,
            endDate = leoEndDate,
            messageTitle = oliviaMessageTitle,
            messageContent = oliviaMessageContent,
        )

        val terminatedOliviaReservation = reservationService.getBoatSpaceReservation(reservationOfOlivia.id)
        val terminatedLeoReservation = reservationService.getBoatSpaceReservation(reservationOfLeo.id)
        val sentMessage = messageRepository.getMessagesSentToUser(citizenIdOlivia).firstOrNull()

        assertEquals(ReservationStatus.Confirmed, originalOliviaReservation?.status, "Olivia reservation starts as Confirmed")
        assertNotEquals(
            terminatedOliviaReservation?.endDate,
            originalOliviaReservation?.endDate,
            "Olivia reservation endDate starts with different value"
        )
        assertEquals(null, originalOliviaReservation?.terminationReason, "Olivia termination reason starts as null")
        assertEquals(null, originalOliviaReservation?.terminationComment, "Olivia termination comment starts as null")

        assertEquals(ReservationStatus.Cancelled, terminatedOliviaReservation?.status, "Olivia reservation was terminated")
        assertEquals(oliviaEndDate, terminatedOliviaReservation?.endDate, "Olivia end date is set to the given date")
        assertEquals(oliviaTerminationReason, terminatedOliviaReservation?.terminationReason, "Olivia termination reason is set")
        assertEquals(oliviaTerminationComment, terminatedOliviaReservation?.terminationComment, "Olivia termination comment is set")

        assertEquals(oliviaMessageTitle, sentMessage?.subject, "Olivia message title is set right")
        assertEquals(oliviaMessageContent, sentMessage?.body, "Olivia message body is set right")

        // Leo's reservation - same as Olivia's but without comment and with a different end date and reason
        assertEquals(ReservationStatus.Confirmed, originalLeoReservation?.status, "Leo reservation starts as Confirmed")
        assertNotEquals(
            terminatedLeoReservation?.endDate,
            originalLeoReservation?.endDate,
            "Leo reservation endDate starts different value"
        )
        assertEquals(null, originalLeoReservation?.terminationReason, "Leo termination reason starts as null")
        assertEquals(null, originalLeoReservation?.terminationComment, "Leo termination comment starts as null")

        assertEquals(ReservationStatus.Cancelled, terminatedLeoReservation?.status, "Leo reservation was terminated")
        assertEquals(leoEndDate, terminatedLeoReservation?.endDate, "Leo end date is set to the given date")
        assertEquals(leoTerminationReason, terminatedLeoReservation?.terminationReason, "Leo termination reason is set")
        assertEquals(null, terminatedLeoReservation?.terminationComment, "Leo termination comment is null as it was not set")
    }

    @Test
    fun `should not be able to terminate reservation for other users as a citizen`() {
        // create the reservation for Leo
        val reservation = testUtils.createReservationInConfirmedState(CreateReservationParams(timeProvider, citizenIdLeo, 1, 1))

        // Try to terminate the reservation as Leo but with employee special method
        val exception =
            assertThrows(Unauthorized::class.java) {
                terminateService.terminateBoatSpaceReservationAsEmployee(
                    reservationId = reservation.id,
                    terminatorId = citizenIdLeo,
                    terminationReason = ReservationTerminationReason.UserRequest,
                    endDate = timeProvider.getCurrentDate(),
                    messageTitle = "",
                    messageContent = ""
                )
            }
        assertEquals("Unauthorized", exception.message, "termination throws unauthorized exception")
        val terminatedReservation = reservationService.getBoatSpaceReservation(reservation.id)
        assertEquals(ReservationStatus.Confirmed, terminatedReservation?.status, "reservation was not terminated")
    }
}
