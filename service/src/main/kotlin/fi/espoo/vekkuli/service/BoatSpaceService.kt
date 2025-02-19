package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.boatSpace.boatSpaceList.BoatSpaceListParams
import fi.espoo.vekkuli.boatSpace.boatSpaceList.BoatSpaceListRow
import fi.espoo.vekkuli.boatSpace.boatSpaceList.BoatSpaceSortBy
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.filter.SortDirection
import fi.espoo.vekkuli.repository.filter.boatspacereservation.*
import fi.espoo.vekkuli.utils.AndExpr
import fi.espoo.vekkuli.utils.SqlExpr
import fi.espoo.vekkuli.utils.decimalToInt
import org.springframework.stereotype.Service
import java.math.BigDecimal

data class BoatSpaceFilter(
    val boatType: BoatType? = null,
    val boatOrSpaceWidth: Int? = null,
    val boatOrSpaceLength: Int? = null,
    val amenities: List<BoatSpaceAmenity>? = null,
    val boatSpaceType: BoatSpaceType? = null,
    val locationIds: List<Int>? = null
)

interface BoatSpaceRepository {
    fun getUnreservedBoatSpaceOptions(params: BoatSpaceFilter): Pair<List<Harbor>, Int>

    fun getBoatSpace(boatSpace: Int): BoatSpace?

    fun isBoatSpaceReserved(boatSpace: Int): Boolean

    fun getBoatSpaces(
        filter: SqlExpr,
        sortBy: BoatSpaceSortBy? = null
    ): List<BoatSpaceListRow>
}

fun <T> getSingleOrEmptyList(item: T?): List<T> = if (item != null) listOf(item) else listOf()

@Service
class BoatSpaceService(
    private val boatSpaceRepo: BoatSpaceRepository
) {
    fun getBoatSpacesFiltered(params: BoatSpaceListParams): List<BoatSpaceListRow> {
        val sortBy =
            BoatSpaceSortBy(
                listOf(params.sortBy to if (params.ascending) SortDirection.Ascending else SortDirection.Descending)
            )
        val filters: MutableList<SqlExpr> = mutableListOf()

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

        // TODO: add active boat space filter

        val boatSpaces =
            boatSpaceRepo.getBoatSpaces(
                AndExpr(
                    filters
                ),
                sortBy
            )
        return boatSpaces
    }

    fun getUnreservedBoatSpaceOptions(
        boatType: BoatType? = null,
        width: BigDecimal? = null,
        length: BigDecimal? = null,
        amenities: List<BoatSpaceAmenity>? = null,
        boatSpaceType: BoatSpaceType? = null,
        harbor: List<String>? = null,
    ): Pair<List<Harbor>, Int> {
        val params =
            BoatSpaceFilter(
                boatType,
                decimalToInt(width),
                decimalToInt(length),
                amenities,
                boatSpaceType,
                if (boatSpaceType != BoatSpaceType.Storage) harbor?.map { s -> s.toInt() } else null
            )
        return boatSpaceRepo.getUnreservedBoatSpaceOptions(params)
    }
}
