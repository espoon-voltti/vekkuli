package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.QueuedMessage
import fi.espoo.vekkuli.domain.Recipient
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JdbiSentMessageRepository(
    private val jdbi: Jdbi
) : SentMessageRepository {
    override fun addSentEmails(
        senderId: UUID?,
        senderAddress: String,
        recipients: List<Recipient>,
        subject: String,
        body: String,
    ): List<QueuedMessage> =
        jdbi.withHandleUnchecked { handle ->
            val batch =
                handle.prepareBatch(
                    """
            INSERT INTO sent_message (status, sender_id, sender_address, recipient_id, recipient_address, type, subject, body)
            VALUES ('Queued', :senderId, :senderAddress, :recipientId, :recipientEmail, 'Email', :subject, :body)
            """
                )

            recipients.forEach { recipient ->
                batch
                    .bind("senderId", senderId)
                    .bind("senderAddress", senderAddress)
                    .bind("recipientId", recipient.id)
                    .bind("recipientEmail", recipient.email)
                    .bind("subject", subject)
                    .bind("body", body)
                    .add()
            }

            batch
                .executePreparedBatch()
                .mapTo<QueuedMessage>()
                .list()
        }

    override fun setMessagesSent(messageIds: List<Pair<UUID, String>>): List<QueuedMessage> =
        jdbi.withHandleUnchecked { handle ->
            val batch =
                handle.prepareBatch(
                    """
            UPDATE sent_message
            SET status = 'Sent', sent_at = now(), provider_id = :providerId
            WHERE id = :messageId
            RETURNING *
            """
                )

            messageIds.forEach { messageId ->
                batch
                    .bind("messageId", messageId.first)
                    .bind("providerId", messageId.second)
                    .add()
            }

            batch
                .executePreparedBatch()
                .mapTo<QueuedMessage>()
                .list()
        }

    override fun setMessagesFailed(messageIds: List<UUID>): List<QueuedMessage> =
        jdbi.withHandleUnchecked { handle ->
            val batch =
                handle.prepareBatch(
                    """
            UPDATE sent_message
            SET status = 'Failed', sent_at = now()
            WHERE id = :messageId
            RETURNING *
            """
                )

            messageIds.forEach { messageId ->
                batch
                    .bind("messageId", messageId)
                    .add()
            }

            batch
                .executePreparedBatch()
                .mapTo<QueuedMessage>()
                .list()
        }

    override fun getMessagesSentToUser(citizenId: UUID): List<QueuedMessage> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT *
                    FROM sent_message
                    WHERE recipient_id = :citizenId
                    ORDER BY created DESC
                    """
                ).bind("citizenId", citizenId)
                .mapTo<QueuedMessage>()
                .list()
        }

    override fun getUnsentEmails(batchSize: Int): List<QueuedMessage> {
        // set status to processing
        // get batch size
        TODO("Not yet implemented")
    }
}
