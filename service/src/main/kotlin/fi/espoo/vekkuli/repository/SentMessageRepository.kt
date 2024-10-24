package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.QueuedMessage
import fi.espoo.vekkuli.domain.Recipient
import java.util.*

interface SentMessageRepository {
    fun addSentEmails(
        senderId: UUID?,
        senderAddress: String,
        recipients: List<Recipient>,
        subject: String,
        body: String,
    ): List<QueuedMessage>

    fun setMessagesSent(messageIds: List<Pair<UUID, String>>): List<QueuedMessage>

    fun setMessagesFailed(messageIds: List<UUID>): List<QueuedMessage>

    fun getMessagesSentToUser(citizenId: UUID): List<QueuedMessage>

    /** Get all unsent messages in batches and set their status to processing **/
    fun getUnsentEmails(batchSize: Int = 10): List<QueuedMessage>
}
