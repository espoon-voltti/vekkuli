package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.SentMessageRepository
import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
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
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmailTemplateServiceIntegrationTests : IntegrationTestBase() {
    @Autowired lateinit var reservationService: BoatReservationService

    @MockBean lateinit var emailServiceMock: TemplateEmailService

    @Test
    fun `send single email on confirmation`() {
        val madeReservation =
            createReservationInPaymentState(
                timeProvider,
                reservationService,
                this.citizenIdLeo
            )

        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = this.citizenIdLeo,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1",
                )
            )

        reservationService.handlePaymentResult(mapOf("checkout-stamp" to payment.id.toString()), true)
        verify(emailServiceMock).sendEmail(
            eq("varausvahvistus"),
            eq(null),
            any(),
            eq(
                Recipient(
                    this.citizenIdLeo,
                    "leo@noreplytest.fi"
                )
            ),
            any()
        )
    }

    @Test
    fun `send organization email on confirmation`() {
        val madeReservation =
            createReservationInPaymentState(
                timeProvider,
                reservationService,
                organizationId,
                this.citizenIdLeo
            )

        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = this.citizenIdLeo,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1",
                )
            )

        reservationService.handlePaymentResult(mapOf("checkout-stamp" to payment.id.toString()), true)
        verify(emailServiceMock).sendBatchEmail(
            eq("reservation_organization_confirmation"),
            eq(null),
            any(),
            eq(
                listOf(
                    Recipient(organizationId, "eps@noreplytest.fi"),
                    Recipient(
                        UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49"),
                        "olivia@noreplytest.fi"
                    )
                )
            ),
            any()
        )
    }

    @Test
    fun `should send correct template email on invoice`() {
        val madeReservation =
            createReservationInInfoState(
                timeProvider,
                reservationService,
                this.citizenIdLeo
            )
        reservationService.reserveBoatSpace(
            this.citizenIdLeo,
            ReserveBoatSpaceInput(
                reservationId = madeReservation.id,
                boatId = 1,
                boatType = BoatType.OutboardMotor,
                width = 1.0,
                length = 1.0,
                depth = 1.0,
                weight = 1,
                boatRegistrationNumber = "OYK342",
                boatName = "Boat",
                otherIdentification = "Other identification",
                extraInformation = "Extra information",
                ownerShip = OwnershipStatus.Owner,
                email = "leo@noreplytest.fi",
                phone = "123456789"
            ),
            ReservationStatus.Invoiced,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDate(),
            timeProvider.getCurrentDate()
        )
        verify(emailServiceMock).sendEmail(
            eq("reservation_confirmation_invoice"),
            eq(null),
            any(),
            eq(
                Recipient(
                    this.citizenIdLeo,
                    "leo@noreplytest.fi"
                )
            ),
            any()
        )
    }
}

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmailServiceIntegrationTests : IntegrationTestBase() {
    @Autowired lateinit var reservationService: BoatReservationService

    @MockBean lateinit var messageServiceMock: MessageService

    @Test
    fun `should send single email on confirmation`() {
        val madeReservation =
            createReservationInPaymentState(
                timeProvider,
                reservationService,
                this.citizenIdLeo,
                this.citizenIdLeo
            )
        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = this.citizenIdLeo,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1",
                )
            )

        reservationService.handlePaymentResult(mapOf("checkout-stamp" to payment.id.toString()), true)
        verify(messageServiceMock).sendEmails(
            eq(null),
            any(),
            eq(listOf(Recipient(this.citizenIdLeo, "leo@noreplytest.fi"))),
            any(),
            any()
        )
    }

    @Test
    fun `should send multiple emails on confirmation`() {
        val madeReservation =
            createReservationInPaymentState(
                timeProvider,
                reservationService,
                organizationId,
                this.citizenIdLeo
            )
        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = this.citizenIdLeo,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1",
                )
            )

        reservationService.handlePaymentResult(mapOf("checkout-stamp" to payment.id.toString()), true)
        verify(messageServiceMock).sendEmails(
            eq(null),
            any(),
            eq(listOf(Recipient(organizationId, "eps@noreplytest.fi"), Recipient(citizenIdOlivia, "olivia@noreplytest.fi"))),
            any(),
            any(),
        )
    }

    @Test
    fun `should send email on invoice`() {
        val madeReservation =
            createReservationInInfoState(
                timeProvider,
                reservationService,
                this.citizenIdLeo
            )
        reservationService.reserveBoatSpace(
            this.citizenIdLeo,
            ReserveBoatSpaceInput(
                reservationId = madeReservation.id,
                boatId = 1,
                boatType = BoatType.OutboardMotor,
                width = 1.0,
                length = 1.0,
                depth = 1.0,
                weight = 1,
                boatRegistrationNumber = "1",
                boatName = "1",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.Owner,
                email = "leo@noreplytest.fi",
                phone = "123456789"
            ),
            ReservationStatus.Invoiced,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDate(),
            timeProvider.getCurrentDate()
        )
        verify(messageServiceMock).sendEmails(
            eq(null),
            any(),
            eq(listOf(Recipient(this.citizenIdLeo, "leo@noreplytest.fi"))),
            any(),
            any()
        )
    }
}

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SendEmailInterfaceIntegrationTests : IntegrationTestBase() {
    @Autowired lateinit var messageService: MessageService

    @Autowired lateinit var messageRepository: SentMessageRepository

    @MockBean lateinit var sendEmailServiceMock: SendEmailInterface

    @BeforeEach
    override fun resetDatabase() {
        deleteAllEmails(jdbi)
        Mockito.`when`(sendEmailServiceMock.sendEmail(any(), any(), any(), any())).thenReturn("providerId")
    }

    @Test
    fun `should set emails to be sent in batches`() {
        val recipients =
            listOf(
                Recipient(this.citizenIdLeo, "test@email.com"),
                Recipient(this.organizationId, "test@email.com"),
                Recipient(this.citizenIdOlivia, "test@email.com"),
                Recipient(this.citizenIdMikko, "test@email.com"),
            )
        // Send 4 emails
        messageService.sendEmails(null, "sender@gmail.com", recipients, "Subject", "Email body")
        val batchSize = 4
        val emails = messageRepository.getUnsentEmails(batchSize)
        assertEquals(batchSize, emails.size, "Fetched emails should match batch size")
        assertEquals(batchSize, emails.filter { it.status == MessageStatus.Processing }.size, "All emails are processing")

        val emails2 = messageRepository.getUnsentEmails()
        assertEquals(0, emails2.size, "No emails left to send")
    }

    @Test
    fun `should send emails from the database`() {
        val recipients =
            listOf(
                Recipient(this.citizenIdLeo, "test1@email.com"),
                Recipient(this.organizationId, "test2@email.com"),
            )
        Mockito.`when`(sendEmailServiceMock.sendEmail(any(), eq(recipients[0].email), any(), any())).thenReturn("providerId")
        Mockito.`when`(sendEmailServiceMock.sendEmail(any(), eq(recipients[1].email), any(), any())).thenReturn(null)

        messageService.sendEmails(null, "sender@gmail.com", recipients, "Subject", "Email body")
        messageService.sendScheduledEmails()
        val recipientCaptor = argumentCaptor<String>()

        verify(sendEmailServiceMock, times(2)).sendEmail(any(), recipientCaptor.capture(), any(), any())
        val capturedRecipients = recipientCaptor.allValues

        assertEquals(2, capturedRecipients.size)
        assertTrue(capturedRecipients.containsAll(listOf(recipients[0].email, recipients[1].email)))

        val sentEmail = messageRepository.getMessagesSentToUser(this.citizenIdLeo)[0]
        assertEquals(MessageStatus.Sent, sentEmail.status, "One email successfully sent to user")

        val emails = messageRepository.getUnsentEmails()
        assertEquals(1, emails.size, "One email left to send")
    }
}
