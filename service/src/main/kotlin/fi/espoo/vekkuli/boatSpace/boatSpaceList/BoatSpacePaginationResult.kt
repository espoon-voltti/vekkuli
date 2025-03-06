package fi.espoo.vekkuli.boatSpace.boatSpaceList

import fi.espoo.vekkuli.repository.PaginatedResult

class BoatSpacePaginationResult<T>(
    items: List<T>,
    totalRows: Int,
    start: Int,
    end: Int,
) : PaginatedResult<T>(items, totalRows, start, end)
