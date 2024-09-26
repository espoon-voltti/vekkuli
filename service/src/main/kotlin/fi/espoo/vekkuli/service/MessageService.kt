package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.SentMessage
import fi.espoo.vekkuli.repository.SentMessageRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MessageService(
    private val messageRepository: SentMessageRepository,
    private val sendEmailService: SendEmailService,
) {
    fun sendEmail(
        // Who initiated the sending of the email (null if automated)
        userId: UUID?,
        // From which email address was message sent
        senderAddress: String,
        // Citizen who receives the email
        recipientId: UUID,
        // Citizen email address
        recipientEmail: String,
        // Email subject
        subject: String,
        // Email message body
        body: String,
    ): SentMessage {
        val msg = messageRepository.addSentEmail(userId, senderAddress, recipientId, recipientEmail, subject, body)
        val messageId = sendEmailService.sendEmail(senderAddress, recipientEmail, subject, body)
        if (messageId != null) {
            return messageRepository.setMessageSent(msg.id, messageId)
        } else {
            return messageRepository.setMessageFailed(msg.id, "Failed to send email")
        }
    }
}
