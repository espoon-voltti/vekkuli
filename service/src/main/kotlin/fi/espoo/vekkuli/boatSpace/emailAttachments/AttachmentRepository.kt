package fi.espoo.vekkuli.boatSpace.emailAttachments

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AttachmentRepository(
    private val jdbi: Jdbi
) {
    fun addAttachment(
        messageId: UUID,
        key: String
    ) {
        jdbi.withHandle<Unit, Exception>({ handle ->
            handle
                .createUpdate("INSERT INTO attachment (key, message_id) VALUES (:key, :messageId)")
                .bind("key", key)
                .bind("messageId", messageId)
                .execute()
        })
    }

    fun getAttachmentKeys(messageId: UUID): List<String> =
        jdbi.withHandle<List<String>, Exception> { handle ->
            handle
                .createQuery(
                    "SELECT a.key FROM attachment a JOIN sent_message sm ON a.message_id = sm.id WHERE sm.id = :messageId"
                ).bind("messageId", messageId)
                .mapTo<String>()
                .list()
        }
}
