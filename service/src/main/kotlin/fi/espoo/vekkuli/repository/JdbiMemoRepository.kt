package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.ReservationType
import fi.espoo.vekkuli.domain.ReserverMemo
import fi.espoo.vekkuli.domain.ReserverMemoWithDetails
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JdbiMemoRepository(
    private val jdbi: Jdbi
) : MemoRepository {
    override fun getMemos(
        reserverId: UUID,
        category: ReservationType
    ): List<ReserverMemoWithDetails> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT
                        cm.id,
                        cm.created_at,
                        cm.created_by as created_by_id,
                        CONCAT(auc.first_name, ' ', auc.last_name) as created_by,
                        cm.updated_at,
                        cm.updated_by as updated_by_id,
                        CONCAT(auu.first_name, ' ', auu.last_name) as updated_by,
                        cm.category,
                        cm.reserver_id,
                        cm.content
                    FROM citizen_memo cm
                    LEFT JOIN app_user auc ON created_by = auc.id
                    LEFT JOIN app_user auu ON updated_by = auu.id
                    WHERE reserver_id = :reserverId AND category = :category
                    ORDER BY cm.created_at DESC
                    """.trimIndent()
                )
            query.bind("reserverId", reserverId)
            query.bind("category", category)
            query.mapTo<ReserverMemoWithDetails>().toList()
        }

    override fun getMemo(id: Int): ReserverMemoWithDetails? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT
                        cm.id,
                        cm.created_at,
                        cm.created_by as created_by_id,
                        CONCAT(auc.first_name, ' ', auc.last_name) as created_by,
                        cm.updated_at,
                        cm.updated_by as updated_by_id,
                        CONCAT(auu.first_name, ' ', auu.last_name) as updated_by,
                        cm.category,
                        cm.reserver_id,
                        cm.content
                    FROM citizen_memo cm
                    LEFT JOIN app_user auc ON created_by = auc.id
                    LEFT JOIN app_user auu ON updated_by = auu.id
                    WHERE cm.id = :id
                    """.trimIndent()
                )
            query.bind("id", id)
            query.mapTo<ReserverMemoWithDetails>().findFirst().orElse(null)
        }

    override fun removeMemo(id: Int) {
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    """
                    DELETE FROM citizen_memo
                    WHERE id = :id
                    """.trimIndent()
                ).bind("id", id)
                .execute()
        }
    }

    override fun insertMemo(
        reserverId: UUID,
        userId: UUID?,
        content: String,
        category: ReservationType
    ): ReserverMemo =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    INSERT INTO citizen_memo (reserver_id, created_by, category, content)
                    VALUES (:reserverId, :createdBy, :category, :content)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("reserverId", reserverId)
            query.bind("createdBy", userId)
            query.bind("category", category)
            query.bind("content", content)
            query.mapTo<ReserverMemo>().one()
        }

    override fun updateMemo(
        id: Int,
        updatedBy: UUID,
        content: String
    ): ReserverMemo =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE citizen_memo
                    SET content = :content, updated_at = now(), updated_by = :updatedBy
                    WHERE id = :id
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("updatedBy", updatedBy)
            query.bind("content", content)
            query.mapTo<ReserverMemo>().one()
        }
}
