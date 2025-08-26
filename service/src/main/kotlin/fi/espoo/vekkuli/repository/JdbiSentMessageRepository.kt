package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.config.DomainConstants
import fi.espoo.vekkuli.domain.Attachment
import fi.espoo.vekkuli.domain.MessageWithAttachments
import fi.espoo.vekkuli.domain.QueuedMessage
import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.domain.ReservationType
import fi.espoo.vekkuli.service.EmailType
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
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
        attachmentIds: List<UUID>
    ): List<QueuedMessage> =
        jdbi.inTransactionUnchecked { handle ->
            val messageBatch =
                handle.prepareBatch(
                    """
            INSERT INTO sent_message (status, sender_id, sender_address, recipient_id, recipient_address, type, subject, body)
            VALUES ('Queued', :senderId, :senderAddress, :recipientId, :recipientEmail, 'Email', :subject, :body)
            """
                )

            recipients.forEach { recipient ->
                messageBatch
                    .bind("senderId", senderId)
                    .bind("senderAddress", senderAddress)
                    .bind("recipientId", recipient.id)
                    .bind("recipientEmail", recipient.email)
                    .bind("subject", subject)
                    .bind("body", body)
                    .add()
            }

            val messages =
                messageBatch
                    .executePreparedBatch()
                    .mapTo<QueuedMessage>()
                    .list()

            if (attachmentIds.isNotEmpty()) {
                val attachmentBatch =
                    handle.prepareBatch(
                        """
                        INSERT INTO attachment (key, name, message_id)
                        SELECT key, name, :messageId
                        FROM attachment
                        WHERE id = :attachmentId AND message_id IS NULL
                        """.trimIndent()
                    )

                // Create attachments for each message and connect with message_id
                messages.forEach { message ->
                    attachmentIds.forEach { attachmentId ->
                        attachmentBatch
                            .bind(
                                "messageId",
                                message.id
                            ).bind("attachmentId", attachmentId)
                            .add()
                    }
                }
                attachmentBatch.execute()

                // Delete stub attachments that are not connected to any message
                handle
                    .createUpdate(
                        """
                        DELETE FROM attachment
                        WHERE id IN (<attachmentIds>) AND message_id IS NULL
                        """.trimIndent()
                    ).bindList("attachmentIds", attachmentIds)
                    .execute()
            }
            messages
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

    override fun getMessagesSentToUser(citizenId: UUID): List<MessageWithAttachments> =
        jdbi.withHandleUnchecked { handle ->
            val messages =
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

            if (messages.isNotEmpty()) {
                // Fetch attachments for each message
                val attachmentQuery =
                    """
                    SELECT key, name, id
                    FROM attachment
                    WHERE message_id = :id
                    ORDER BY created
                    """.trimIndent()

                // Return messages with attachments
                messages.map { m ->
                    val attachments =
                        handle
                            .createQuery(attachmentQuery)
                            .bind("id", m.id)
                            .mapTo<Attachment>()
                            .list()

                    MessageWithAttachments(m, attachments)
                }
            } else {
                emptyList()
            }
        }

    override fun getMessage(messageId: UUID): MessageWithAttachments =
        jdbi.withHandleUnchecked { handle ->
            val message =
                handle
                    .createQuery(
                        """
                        SELECT *
                        FROM sent_message
                        WHERE id = :messageId
                        """.trimIndent()
                    ).bind("messageId", messageId)
                    .mapTo<QueuedMessage>()
                    .one()

            val attachments =
                handle
                    .createQuery(
                        """
                        SELECT key, name, id
                        FROM attachment
                        WHERE message_id = :messageId
                        ORDER BY created
                        """.trimIndent()
                    ).bind("messageId", messageId)
                    .mapTo<Attachment>()
                    .list()

            MessageWithAttachments(message, attachments)
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
        emailType: EmailType,
        recipientEmails: List<String>,
    ): List<String> {
        val query =
            """
            SELECT recipient_email FROM processed_message
            WHERE reservation_type = :reservationType
            AND reservation_id = :reservationId
            AND message_type = :emailType
            AND recipient_email = ANY(:recipientEmails)
            """.trimIndent()
        val alreadySentEmails =
            jdbi.withHandleUnchecked { handle ->
                handle
                    .createQuery(query)
                    .bind("reservationType", reservationType)
                    .bind("reservationId", reservationId)
                    .bind("emailType", emailType.toString())
                    .bind("recipientEmails", recipientEmails.toTypedArray())
                    .mapTo<String>()
                    .list()
            }

        val emailsNotSent = recipientEmails.distinct().filter { !alreadySentEmails.contains(it) }

        if (emailsNotSent.isEmpty()) return emptyList()

        jdbi.withHandleUnchecked { handle ->
            val batch =
                handle.prepareBatch(
                    """
                        INSERT INTO processed_message (reservation_type, reservation_id, message_type, recipient_email)
                        VALUES (:reservationType, :reservationId, :emailType, :recipientEmail)
                        """
                )

            emailsNotSent.forEach { email ->
                batch
                    .bind("reservationType", reservationType)
                    .bind("reservationId", reservationId)
                    .bind("emailType", emailType.toString())
                    .bind("recipientEmail", email)
                    .add()
            }

            batch.execute()
        }
        return emailsNotSent
    }
}
