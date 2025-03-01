package fi.espoo.vekkuli.utils

import org.jdbi.v3.core.statement.Query
import java.util.concurrent.atomic.AtomicInteger

abstract class SqlExpr {
    abstract fun toSql(): String

    abstract fun bind(query: Query)

    companion object {
        private val nameIndex: AtomicInteger = AtomicInteger(1)

        fun getNextIndex(): Int = nameIndex.getAndIncrement()
    }
}

open class OrExpr(
    private val items: List<SqlExpr>
) : SqlExpr() {
    override fun toSql(): String {
        val filtered = items.map { it.toSql() }.filter { !it.trim().isEmpty() }
        return when (filtered.size) {
            0 -> return ""
            1 -> return filtered[0]
            else -> "(" + filtered.joinToString(" OR ") + ")"
        }
    }

    override fun bind(query: Query) {
        items.forEach { it.bind(query) }
    }
}

open class AndExpr(
    private val items: List<SqlExpr>
) : SqlExpr() {
    override fun toSql(): String {
        val filtered = items.map { it.toSql() }.filter { !it.trim().isEmpty() }
        return when (filtered.size) {
            0 -> return ""
            1 -> return filtered[0]
            else -> "(" + filtered.joinToString(" AND ") + ")"
        }
    }

    override fun bind(query: Query) {
        items.forEach { it.bind(query) }
    }
}

open class OperatorExpr(
    private val columnName: String,
    private val operator: String,
    private val value: Any?
) : SqlExpr() {
    lateinit var name: String

    override fun toSql(): String {
        if (value == null) return ""
        name = "op_${getNextIndex()}"
        return "$columnName $operator :$name"
    }

    override fun bind(query: Query) {
        if (value != null) {
            query.bind(name, value)
        }
    }
}

open class InExpr<T>(
    private val columnName: String,
    private val data: List<T>,
    private val convert: (v: T) -> Any = { v -> v as Any },
    private val isNot: Boolean = false,
) : SqlExpr() {
    private lateinit var names: List<String>

    override fun toSql(): String =
        if (data.isNotEmpty()) {
            val notPart = if (isNot) "NOT " else ""
            names = data.indices.map { "in_${columnName}_${getNextIndex()}" }
            "$columnName ${notPart}IN (${names.joinToString(", ") {":$it"}})"
        } else {
            ""
        }

    override fun bind(query: Query) {
        data.forEachIndexed { index, item ->
            query.bind(names[index], convert(item))
        }
    }
}

class PaginationExpr(
    val start: Int,
    val end: Int
) : SqlExpr() {
    override fun toSql(): String = "LIMIT :pagination_limit OFFSET :pagination_offset"

    override fun bind(query: Query) {
        query.bind("pagination_limit", end - start)
        query.bind("pagination_offset", start)
    }
}

class EmptyExpr : SqlExpr() {
    override fun toSql(): String = ""

    override fun bind(query: Query) {
    }
}

open class ExecuteExpr(
    private val query: String,
) : SqlExpr() {
    override fun toSql(): String = query

    override fun bind(query: Query) {
    }
}
