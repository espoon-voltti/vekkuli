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

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SendEmailInterfaceIntegrationTests : IntegrationTestBase() {
    @Autowired lateinit var messageService: MessageService

    @Autowired lateinit var messageRepository: SentMessageRepository

    @Autowired lateinit var templateEmailService: TemplateEmailService

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

    @Test
    fun `should get emails that have not been sent`() {
        val recipients =
            listOf(
                Recipient(this.citizenIdLeo, "test@email.com"),
                Recipient(this.organizationId, "test1email.com"),
            )
        val reservationType = ReservationType.Marine
        val reservationId = 1
        val source = "test"
        var emails =
            templateEmailService.sendBatchEmail(
                "reservation_organization_confirmation",
                null,
                "sender@email.com",
                listOf(recipients[0]),
                reservationType,
                reservationId,
                source,
                mapOf()
            )
        assertEquals(1, emails.size, "One email to be sent")
        emails =
            templateEmailService.sendBatchEmail(
                "reservation_organization_confirmation",
                null,
                "sender@email.com",
                recipients,
                reservationType,
                reservationId,
                source,
                mapOf()
            )
        assertEquals(1, emails.size, "Only one email was not already sent")
        assertEquals(2, messageRepository.getUnsentEmails().size, "Two emails to be sent")
    }
}
