package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.SentMessage
import org.springframework.stereotype.Service
import java.util.UUID

interface SentMessageRepository {
    fun addSentEmail(
        senderId: UUID?,
        recipientId: UUID,
        recipientEmail: String,
        subject: String,
        body: String,
    ): SentMessage

    fun setMessageSent(
        messageId: UUID,
        providerId: String
    ): SentMessage

    fun setMessageFailed(
        messageId: UUID,
        providerId: String
    ): SentMessage
}

@Service
class MessageService(
    private val messageRepository: SentMessageRepository,
    private val sendEmailService: SendEmailService,
) {
    fun sendEmail(
        // Who initiated the sending of the email (null if automated)
        userId: UUID?,
        // Citizen who receives the email
        recipientId: UUID,
        // Citizen email address
        recipientEmail: String,
        // Email subject
        subject: String,
        // Email message body
        body: String,
    ): SentMessage {
        val msg = messageRepository.addSentEmail(userId, recipientId, recipientEmail, subject, body)
        val messageId = sendEmailService.sendEmail(recipientEmail, subject, body)
        if (messageId != null) {
            return messageRepository.setMessageSent(msg.id, messageId)
        } else {
            return messageRepository.setMessageFailed(msg.id, "Failed to send email")
        }
    }
}
