package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.SentMessage
import java.util.*

interface SentMessageRepository {
    fun addSentEmail(
        senderId: UUID?,
        senderAddress: String,
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

    fun getMessagesSentToUser(citizenId: UUID): List<SentMessage>
}
