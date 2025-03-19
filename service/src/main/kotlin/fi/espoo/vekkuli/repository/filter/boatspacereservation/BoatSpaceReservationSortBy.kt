package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.domain.BoatSpaceReservationFilterColumn
import fi.espoo.vekkuli.repository.filter.SortBy
import fi.espoo.vekkuli.repository.filter.SortDirection

class BoatSpaceReservationSortBy(
    columns: List<Pair<BoatSpaceReservationFilterColumn, SortDirection>>,
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
        private val columnFieldMap: Map<BoatSpaceReservationFilterColumn, List<String>> =
            mapOf(
                BoatSpaceReservationFilterColumn.START_DATE to listOf("start_date"),
                BoatSpaceReservationFilterColumn.END_DATE to listOf("end_date"),
                BoatSpaceReservationFilterColumn.PLACE to
                    listOf(
                        "location",
                        "place"
                    ),
                BoatSpaceReservationFilterColumn.PLACE_TYPE to listOf("type"),
                BoatSpaceReservationFilterColumn.CUSTOMER to listOf("name"),
                BoatSpaceReservationFilterColumn.EMAIL to listOf("email"),
                BoatSpaceReservationFilterColumn.PHONE to listOf("phone"),
                BoatSpaceReservationFilterColumn.HOME_TOWN to listOf("municipality_name"),
                BoatSpaceReservationFilterColumn.BOAT to listOf("boat_registration_code"),
                BoatSpaceReservationFilterColumn.AMENITY to listOf("amenity", "storage_type"),
                BoatSpaceReservationFilterColumn.WARNING_CREATED to listOf("rw.created"),
            )
    }
}
