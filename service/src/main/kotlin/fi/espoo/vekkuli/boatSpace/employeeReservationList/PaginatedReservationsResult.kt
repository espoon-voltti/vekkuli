package fi.espoo.vekkuli.boatSpace.employeeReservationList

import fi.espoo.vekkuli.repository.PaginatedResult

class PaginatedReservationsResult<T>(
    items: List<T>,
    totalRows: Int,
    start: Int,
    end: Int,
    val totalWarnings: Int
) : PaginatedResult<T>(items, totalRows, start, end)
