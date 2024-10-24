package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.QueuedMessage
import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.repository.SentMessageRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.UUID

interface MessageServiceInterface {
    fun sendEmails(
        userId: UUID?,
        senderAddress: String,
        recipients: List<Recipient>,
        subject: String,
        body: String,
    ): List<QueuedMessage>
}

@Service
class MessageService(
    private val messageRepository: SentMessageRepository,
    private val sendEmailService: SendEmailInterface
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
    ): List<QueuedMessage> {
        val msg = messageRepository.addSentEmails(userId, senderAddress, recipients, subject, body)
        return msg
    }

    @Scheduled(fixedRate = 60000)
    fun sendScheduledEmails() {
        val unsentEmails = messageRepository.getUnsentEmails()

        val failedMessageIds = mutableListOf<UUID>()
        val sentMessageIds = mutableListOf<Pair<UUID, String>>()
        unsentEmails.forEach {
            // Send email
            var providerId =
                sendEmailService.sendEmail(
                    it.senderAddress,
                    it.recipientAddress,
                    it.subject,
                    it.body
                )
            if (providerId == null) {
                failedMessageIds.add(it.id)
            } else {
                sentMessageIds.add(Pair(it.id, providerId))
            }

            messageRepository.setMessagesFailed(failedMessageIds)

            messageRepository.setMessagesSent(sentMessageIds)
        }
    }
}
