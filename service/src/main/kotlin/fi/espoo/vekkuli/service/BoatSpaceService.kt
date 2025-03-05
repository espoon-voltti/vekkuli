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

data class EditBoatSpaceParams(
    val type: BoatSpaceType,
    val section: String?,
    val placeNumber: Int?,
    val amenity: BoatSpaceAmenity,
    val widthCm: Int,
    val lengthCm: Int,
    val priceId: Int,
    val isActive: Boolean
)

interface BoatSpaceRepository {
    fun getUnreservedBoatSpaceOptions(params: BoatSpaceFilter): Pair<List<Harbor>, Int>

    fun getBoatSpace(boatSpace: Int): BoatSpace?

    fun isBoatSpaceAvailable(boatSpace: Int): Boolean

    fun getBoatSpaces(
        filter: SqlExpr,
        sortBy: BoatSpaceSortBy? = null
    ): List<BoatSpaceListRow>

    fun getSections(): List<String>

    fun editBoatSpaces(
        boatSpaceIds: List<Int>,
        editBoatSpaceParams: EditBoatSpaceParams
    )
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

        if (params.boatSpaceState.isNotEmpty() && params.boatSpaceState.size != BoatSpaceState.entries.size) {
            filters.add(IsBoatSpaceStateExpr(params.boatSpaceState))
        }

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

    fun getSections() = boatSpaceRepo.getSections()

    fun editBoatSpaces(
        boatSpaceIds: List<Int>,
        editBoatSpaceParams: EditBoatSpaceParams
    ) {
        var editParams = editBoatSpaceParams
        if (boatSpaceIds.size > 1) {
            editParams = editParams.copy(section = null, placeNumber = null)
        }
        boatSpaceRepo.editBoatSpaces(boatSpaceIds, editParams)
    }
}
