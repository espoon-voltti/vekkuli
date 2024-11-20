package fi.espoo.vekkuli.utils

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.statement.SqlLogger
import org.jdbi.v3.core.statement.StatementContext
import java.util.*

class DbUtil {
    companion object {
        fun updateTable(
            handle: Handle,
            table: String,
            id: UUID,
            params: Map<String, Any?>
        ) {
            val sql = mutableListOf<String>()
            val bindings = mutableMapOf<String, Any?>()

            bindings["id"] = id

            params.forEach { (key, value) ->
                sql.add("$key = :$key")
                bindings[key] = value
            }

            val updateQuery =
                """
                UPDATE $table
                SET ${sql.joinToString(", ")}
                WHERE id = :id
                """.trimIndent()

            val q = handle.createUpdate(updateQuery)
            bindings.forEach { (key, value) -> q.bind(key, value) }
            q.execute()
        }

        fun buildNameSearchClause(nameSearchParam: String?): String {
            return if (!nameSearchParam.isNullOrBlank()) {
                return "(r.name ILIKE '%' || REPLACE(REPLACE(:nameSearch, '%', ''), '_', '') || '%')"
            } else {
                "true"
            }
        }
    }
}

class SqlDebugLogger : SqlLogger {
    override fun logBeforeExecution(context: StatementContext) {
        println("Rendered SQL: ${context.renderedSql}")
        println("Bindings: ${context.binding}")
    }
}
