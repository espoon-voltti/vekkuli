package fi.espoo.vekkuli.boatSpace.emailAttachments

import fi.espoo.vekkuli.domain.Attachment
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
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
            jdbi.withHandleUnchecked { handle ->
                handle
                    .createUpdate("INSERT INTO attachment (key, name) VALUES (:key, :name)")
                    .bind("key", key)
                    .bind("name", name)
                    .executeAndReturnGeneratedKeys("id")
                    .mapTo<String>()
                    .one()
            }
        return id
    }

    fun getAttachment(id: UUID): Attachment? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    "" +
                        "SELECT id, key, name FROM attachment WHERE id = :id"
                ).bind("id", id)
                .mapTo<Attachment>()
                .one()
        }

    fun getAttachments(ids: List<UUID>): List<Attachment> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    "" +
                        "SELECT id, key, name FROM attachment WHERE id IN(<ids>)"
                ).bindList("ids", ids)
                .mapTo<Attachment>()
                .list()
        }

    fun addAttachmentsToMessages(
        ids: List<UUID>,
        messageId: List<UUID>,
    ): List<String> =
        jdbi.withHandleUnchecked { handle ->
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
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    "SELECT a.key FROM attachment a JOIN sent_message sm ON a.message_id = sm.id WHERE sm.id = :messageId"
                ).bind("messageId", messageId)
                .mapTo<String>()
                .list()
        }
}
