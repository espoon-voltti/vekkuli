package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.times
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScheduledSendEmailServiceTests : IntegrationTestBase() {
    @MockBean lateinit var templateEmailService: TemplateEmailService

    @Autowired lateinit var scheduledSendEmailService: ScheduledSendEmailService

    @Autowired lateinit var reservationService: BoatReservationService

    @BeforeEach
    override fun resetDatabase() {
        deleteAllEmails(jdbi)
    }

    @Test
    fun `should send reservation expiring notification emails`() {
        val recipients =
            listOf(
                Recipient(this.citizenIdLeo, "test1@email.com"),
                Recipient(this.citizenIdOlivia, "test3@email.com"),
                Recipient(this.citizenIdMikko, "test4@email.com"),
            )
        val madeReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    1,
                    1,
                    ReservationValidity.FixedTerm
                )
            )
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdOlivia,
                2,
                3,
                ReservationValidity.FixedTerm
            )
        )

        // mock time to be 20 days before the end date
        mockTimeProvider(
            timeProvider,
            madeReservation.endDate.minusDays(20).atStartOfDay()
        )
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdMikko,
                3,
                2,
                ReservationValidity.FixedTerm
            )
        )

        scheduledSendEmailService.sendReservationExpiryReminderEmails()
        val recipientsCaptor = argumentCaptor<List<Recipient>>()

        verify(templateEmailService, times(2)).sendBatchEmail(
            any(),
            eq(null),
            any(),
            recipientsCaptor.capture(),
            eq(ReservationType.Marine),
            any(),
            eq("expiry"),
            any()
        )

        val capturedRecipients = recipientsCaptor.allValues.flatten().map { it.id }

        assertEquals(2, capturedRecipients.size)
        assertEquals(
            listOf(recipients[0].id, recipients[1].id),
            capturedRecipients,
            "Should contain all recipients but one"
        )
    }

    @Test
    fun `should send reservation renew reminder emails`() {
        val recipients =
            listOf(
                Recipient(this.citizenIdLeo, "test1@email.com"),
                Recipient(this.citizenIdOlivia, "test3@email.com"),
                Recipient(this.citizenIdMikko, "test4@email.com"),
            )
        val madeReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    1,
                    1,
                    ReservationValidity.Indefinite
                )
            )
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdOlivia,
                2,
                3,
                ReservationValidity.Indefinite
            )
        )

        // mock time to be 20 days before the end date
        mockTimeProvider(
            timeProvider,
            madeReservation.endDate.minusDays(20).atStartOfDay()
        )
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdMikko,
                3,
                2,
                ReservationValidity.Indefinite
            )
        )

        scheduledSendEmailService.sendReservationRenewReminderEmails()
        val recipientsCaptor = argumentCaptor<List<Recipient>>()

        verify(templateEmailService, times(2)).sendBatchEmail(
            any(),
            eq(null),
            any(),
            recipientsCaptor.capture(),
            eq(ReservationType.Marine),
            any(),
            eq("renew"),
            any()
        )

        val capturedRecipients = recipientsCaptor.allValues.flatten().map { it.id }

        assertEquals(2, capturedRecipients.size)
        assertEquals(
            setOf(recipients[0].id, recipients[1].id),
            capturedRecipients.toSet(),
            "Should contain all recipients but one"
        )
    }
}
