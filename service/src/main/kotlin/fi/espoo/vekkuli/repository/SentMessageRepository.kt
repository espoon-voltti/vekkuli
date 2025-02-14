package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.config.DomainConstants
import fi.espoo.vekkuli.domain.QueuedMessage
import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.domain.ReservationType
import fi.espoo.vekkuli.service.EmailType
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

    fun getMessage(messageId: UUID): QueuedMessage

    /** Get all unsent messages in batches and set their status to processing **/
    fun getUnsentEmailsAndSetToProcessing(batchSize: Int = DomainConstants.DEFAULT_EMAIL_BATCH_SIZE): List<QueuedMessage>

    fun getAndInsertUnsentEmails(
        reservationType: ReservationType,
        reservationId: Int,
        emailType: EmailType,
        recipientEmails: List<String>
    ): List<String>
}
