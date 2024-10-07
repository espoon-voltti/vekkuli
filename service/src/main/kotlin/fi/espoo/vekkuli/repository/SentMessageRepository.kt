package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.domain.SentMessage
import java.util.*

interface SentMessageRepository {
    fun addSentEmails(
        senderId: UUID?,
        senderAddress: String,
        recipients: List<Recipient>,
        subject: String,
        body: String,
    ): List<SentMessage>

    fun setMessagesSent(
        messageId: List<UUID>,
        providerId: String
    ): List<SentMessage>

    fun setMessagesFailed(
        messageId: List<UUID>,
        providerId: String
    ): List<SentMessage>

    fun getMessagesSentToUser(citizenId: UUID): List<SentMessage>
}
