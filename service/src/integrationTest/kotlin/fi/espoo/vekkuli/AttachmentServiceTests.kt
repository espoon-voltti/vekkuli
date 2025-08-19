package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentService
import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.service.MessageService
import fi.espoo.vekkuli.service.MessageServiceInterface
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
    @Autowired
    private lateinit var attachmentService: AttachmentService

    @Autowired
    private lateinit var messageService: MessageServiceInterface

    @Test
    fun `should add attachment`() {
        val bytes = "test-input".toByteArray()
        val size = bytes.size.toLong()

        val id =
            attachmentService.uploadAttachment(
                contentType = "image/png",
                input = bytes.inputStream(), // fresh stream
                size = size,
                name = "test-name"
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

        val attachments = attachmentService.getAttachmentsForMessage(messages.first().id)

        assertEquals(attachments?.size, 2)
        assertArrayEquals(bytes, attachments!!.first().data, "Attachment data should be the same")
    }
}
