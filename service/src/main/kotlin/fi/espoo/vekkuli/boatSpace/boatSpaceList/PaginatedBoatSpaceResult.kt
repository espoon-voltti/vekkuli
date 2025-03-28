package fi.espoo.vekkuli.boatSpace.boatSpaceList

import fi.espoo.vekkuli.repository.PaginatedResult

class PaginatedBoatSpaceResult<T>(
    items: List<T>,
    totalRows: Int,
    start: Int,
    end: Int,
    val reservedSpaces: Int
) : PaginatedResult<T>(items, totalRows, start, end)

data class BoatSpaceStats(
    val spaces: Int,
    val reservations: Int
)
