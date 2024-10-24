package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository

@Repository
class JdbiVariableRepository(
    private val jdbi: Jdbi
) : VariableRepository {
    override fun setVariable(
        id: String,
        value: String
    ): Variable =
        jdbi.withHandleUnchecked { handle ->
            val result =
                handle
                    .createQuery(
                        """
                        INSERT INTO variable (id, value)
                        VALUES (:id, :value)
                        ON CONFLICT (id) 
                        DO UPDATE SET value = EXCLUDED.value
                        RETURNING *
                        """
                    ).bind("id", id)
                    .bind("value", value)
                    .mapTo<Variable>()
                    .one()
            result
        }

    override fun getVariable(id: String): Variable? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT * FROM variable WHERE id = :id
                    """.trimIndent()
                ).bind("id", id)
                .mapTo<Variable>()
                .firstOrNull()
        }

    override fun deleteVariable(id: String): Boolean =
        jdbi.withHandleUnchecked { handle ->
            val rowsAffected =
                handle
                    .createUpdate(
                        """
                        DELETE FROM variable WHERE id = :id
                        """.trimIndent()
                    ).bind("id", id)
                    .execute()
            rowsAffected > 0
        }
}
