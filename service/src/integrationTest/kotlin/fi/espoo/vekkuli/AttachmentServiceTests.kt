package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentService
import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.service.MessageService
import fi.espoo.vekkuli.service.MessageServiceInterface
import fi.espoo.vekkuli.service.ReserverService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AttachmentServiceTests : IntegrationTestBase() {
    @Autowired private lateinit var reserverService: ReserverService

    @Autowired
    private lateinit var attachmentService: AttachmentService

    @Autowired
    private lateinit var messageService: MessageServiceInterface

    @Test
    fun `should add attachment`() {
        val bytes = "test-input".toByteArray()
        val size = bytes.size.toLong()
        val name = "test-name"
        val id =
            attachmentService.uploadAttachment(
                contentType = "image/png",
                input = bytes.inputStream(), // fresh stream
                size = size,
                name = name
            )
        val id2 =
            attachmentService.uploadAttachment(
                contentType = "image/png",
                input = bytes.inputStream(), // fresh stream
                size = size,
                name = "test-name2"
            )
        val recipients =
            listOf(
                Recipient(this.citizenIdLeo, "test@gmail.com"),
                Recipient(this.citizenIdOlivia, "olivia@gmail.com")
            )
        val messages =
            messageService.sendEmails(
                null,
                "sender@gmail.com",
                recipients,
                "Subject",
                "Email body",
                listOf(id, id2)
            )

        val message = reserverService.getMessage(messages.first().id)

        assertEquals(message.attachments.size, 2)
        assertEquals(name, message.attachments.first().name)

        val userMessages = reserverService.getMessages(citizenIdOlivia)
        assertEquals(userMessages.size, 1, "There should be one message")
        val userMessage = userMessages.first()
        assertEquals(userMessage.attachments.size, 2, "There should be two attachments")
        assertEquals(
            userMessage.attachments.first().name,
            message.attachments.first().name
        )
    }
}
