package fi.espoo.vekkuli.repository.filter

enum class SortDirection {
    Ascending,
    Descending
}

open class SortBy(
    private val fields: List<Pair<String, SortDirection>>,
    private val allowedFields: List<String>,
) {
    fun apply(): String {
        val orderClauses =
            fields.mapNotNull { (field, direction) ->
                field.takeIf { it in allowedFields }?.let {
                    val sortDir = if (direction == SortDirection.Descending) "DESC" else "ASC"
                    "$it $sortDir"
                }
            }

        return if (orderClauses.isNotEmpty()) {
            "ORDER BY ${orderClauses.joinToString(", ")}"
        } else {
            ""
        }
    }
}
