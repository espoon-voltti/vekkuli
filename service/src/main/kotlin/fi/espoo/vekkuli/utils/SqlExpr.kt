package fi.espoo.vekkuli.utils

abstract class SqlExpr {
    abstract fun toSql(): String
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
}

class StringOperatorExpr(
    private val columnName: String,
    private val operator: String,
    private val value: String?
) : SqlExpr() {
    override fun toSql(): String {
        if (value == null) return ""
        return "$columnName $operator '$value'"
    }
}

class IntOperatorExpr(
    private val columnName: String,
    private val operator: String,
    private val value: Int?
) : SqlExpr() {
    override fun toSql(): String {
        if (value == null) return ""
        return "$columnName $operator $value"
    }
}

fun stringOperatorExp(
    columnName: String,
    operator: String,
    value: String?
): SqlExpr = StringOperatorExpr(columnName, operator, value)

fun intOperatorExp(
    columnName: String,
    operator: String,
    value: Int?
): SqlExpr = IntOperatorExpr(columnName, operator, value)
