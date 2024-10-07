package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.domain.SentMessage
import fi.espoo.vekkuli.repository.SentMessageRepository
import org.springframework.stereotype.Service
import java.util.UUID

interface MessageServiceInterface {
    fun sendEmails(
        userId: UUID?,
        senderAddress: String,
        recipients: List<Recipient>,
        subject: String,
        body: String,
    ): List<SentMessage>
}

@Service
class MessageService(
    private val messageRepository: SentMessageRepository,
    private val sendEmailService: SendEmailInterface,
) : MessageServiceInterface {
    override fun sendEmails(
        // Who initiated the sending of the email (null if automated)
        userId: UUID?,
        // From which email address was message sent
        senderAddress: String,
        // Recipients' id and email address
        recipients: List<Recipient>,
        // Email subject
        subject: String,
        // Email message body
        body: String,
    ): List<SentMessage> {
        val msg = messageRepository.addSentEmails(userId, senderAddress, recipients, subject, body)
        val messageId = sendEmailService.sendMultipleEmails(senderAddress, recipients.map { it.email }, subject, body)
        if (messageId == null) {
            return messageRepository.setMessagesFailed(msg.map { it.id }, "Failed to send email")
        }
        return messageRepository.setMessagesSent(msg.map { it.id }, messageId)
    }
}
