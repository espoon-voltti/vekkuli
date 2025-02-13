package fi.espoo.vekkuli.boatSpace.boatSpaceList

import fi.espoo.vekkuli.domain.BoatSpaceFilterColumn
import fi.espoo.vekkuli.repository.filter.SortBy
import fi.espoo.vekkuli.repository.filter.SortDirection

class BoatSpaceSortBy(
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
                BoatSpaceFilterColumn.PLACE to
                    listOf(
                        "location_name",
                        "place"
                    ),
                BoatSpaceFilterColumn.PLACE_TYPE to listOf("type"),
                BoatSpaceFilterColumn.AMENITY to listOf("amenity"),
                BoatSpaceFilterColumn.PLACE_WIDTH to listOf("width_cm"),
                BoatSpaceFilterColumn.PLACE_LENGTH to listOf("length_cm"),
                BoatSpaceFilterColumn.PRICE to listOf("price_cents", "price_class"),
                BoatSpaceFilterColumn.ACTIVE to listOf(""),
                BoatSpaceFilterColumn.RESERVER to listOf("reserver_name")
            )
    }
}
