package fi.espoo.vekkuli
import fi.espoo.vekkuli.boatSpace.invoice.BoatSpaceInvoiceService
import fi.espoo.vekkuli.boatSpace.reservationStatus.ReservationStatusService
import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReason
import fi.espoo.vekkuli.boatSpace.terminateReservation.TerminateReservationService
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationStatusIntegrationTests : IntegrationTestBase() {
    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
        deleteAllEmails(jdbi)
    }

    @Autowired
    private lateinit var boatSpaceInvoiceService: BoatSpaceInvoiceService

    @Autowired
    private lateinit var reservationStatusService: ReservationStatusService

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var terminateService: TerminateReservationService

    @Test
    fun `terminated boat space should not be active`() {
        val newReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                )
            )

        val originalReservation = reservationService.getBoatSpaceReservation(newReservation.id)
        terminateService.terminateBoatSpaceReservationAsOwner(newReservation.id, citizenIdOlivia)
        val terminatedReservation = reservationService.getBoatSpaceReservation(newReservation.id)

        assertNotNull(originalReservation, "Original reservation exists")
        assertNotNull(terminatedReservation, "terminated reservation exists")
        assertEquals(originalReservation.id, terminatedReservation.id, "Original and terminated reservation have the same id")
        assertTrue(reservationStatusService.isReservationActive(originalReservation), "Original reservation is active")
        assertEquals(reservationStatusService.isReservationActive(terminatedReservation), false, "terminated reservation is not active")
    }

    @Test
    fun `terminated boat space should be active if end date has not passed`() {
        val employeeTerminatorId = userId
        val endDate = timeProvider.getCurrentDate().plusWeeks(2)
        val terminationReason = ReservationTerminationReason.RuleViolation
        val terminationComment = "Olivia's comment"
        val messageTitle = "Olivia's message title"
        val messageContent = "Olivia's message content"

        val newReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                )
            )

        terminateService.terminateBoatSpaceReservationAsEmployee(
            newReservation.id,
            employeeTerminatorId,
            terminationReason,
            endDate,
            terminationComment,
            messageTitle,
            messageContent
        )

        val terminatedReservation = reservationService.getBoatSpaceReservation(newReservation.id)

        assertNotNull(terminatedReservation, "terminated reservation exists")
        assertTrue(
            reservationStatusService.isReservationActive(terminatedReservation),
            "Terminated reservation should be active when termination date is far in the future"
        )

        // go forth the day before end date,the reservation should still be active
        mockTimeProvider(
            timeProvider,
            endDate.minusDays(1).atTime(12, 0),
        )
        assertTrue(
            reservationStatusService.isReservationActive(terminatedReservation),
            "Terminated reservation should be active when termination date is tomorrow"
        )
        // go forth the end day
        mockTimeProvider(
            timeProvider,
            endDate.atTime(0, 0, 1),
        )

        assertEquals(
            false,
            reservationStatusService.isReservationActive(terminatedReservation),
            "Terminated reservation should not be active when termination date is today"
        )
    }

    @Test
    fun `confirmed boat space should be active if end date is today or has not passed`() {
        val confirmedReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                )
            )

        assertTrue(
            reservationStatusService.isReservationActive(confirmedReservation),
            "Confirmed reservation should be active"
        )

        // go forth to the end date,the reservation should still be active
        mockTimeProvider(
            timeProvider,
            confirmedReservation.endDate.atTime(12, 0),
        )
        assertTrue(
            reservationStatusService.isReservationActive(confirmedReservation),
            "Confirmed reservation should be active when end date is today"
        )
        // go forth tomorrow
        mockTimeProvider(
            timeProvider,
            confirmedReservation.endDate.plusDays(1).atTime(0, 0, 1),
        )

        assertEquals(
            false,
            reservationStatusService.isReservationActive(confirmedReservation),
            "Confirmed reservation should not be active when termination date has passed"
        )
    }

    @Test
    fun `invoiced boat space should be active if end date is today or has not passed`() {
        val invoicedReservation =
            testUtils.createReservationInInvoiceState(
                timeProvider,
                reservationService,
                boatSpaceInvoiceService,
                citizenIdOlivia,
            )

        assertTrue(
            reservationStatusService.isReservationActive(invoicedReservation),
            "Invoiced reservation should be active"
        )

        // go forth to the end date,the reservation should still be active
        mockTimeProvider(
            timeProvider,
            invoicedReservation.endDate.atTime(12, 0),
        )
        assertTrue(
            reservationStatusService.isReservationActive(invoicedReservation),
            "Confirmed reservation should be active when end date is today"
        )
        // go forth tomorrow
        mockTimeProvider(
            timeProvider,
            invoicedReservation.endDate.plusDays(1).atTime(0, 0, 1),
        )

        assertEquals(
            false,
            reservationStatusService.isReservationActive(invoicedReservation),
            "Confirmed reservation should not be active when termination date has passed"
        )
    }

    @Test
    fun `payment, info and renewal boat space should not be active`() {
        val infoReservation =
            testUtils.createReservationInInfoState(
                timeProvider,
                reservationService,
                citizenIdOlivia,
                1
            )

        val paymentReservation =
            testUtils.createReservationInPaymentState(
                timeProvider,
                reservationService,
                citizenIdOlivia,
                2
            )

        val renewalReservation =
            testUtils.createReservationInRenewState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                    3
                )
            )

        assertEquals(
            false,
            reservationStatusService.isReservationActive(infoReservation),
            "Info reservation should not be active"
        )
        assertEquals(
            false,
            reservationStatusService.isReservationActive(paymentReservation),
            "Payment reservation should not be active"
        )
        assertEquals(
            false,
            reservationStatusService.isReservationActive(renewalReservation),
            "Renewal reservation should not be active"
        )
    }

    @Test
    fun `reservation should be terminated only if it's terminated`() {
        val newReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                )
            )

        val originalReservation = reservationService.getBoatSpaceReservation(newReservation.id)

        assertNotNull(originalReservation, "Original reservation exists")
        assertEquals(false, reservationStatusService.isReservationTerminated(originalReservation), "Original reservation is not terminated")

        // go beyond the end of the reservation
        mockTimeProvider(
            timeProvider,
            newReservation.endDate.plusDays(1).atTime(0, 0, 1),
        )

        val expiredReservation = reservationService.getBoatSpaceReservation(newReservation.id)

        assertNotNull(expiredReservation, "Expired reservation exists")
        assertEquals(false, reservationStatusService.isReservationTerminated(expiredReservation), "Expired reservation is not terminated")

        terminateService.terminateBoatSpaceReservationAsOwner(originalReservation.id, citizenIdOlivia)
        val terminatedReservation = reservationService.getBoatSpaceReservation(originalReservation.id)

        assertNotNull(terminatedReservation, "Terminated reservation exists")
        assertTrue(
            reservationStatusService.isReservationTerminated(terminatedReservation),
            "Reservation is terminated after it's terminated"
        )
    }
}
