package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.utils.SqlExpr
import org.jdbi.v3.core.statement.Query

class EmailSearchExpr(
    private val keyword: String,
) : SqlExpr() {
    lateinit var placeholder: String

    override fun toSql(): String {
        placeholder = "email_search_${getNextIndex()}"
        return "(r.email ILIKE REPLACE(REPLACE(:$placeholder, '%', ''), '_', '') || '%')"
    }

    override fun bind(query: Query) {
        query.bind(placeholder, keyword)
    }
}
