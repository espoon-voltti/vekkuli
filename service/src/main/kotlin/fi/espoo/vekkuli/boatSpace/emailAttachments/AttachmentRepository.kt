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
        name: String,
        sizeBytes: Long,
    ): UUID {
        val id =
            jdbi.withHandleUnchecked { handle ->
                handle
                    .createUpdate(
                        "INSERT INTO attachment (key, name, size_bytes) VALUES (:key, :name, :sizeBytes)"
                    )
                    .bind("key", key)
                    .bind("name", name)
                    .bind("sizeBytes", sizeBytes)
                    .executeAndReturnGeneratedKeys("id")
                    .mapTo<UUID>()
                    .one()
            }
        return id
    }

    fun getAttachment(id: UUID): Attachment? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    "SELECT id, key, name, size_bytes AS sizeBytes FROM attachment WHERE id = :id"
                ).bind("id", id)
                .mapTo<Attachment>()
                .singleOrNull()
        }

    fun deleteAttachment(id: UUID) {
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate("DELETE FROM attachment WHERE id = :id")
                .bind("id", id)
                .execute()
        }
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

    fun findSizesByIds(ids: List<UUID>): Map<UUID, Long?> {
        if (ids.isEmpty()) return emptyMap()
        return jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    "SELECT id, size_bytes FROM attachment WHERE id IN (<ids>)"
                )
                .bindList("ids", ids)
                .map { rs, _ ->
                    val uuid = rs.getObject("id", UUID::class.java)
                    val raw = rs.getLong("size_bytes")
                    val size: Long? = if (rs.wasNull()) null else raw
                    uuid to size
                }
                .list()
                .toMap()
        }
    }
}
