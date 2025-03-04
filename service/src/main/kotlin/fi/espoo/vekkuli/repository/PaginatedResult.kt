package fi.espoo.vekkuli.repository

open class PaginatedResult<T>(
    val items: List<T>,
    val totalRows: Int,
    val start: Int,
    val end: Int
)
