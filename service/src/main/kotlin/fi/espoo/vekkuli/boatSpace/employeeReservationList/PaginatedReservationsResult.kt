package fi.espoo.vekkuli.boatSpace.employeeReservationList

import fi.espoo.vekkuli.domain.ReservationWarning
import fi.espoo.vekkuli.repository.PaginatedResult

class PaginatedReservationsResult<T>(
    items: List<T>,
    totalRows: Int,
    start: Int,
    end: Int,
    val warnings: List<ReservationWarning>
) : PaginatedResult<T>(items, totalRows, start, end)
