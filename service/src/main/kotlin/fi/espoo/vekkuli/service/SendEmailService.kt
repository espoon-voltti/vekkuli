package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentData
import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentService
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.domain.Attachment
import jakarta.activation.DataHandler
import jakarta.mail.Part
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import jakarta.mail.util.ByteArrayDataSource
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.*
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.*
import jakarta.mail.Message as JakartaMessage

interface SendEmailInterface {
    fun sendEmail(
        senderAddress: String?,
        emailAddress: String,
        subject: String,
        body: String,
        attachments: List<Attachment>
    ): String?
}

@Service
@Profile("test")
class SendEmailServiceMock(
    private val attachmentService: AttachmentService
) : SendEmailInterface {
    data class SentEmail(
        val senderAddress: String,
        val recipientAddress: String,
        val subject: String,
        val body: String
    )

    companion object {
        val emails = mutableListOf<SentEmail>()

        fun resetEmails() {
            emails.clear()
        }
    }

    override fun sendEmail(
        senderAddress: String?,
        emailAddress: String,
        subject: String,
        body: String,
        attachments: List<Attachment>,
    ): String? {
        val email = "Email from $senderAddress to $emailAddress with subject $subject and content $body"
        println(email)
        emails.add(SentEmail(senderAddress ?: "", emailAddress, subject, body))
        return email
    }
}

@Service
@Profile("!test")
class SendEmailService(
    private val sesClient: SesClient,
    private val emailEnv: EmailEnv,
    private val attachmentService: AttachmentService,
) : SendEmailInterface {
    private val logger = LoggerFactory.getLogger(CitizenUserController::class.java)

    override fun sendEmail(
        senderAddress: String?,
        emailAddress: String,
        subject: String,
        body: String,
        attachments: List<Attachment>,
    ): String? {
        if (!emailEnv.enabled) {
            println(
                "Email from $senderAddress (arn ${emailEnv.senderArn}, " +
                    "region ${emailEnv.region}) to $emailAddress with subject $subject and content $body"
            )
            return "Test-${UUID.randomUUID()}"
        }
        val attachmentsContent = attachments.map { it -> attachmentService.getAttachment(it.key) }
        if (attachmentsContent.any { it == null }) {
            logger.error("Failed to send email: one or more attachments could not be found")
            throw IllegalStateException(
                "Failed to send email: one or more attachments could not be found"
            )
        }

        val mimeMessage = createMimeMessageRaw(senderAddress, emailAddress, subject, body, attachmentsContent.filterNotNull())

        val emailRequest =
            SendRawEmailRequest
                .builder()
                .rawMessage {
                    it.data(mimeMessage)
                }.build()

        try {
            val response = sesClient.sendRawEmail(emailRequest)
            return response.messageId()
        } catch (ex: SesException) {
            logger.error("Failed to send email to $emailAddress: ${ex.awsErrorDetails().errorMessage()}")
            return null
        }
    }

    fun createMimeMessageRaw(
        senderAddress: String?,
        emailAddress: String,
        subject: String,
        body: String,
        attachments: List<AttachmentData>
    ): SdkBytes? {
        val session = Session.getDefaultInstance(Properties())
        val message = MimeMessage(session)

        message.setFrom(InternetAddress(senderAddress))
        message.setRecipients(JakartaMessage.RecipientType.TO, InternetAddress.parse(emailAddress))
        message.subject = subject

        val textPart =
            MimeBodyPart().apply {
                setText(body, "UTF-8")
            }

        val multipart =
            MimeMultipart("mixed").apply {
                addBodyPart(textPart)
                attachments.forEach { att ->
                    addBodyPart(
                        MimeBodyPart().apply {
                            dataHandler = DataHandler(ByteArrayDataSource(att.data, att.contentType))
                            fileName = att.name
                            disposition = Part.ATTACHMENT
                        }
                    )
                }
            }

        message.setContent(multipart)

        val outputStream = ByteArrayOutputStream()
        message.writeTo(outputStream)
        return SdkBytes.fromByteBuffer(ByteBuffer.wrap(outputStream.toByteArray()))
    }
}
