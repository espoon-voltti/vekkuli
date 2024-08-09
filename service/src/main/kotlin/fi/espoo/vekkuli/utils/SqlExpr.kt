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

class OrExpr(
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

class AndExpr(
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

class OperatorExpr(
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

data class InExpr<T>(
    private val columnName: String,
    private val data: List<T>,
    private val convert: (v: T) -> String = fun (v) = v.toString(),
) : SqlExpr() {
    override fun toSql(): String =
        if (data.isNotEmpty()) {
            "$columnName IN (${data.map { convert(it) }.joinToString(", ")})"
        } else {
            ""
        }

    override fun bind(query: Query) {
    }
}

class EmptyExpr : SqlExpr() {
    override fun toSql(): String = ""

    override fun bind(query: Query) {
    }
}
