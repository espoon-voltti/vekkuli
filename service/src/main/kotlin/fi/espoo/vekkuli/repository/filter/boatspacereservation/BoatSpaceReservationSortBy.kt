package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.domain.BoatSpaceFilterColumn
import fi.espoo.vekkuli.repository.filter.SortBy
import fi.espoo.vekkuli.repository.filter.SortDirection

class BoatSpaceReservationSortBy(
    columns: List<Pair<BoatSpaceFilterColumn, SortDirection>>,
) : SortBy(
        fields =
            columns.flatMap { (field, direction) ->
                columnFieldMap[field]?.map { column ->
                    column to direction
                } ?: emptyList()
            },
        allowedFields = columnFieldMap.flatMap { it.value }
    ) {
    companion object {
        private val columnFieldMap: Map<BoatSpaceFilterColumn, List<String>> =
            mapOf(
                BoatSpaceFilterColumn.START_DATE to listOf("start_date"),
                BoatSpaceFilterColumn.END_DATE to listOf("end_date"),
                BoatSpaceFilterColumn.PLACE to
                    listOf(
                        "location",
                        "place"
                    ),
                BoatSpaceFilterColumn.PLACE_TYPE to listOf("type"),
                BoatSpaceFilterColumn.CUSTOMER to listOf("name"),
                BoatSpaceFilterColumn.HOME_TOWN to listOf("municipality_name"),
                BoatSpaceFilterColumn.BOAT to listOf("boat_registration_code")
            )
    }
}
