package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.SentMessage
import fi.espoo.vekkuli.service.SentMessageRepository
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JdbiSentMessageRepository(
    private val jdbi: Jdbi
) : SentMessageRepository {
    override fun addSentEmail(
        senderId: UUID?,
        senderAddress: String,
        recipientId: UUID,
        recipientEmail: String,
        subject: String,
        body: String,
    ): SentMessage =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    INSERT INTO sent_message (status, sender_id, sender_address, recipient_id, recipient_address, type, subject, body)
                    VALUES ('Queued', :senderId, :senderAddress, :recipientId, :recipientEmail, 'Email', :subject, :body)
                    RETURNING *
                    """
                ).bind("senderId", senderId)
                .bind("senderAddress", senderAddress)
                .bind("recipientId", recipientId)
                .bind("recipientEmail", recipientEmail)
                .bind("subject", subject)
                .bind("body", body)
                .mapTo<SentMessage>()
                .one()
        }

    override fun setMessageSent(
        messageId: UUID,
        providerId: String
    ): SentMessage =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    UPDATE sent_message
                    SET status = 'Sent', sent_at = now(), provider_id = :providerId
                    WHERE id = :messageId
                    RETURNING *
                    """
                ).bind("messageId", messageId)
                .bind("providerId", providerId)
                .mapTo<SentMessage>()
                .one()
        }

    override fun setMessageFailed(
        messageId: UUID,
        providerId: String
    ): SentMessage =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    UPDATE sent_message
                    SET status = 'Failed', sent_at = now(), provider_id = :providerId
                    WHERE id = :messageId
                    RETURNING *
                    """
                ).bind("messageId", messageId)
                .bind("providerId", providerId)
                .mapTo<SentMessage>()
                .one()
        }

    override fun getMessagesSentToUser(citizenId: UUID): List<SentMessage> =
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
                .mapTo<SentMessage>()
                .list()
        }
}
