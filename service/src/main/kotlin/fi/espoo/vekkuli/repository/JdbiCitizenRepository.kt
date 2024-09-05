package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.Citizen
import fi.espoo.vekkuli.domain.CitizenMemo
import fi.espoo.vekkuli.domain.MemoCategory
import fi.espoo.vekkuli.service.CitizenRepository
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
class JdbiCitizenRepository(
    private val jdbi: Jdbi
) : CitizenRepository {
    override fun getCitizen(id: UUID): Citizen? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT * FROM citizen WHERE id = :id
                    """.trimIndent()
                )
            query.bind("id", id)
            val citizens = query.mapTo<Citizen>().toList()
            if (citizens.isEmpty()) null else citizens[0]
        }

    override fun updateCitizen(
        id: UUID,
        phone: String,
        email: String,
    ): Citizen =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE citizen
                    SET phone = :phone, email = :email, updated = :updated
                    WHERE id = :id
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("phone", phone)
            query.bind("email", email)
            query.bind("updated", LocalDate.now())
            query.mapTo<Citizen>().one()
        }

    override fun getMemos(
        citizenId: UUID,
        category: MemoCategory
    ): List<CitizenMemo> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT * FROM citizen_memo
                    WHERE citizen_id = :citizenId AND category = :category
                    """.trimIndent()
                )
            query.bind("citizenId", citizenId)
            query.bind("category", category)
            query.mapTo<CitizenMemo>().toList()
        }

    override fun removeMemo(id: UUID) {
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
        citizenId: UUID,
        userId: UUID,
        category: MemoCategory,
        content: String
    ): CitizenMemo =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    INSERT INTO citizen_memo (citizen_id, created_by, category, content)
                    VALUES (:citizenId, :createdBy, :category, :content)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("citizenId", citizenId)
            query.bind("createdBy", userId)
            query.bind("category", category)
            query.bind("content", content)
            query.mapTo<CitizenMemo>().one()
        }

    override fun updateMemo(
        id: Int,
        updatedBy: UUID,
        content: String
    ): CitizenMemo =
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
            query.mapTo<CitizenMemo>().one()
        }
}
