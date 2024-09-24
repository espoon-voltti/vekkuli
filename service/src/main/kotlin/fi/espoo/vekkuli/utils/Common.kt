package fi.espoo.vekkuli.utils

/** Replace spaces with '&' and append ':*' to each term for prefix matching **/
fun formatNameSearchParam(nameSearchParam: String): String {
    return nameSearchParam
        .trim()
        .split("\\s+".toRegex())
        .joinToString(" & ") { "$it:*" }
}

fun buildNameSearchClause(nameSearchParam: String?): String {
    return if (!nameSearchParam.isNullOrEmpty()) {
        "c.full_name_tsvector @@ to_tsquery('simple', :nameSearch)"
    } else {
        "true"
    }
}
