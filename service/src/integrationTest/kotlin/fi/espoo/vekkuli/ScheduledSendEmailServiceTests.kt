package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.renewal.RenewalPolicyService
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipReservationPeriod
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
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
    lateinit var sendMassEmailService: SendMassEmailService

    @Autowired
    lateinit var reservationService: BoatReservationService

    @MockitoBean
    lateinit var renewalPolicyService: RenewalPolicyService

    @BeforeEach
    override fun resetDatabase() {
        deleteAllEmails(jdbi)
        deleteAllReservations(jdbi)
        SendEmailServiceMock.resetEmails()
        mockCanRenew()
    }

    private fun mockCanRenew() {
        Mockito
            .`when`(
                renewalPolicyService.citizenCanRenewReservation(
                    any(),
                    any()
                )
            ).thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        startOfSlipReservationPeriod.toLocalDate(),
                        startOfSlipReservationPeriod.toLocalDate(),
                        ReservationValidity.Indefinite
                    )
                )
            )
    }

    data class TestUser(
        val id: UUID,
        val name: String,
        val email: String,
        val boatSpaceType: BoatSpaceType
    )

    private final val storagePlaceUser =
        TestUser(this.citizenIdJorma, "Pulkkinen Jorma", "jorma@noreplytest.fi", BoatSpaceType.Storage)
    private val expectedEmailRecipients =
        listOf(
            TestUser(this.citizenIdLeo, "Korhonen Leo", "leo@noreplytest.fi", BoatSpaceType.Slip),
            storagePlaceUser,
            TestUser(this.citizenIdOlivia, "Espoon Pursiseura", "olivia@noreplytest.fi", BoatSpaceType.Slip),
            TestUser(this.organizationId, "Espoon Pursiseura", "eps@noreplytest.fi", BoatSpaceType.Slip),
            TestUser(this.citizenIdMarko, "Kuusinen Marko", "marko@noreplytest.fi", BoatSpaceType.Slip)
        ).sortedBy { e -> e.email }

    private val now = LocalDateTime.of(2024, 12, 10, 12, 12, 12)
    private val endDate = LocalDate.of(2024, 12, 31)

    private fun setupTest(
        validity: ReservationValidity,
        expectedSentEmailCount: Int = 4,
        functionToTest: () -> Unit
    ): List<SendEmailServiceMock.SentEmail> {
        mockTimeProvider(timeProvider, now)
        Mockito
            .`when`(
                renewalPolicyService.citizenCanRenewReservation(
                    any(),
                    eq(nonEspooCitizenWithoutReservationsId)
                )
            ).thenReturn(
                ReservationResult.Failure(
                    errorCode = ReservationResultErrorCode.NotEspooCitizen
                )
            )
        // slip space reservation
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                citizenIdLeo,
                1,
                1,
                validity,
                endDate = endDate
            )
        )
        // storage space reservation
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                citizenIdJorma,
                346,
                3,
                validity,
                endDate = endDate
            )
        )
        // organization reservation, mail should be sent to it and its members
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                citizenIdOlivia,
                2,
                6,
                validity,
                reserverId = this.organizationId,
                endDate = endDate
            )
        )
        // this reservation should not get the email as it not expiring soon enough
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                citizenIdMikko,
                30,
                2,
                validity,
                endDate = endDate.plusDays(BoatSpaceConfig.DAYS_BEFORE_RESERVATION_EXPIRY_NOTICE.toLong() + 1)
            )
        )
        // this reservation should not get the email as it is too old
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                citizenIdMikko,
                31,
                3,
                validity,
                endDate = LocalDate.of(2024, 12, 1),
            )
        )
        // this reservation should not get the email as the citizen is not from Espoo
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                nonEspooCitizenWithoutReservationsId,
                3,
                4,
                validity,
                endDate = endDate
            )
        )

        mockTimeProvider(
            timeProvider,
            endDate.minusDays(20).atStartOfDay()
        )

        // First send the reservation confirmation emails
        messageService.sendScheduledEmails()
        SendEmailServiceMock.resetEmails()

        functionToTest()
        messageService.sendScheduledEmails()

        // sort by email address so it matches usersWithExpiringFixedTermReservation
        val sentEmails = SendEmailServiceMock.emails.sortedBy { e -> e.recipientAddress }

        assertEquals(expectedSentEmailCount, sentEmails.size)
        return sentEmails
    }

    @Test
    fun `should send reservation expiring notification emails`() {
        val sentEmails =
            setupTest(ReservationValidity.FixedTerm, 5) {
                sendMassEmailService.sendReservationExpiryReminderEmails()
            }

        sentEmails.forEach { sentEmail ->
            val reserver =
                expectedEmailRecipients.find { e -> e.email == sentEmail.recipientAddress }
                    ?: throw IllegalStateException("No reserver for email ${sentEmail.recipientAddress}")
            assertEquals(reserver.email, sentEmail.recipientAddress)
            val spacePhrase =
                when (reserver.boatSpaceType) {
                    BoatSpaceType.Slip -> "laituripaikkavarauksesi"
                    else -> "säilytyspaikkavarauksesi"
                }
            assertEquals("Espoon kaupungin $spacePhrase on päättymässä", sentEmail.subject)
            assertTrue(sentEmail.body.contains("varausaika on päättymässä 31.12.2024"))
            assertTrue(sentEmail.body.contains("Paikan vuokraaja: ${reserver.name}"))
        }
    }

    @Test
    fun `should send reservation renew reminder emails`() {
        val sentEmails =
            setupTest(ReservationValidity.Indefinite, 4) {
                sendMassEmailService.sendReservationRenewReminderEmails()
            }

        sentEmails.forEach { sentEmail ->
            val reserver =
                expectedEmailRecipients.find { e -> e.email == sentEmail.recipientAddress }
                    ?: throw IllegalStateException("No reserver for email ${sentEmail.recipientAddress}")
            val spacePhrase =
                when (reserver.boatSpaceType) {
                    BoatSpaceType.Slip -> "laituripaikkasi"
                    else -> "säilytyspaikkasi"
                }
            assertEquals(reserver.email, sentEmail.recipientAddress)
            assertEquals("Varmista Espoon kaupungin $spacePhrase jatko ensi kaudelle nyt", sentEmail.subject)
            assertTrue(sentEmail.body.contains("On aika jatkaa $spacePhrase varausta ensi kaudelle."))
            assertTrue(sentEmail.body.contains("Paikan vuokraaja: ${reserver.name}"))
        }
    }

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun `should send email when a reservation ends to reservers and do not send it again, for storage space, send email to employee also`() {
        val sentEmails =
            setupTest(ReservationValidity.FixedTerm, 6) {
                mockTimeProvider(
                    timeProvider,
                    endDate.plusDays(1).atStartOfDay()
                )
                sendMassEmailService.sendReservationExpiredEmails()
            }
        expectedEmailRecipients.forEachIndexed { i, reserver ->
            val sentEmail = sentEmails[i]
            assertEquals(reserver.email, sentEmail.recipientAddress)
            assertEquals("Espoon kaupungin venepaikan vuokrasopimus on päättynyt", sentEmail.subject)
            assertTrue(sentEmail.body.contains("Paikan vuokraaja: ${reserver.name}"))
        }

        val emailToEmployee = sentEmails.last()
        val employeeEmailSubject =
            "Säilytyspaikan Ämmäsmäki C 030 vuokrasopimus on päättynyt, asiakas: ${storagePlaceUser.name}"

        assertEquals("venepaikat@espoo.fi", emailToEmployee.recipientAddress)
        assertEquals(employeeEmailSubject, emailToEmployee.subject)
        assertTrue(emailToEmployee.body.contains("Asiakas:\n${storagePlaceUser.name}"))

        SendEmailServiceMock.resetEmails()

        // Verify that expiration emails are not sent again.
        sendMassEmailService.sendReservationExpiredEmails()
        messageService.sendScheduledEmails()

        assertEquals(0, SendEmailServiceMock.emails.size)
    }
}
