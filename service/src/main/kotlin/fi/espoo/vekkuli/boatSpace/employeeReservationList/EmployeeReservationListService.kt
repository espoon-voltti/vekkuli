package fi.espoo.vekkuli.boatSpace.employeeReservationList

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.filter.SortDirection
import fi.espoo.vekkuli.repository.filter.boatspacereservation.AmenityExpr
import fi.espoo.vekkuli.repository.filter.boatspacereservation.BoatSpaceReservationSortBy
import fi.espoo.vekkuli.repository.filter.boatspacereservation.BoatSpaceTypeExpr
import fi.espoo.vekkuli.repository.filter.boatspacereservation.EndDateNotPassedExpr
import fi.espoo.vekkuli.repository.filter.boatspacereservation.EndDatePassedExpr
import fi.espoo.vekkuli.repository.filter.boatspacereservation.HasReserverExceptionsExpr
import fi.espoo.vekkuli.repository.filter.boatspacereservation.HasWarningExpr
import fi.espoo.vekkuli.repository.filter.boatspacereservation.LocationExpr
import fi.espoo.vekkuli.repository.filter.boatspacereservation.NameSearchExpr
import fi.espoo.vekkuli.repository.filter.boatspacereservation.PhoneSearchExpr
import fi.espoo.vekkuli.repository.filter.boatspacereservation.ReservationValidityExpr
import fi.espoo.vekkuli.repository.filter.boatspacereservation.SectionExpr
import fi.espoo.vekkuli.repository.filter.boatspacereservation.StatusExpr
import fi.espoo.vekkuli.utils.AndExpr
import fi.espoo.vekkuli.utils.PaginationExpr
import fi.espoo.vekkuli.utils.SqlExpr
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Service

@Service
class EmployeeReservationListService(
    private val timeProvider: TimeProvider,
    private val jdbi: Jdbi
) {
    fun getBoatSpaceReservations(
        params: BoatSpaceReservationFilter,
        paginationStart: Int? = null,
        paginationEnd: Int? = null
    ): PaginatedReservationsResult<BoatSpaceReservationItem> {
        val pagination = PaginationExpr(paginationStart ?: params.paginationStart, paginationEnd ?: params.paginationEnd)
        val filters: MutableList<SqlExpr> = mutableListOf()
        // Add status filters based on the payment status
        filters.add(
            StatusExpr(
                params.payment
                    .flatMap {
                        when (it) {
                            PaymentFilter.CONFIRMED -> listOf(ReservationStatus.Confirmed)
                            PaymentFilter.INVOICED -> listOf(ReservationStatus.Invoiced)
                            PaymentFilter.PAYMENT -> listOf(ReservationStatus.Payment, ReservationStatus.Info)
                            PaymentFilter.CANCELLED -> listOf(ReservationStatus.Cancelled)
                        }
                    }.ifEmpty {
                        listOf(
                            ReservationStatus.Confirmed,
                            ReservationStatus.Invoiced,
                            ReservationStatus.Payment,
                            ReservationStatus.Info,
                            ReservationStatus.Cancelled
                        )
                    }
            )
        )

        if (params.expiration == ReservationExpiration.Active) {
            filters.add(EndDateNotPassedExpr(timeProvider.getCurrentDate()))
        } else {
            filters.add(EndDatePassedExpr(timeProvider.getCurrentDate()))
        }

        if (params.warningFilter == true) {
            filters.add(HasWarningExpr())
        }

        if (params.exceptionsFilter == true) {
            filters.add(HasReserverExceptionsExpr())
        }

        if (!params.nameSearch.isNullOrBlank()) {
            filters.add(NameSearchExpr(params.nameSearch))
        }

        if (!params.phoneSearch.isNullOrBlank()) {
            filters.add(PhoneSearchExpr(params.phoneSearch))
        }

        if (params.harbor.isNotEmpty()) {
            filters.add(LocationExpr(params.harbor))
        }

        if (params.boatSpaceType.isNotEmpty()) {
            filters.add(BoatSpaceTypeExpr(params.boatSpaceType))
        }

        if (params.amenity.isNotEmpty()) {
            filters.add(AmenityExpr(params.amenity))
        }

        if (params.sectionFilter.isNotEmpty()) {
            filters.add(SectionExpr(params.sectionFilter))
        }

        if (params.validity.isNotEmpty()) {
            filters.add(ReservationValidityExpr(params.validity))
        }

        val direction = if (params.ascending) SortDirection.Ascending else SortDirection.Descending
        val warningsSort =
            if (params.warningFilter == true) {
                listOf(
                    BoatSpaceReservationFilterColumn.WARNING_CREATED to SortDirection.Descending
                )
            } else {
                emptyList()
            }
        val sortBy =
            BoatSpaceReservationSortBy(
                warningsSort +
                    listOf(
                        params.sortBy to direction
                    )
            )

        return getPaginatedBoatSpaceReservationItemsWithWarnings(
            AndExpr(
                filters
            ),
            sortBy,
            pagination
        )
    }

    fun getPaginatedBoatSpaceReservationItemsWithWarnings(
        filters: SqlExpr,
        sortBy: BoatSpaceReservationSortBy,
        pagination: PaginationExpr
    ): PaginatedReservationsResult<BoatSpaceReservationItem> {
        val paginatedIds = getFilteredAndPaginatedBoatSpaceReservationIds(jdbi, filters, sortBy, pagination)
        val reservations =
            getBoatSpaceReservationItemsByIds(
                jdbi,
                paginatedIds.items,
                sortBy
            )
        val warningCount = getFilteredBoatSpaceReservationWarningCount(jdbi, filters)
        return PaginatedReservationsResult(reservations, paginatedIds.totalRows, paginatedIds.start, paginatedIds.end, warningCount)
    }
}
