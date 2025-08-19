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
        key: String,
        name: String
    ): String {
        val id =
            jdbi.withHandle<String, Exception>({ handle ->
                handle
                    .createUpdate("INSERT INTO attachment (key, name) VALUES (:key, :name)")
                    .bind("key", key)
                    .bind("name", name)
                    .executeAndReturnGeneratedKeys("id")
                    .mapTo<String>()
                    .one()
            })
        return id
    }

    fun addAttachmentsToMessages(
        ids: List<UUID>,
        messageId: List<UUID>,
    ): List<String> =
        jdbi.withHandle<List<String>, Exception> { handle ->
            handle
                .createUpdate(
                    "UPDATE attachment SET message_id = :messageId WHERE id IN (<ids>)"
                ).bind("messageId", messageId)
                .bindList("ids", ids)
                .executeAndReturnGeneratedKeys("id")
                .mapTo(String::class.java)
                .list()
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
