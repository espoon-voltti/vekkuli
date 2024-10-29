package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.config.DomainConstants
import fi.espoo.vekkuli.domain.QueuedMessage
import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.domain.ReservationType
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JdbiSentMessageRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
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

    override fun getUnsentEmailsAndSetToProcessing(batchSize: Int): List<QueuedMessage> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle
                    .createQuery(
                        """
                        WITH first_ten AS (
                            SELECT *
                            FROM sent_message
                            WHERE status = 'Queued' OR (status = 'Failed' AND retry_count < :failedMessageRetryLimit)
                                OR (status = 'Processing' AND created < :currentDateTime - interval '30 minutes')
                            ORDER BY created ASC
                            LIMIT :batchSize
                        )
                        UPDATE sent_message
                        SET status = 'Processing', retry_count = retry_count + 1
                        WHERE id IN (SELECT id FROM first_ten)
                        RETURNING *;
                        """.trimIndent()
                    )
            query.bind("batchSize", batchSize)
            query.bind("currentDateTime", timeProvider.getCurrentDateTime())
            query.bind("failedMessageRetryLimit", DomainConstants.FAILED_MESSAGE_RETRY_LIMIT)
            query
                .mapTo<QueuedMessage>()
                .list()
        }

    override fun getAndInsertUnsentEmails(
        reservationType: ReservationType,
        reservationId: Int,
        source: String,
        recipientEmails: List<String>,
    ): List<String> {
        val query =
            """
            SELECT recipient_email FROM processed_message
            WHERE reservation_type = :reservationType
            AND reservation_id = :reservationId
            AND message_type = :source
            AND recipient_email = ANY(:recipientEmails)
            """.trimIndent()
        val alreadySentEmails =
            jdbi.withHandleUnchecked { handle ->
                handle
                    .createQuery(query)
                    .bind("reservationType", reservationType)
                    .bind("reservationId", reservationId)
                    .bind("source", source)
                    .bind("recipientEmails", recipientEmails.toTypedArray())
                    .mapTo<String>()
                    .list()
            }

        val emailsNotSent = recipientEmails.filter { !alreadySentEmails.contains(it) }
        jdbi.withHandleUnchecked { handle ->
            val batch =
                handle.prepareBatch(
                    """
                        INSERT INTO processed_message (reservation_type, reservation_id, message_type, recipient_email)
                        VALUES (:reservationType, :reservationId, :source, :recipientEmail)
                        """
                )

            emailsNotSent.forEach { email ->
                batch
                    .bind("reservationType", reservationType)
                    .bind("reservationId", reservationId)
                    .bind("source", source)
                    .bind("recipientEmail", email)
                    .add()
            }

            batch.execute()
        }
        return emailsNotSent
    }
}
