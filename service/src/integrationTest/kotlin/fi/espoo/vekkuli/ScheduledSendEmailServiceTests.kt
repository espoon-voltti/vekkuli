package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScheduledSendEmailServiceTests : IntegrationTestBase() {
    @Autowired
    lateinit var messageService: MessageService

    @Autowired
    lateinit var scheduledSendEmailService: ScheduledSendEmailService

    @Autowired
    lateinit var reservationService: BoatReservationService

    @BeforeEach
    override fun resetDatabase() {
        deleteAllEmails(jdbi)
        jdbi.withHandleUnchecked { handle ->
            // seed insert some reservations which mess the tests
            handle.execute("DELETE FROM payment")
            handle.execute("DELETE FROM boat_space_reservation")
        }

        SendEmailServiceMock.resetEmails()
    }

    data class TestUser(
        val id: UUID,
        val name: String,
        val email: String,
    )

    val expectedEmailRecipients =
        listOf(
            TestUser(this.citizenIdLeo, "Korhonen Leo", "leo@noreplytest.fi"),
            TestUser(this.citizenIdJorma, "Pulkkinen Jorma", "jorma@noreplytest.fi"),
            TestUser(this.citizenIdOlivia, "Espoon Pursiseura", "olivia@noreplytest.fi"),
            TestUser(this.organizationId, "Espoon Pursiseura", "eps@noreplytest.fi")
        ).sortedBy { e -> e.email }

    private fun setupTest(
        validity: ReservationValidity,
        functionToTest: () -> Unit
    ): List<SendEmailServiceMock.SentEmail> {
        val now = LocalDateTime.of(2024, 12, 10, 12, 12, 12)
        val endOfYear = LocalDate.of(2024, 12, 31)
        mockTimeProvider(timeProvider, now)

        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                1,
                1,
                validity,
                endDate = endOfYear
            )
        )
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdJorma,
                2,
                3,
                validity,
                endDate = endOfYear
            )
        )
        // organization reservation, mail should be sent to it and its members
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                citizenIdOlivia,
                3,
                6,
                validity,
                reserverId = this.organizationId,
                endDate = endOfYear
            )
        )

        // this reservation should not get the email
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdMikko,
                6,
                2,
                validity,
                endDate = LocalDate.of(2025, 1, 31),
            )
        )

        mockTimeProvider(
            timeProvider,
            endOfYear.minusDays(20).atStartOfDay()
        )

        functionToTest()
        messageService.sendScheduledEmails()

        // sort by email address so it matches usersWithExpiringFixedTermReservation
        val sentEmails = SendEmailServiceMock.emails.sortedBy { e -> e.recipientAddress }

        assertEquals(4, sentEmails.size)
        return sentEmails
    }

    @Test
    fun `should send reservation expiring notification emails`() {
        val sentEmails =
            setupTest(ReservationValidity.FixedTerm) {
                scheduledSendEmailService.sendReservationExpiryReminderEmails()
            }

        expectedEmailRecipients.forEachIndexed { i, reserver ->
            val sentEmail = sentEmails[i]
            assertEquals(reserver.email, sentEmail.recipientAddress)
            assertEquals("Espoon kaupungin laituripaikkavarauksesi on päättymässä", sentEmail.subject)
            assertTrue(sentEmail.body.contains("varausaika on päättymässä 31.12.2024"))
            assertTrue(sentEmail.body.contains("Paikan vuokraaja: ${reserver.name}"))
        }
    }

    @Test
    fun `should send reservation renew reminder emails`() {
        val sentEmails =
            setupTest(ReservationValidity.Indefinite) {
                scheduledSendEmailService.sendReservationRenewReminderEmails()
            }
        expectedEmailRecipients.forEachIndexed { i, reserver ->
            val sentEmail = sentEmails[i]
            assertEquals(reserver.email, sentEmail.recipientAddress)
            assertEquals("Varmista Espoon kaupungin laituripaikkasi jatko ensi kaudelle nyt", sentEmail.subject)
            assertTrue(sentEmail.body.contains("On aika jatkaa laituripaikkasi varausta ensi kaudelle."))
            assertTrue(sentEmail.body.contains("Paikan vuokraaja: ${reserver.name}"))
        }
    }
}
