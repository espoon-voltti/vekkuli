package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentServiceInterface
import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.service.MessageService
import org.junit.jupiter.api.Assertions.assertEquals
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
    @Autowired
    private lateinit var attachmentService: AttachmentServiceInterface

    @Autowired
    private lateinit var messageService: MessageService

    @Test
    fun `should add attachment`() {
        val recipients =
            listOf(Recipient(this.citizenIdLeo, "test@gmail.com"))
        val messages = messageService.sendEmails(null, "sender@gmail.com", recipients, "Subject", "Email body")

        val key =
            attachmentService.addAttachmentToMessage(
                messageId = messages.first().id,
                contentType = "image/png",
                input = "test-input".byteInputStream(),
                size = 1234L
            )

        val attachments = attachmentService.getAttachmentsForMessage(messages.first().id)

        assertEquals(attachments?.size, 1)
        assertEquals(attachments?.first()?.key, key, "Attachment key is correct")
    }
}
