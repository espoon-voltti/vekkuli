package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.boatSpace.boatSpaceDetails.BoatSpaceHistory
import fi.espoo.vekkuli.boatSpace.boatSpaceList.*
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.filter.SortDirection
import fi.espoo.vekkuli.repository.filter.boatspacereservation.*
import fi.espoo.vekkuli.utils.AndExpr
import fi.espoo.vekkuli.utils.PaginationExpr
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
    val locationId: Int?,
    val type: BoatSpaceType?,
    val section: String?,
    val placeNumber: Int?,
    val amenity: BoatSpaceAmenity?,
    val widthCm: Int?,
    val lengthCm: Int?,
    val priceId: Int?,
    val isActive: Boolean?
)

data class CreateBoatSpaceParams(
    val locationId: Int,
    val type: BoatSpaceType,
    val section: String,
    val placeNumber: Int,
    val amenity: BoatSpaceAmenity,
    val widthCm: Int,
    val lengthCm: Int,
    val priceId: Int,
    val isActive: Boolean,
)

interface BoatSpaceRepository {
    fun getUnreservedBoatSpaceOptions(params: BoatSpaceFilter): Pair<List<Harbor>, Int>

    fun getBoatSpace(boatSpace: Int): BoatSpace?

    fun checkIfAnyBoatSpacesHaveReservations(boatSpaceIds: List<Int>): Boolean?

    fun isBoatSpaceAvailable(boatSpace: Int): Boolean

    fun getBoatSpaces(
        filter: SqlExpr,
        sortBy: BoatSpaceSortBy? = null,
        pagination: PaginationExpr? = null,
    ): List<BoatSpaceListRow>

    fun getSections(): List<String>

    fun editBoatSpaces(
        boatSpaceIds: List<Int>,
        editBoatSpaceParams: EditBoatSpaceParams
    )

    fun getBoatSpaceCount(filter: SqlExpr): BoatSpaceStats

    fun deleteBoatSpaces(boatSpaceIds: List<Int>)

    fun createBoatSpace(params: CreateBoatSpaceParams): Int

    fun getBoatSpaceHistory(boatSpaceId: Int): List<BoatSpaceHistory>

    fun getBoatWidthOptions(filter: SqlExpr): List<Int>

    fun getBoatLengthOptions(filter: SqlExpr): List<Int>
}

fun <T> getSingleOrEmptyList(item: T?): List<T> = if (item != null) listOf(item) else listOf()

@Service
class BoatSpaceService(
    private val boatSpaceRepo: BoatSpaceRepository
) {
    fun getBoatSpacesFiltered(
        params: BoatSpaceListParams,
        paginationStart: Int? = null,
        paginationEnd: Int? = null
    ): PaginatedBoatSpaceResult<BoatSpaceListRow> {
        val pagination = PaginationExpr(paginationStart ?: params.paginationStart, paginationEnd ?: params.paginationEnd)
        val filters = buildBoatSpaceFilters(params)
        val sortBy =
            BoatSpaceSortBy(
                listOf(params.sortBy to if (params.ascending) SortDirection.Ascending else SortDirection.Descending) +
                    listOf(BoatSpaceFilterColumn.PLACE to SortDirection.Ascending) // Always sort by place as a fallback
            )

        val boatSpaces =
            boatSpaceRepo.getBoatSpaces(
                filters,
                sortBy,
                pagination
            )
        val boatSpaceStats =
            boatSpaceRepo.getBoatSpaceCount(
                filters
            )

        return PaginatedBoatSpaceResult(boatSpaces, boatSpaceStats.spaces, pagination.start, pagination.end, boatSpaceStats.reservations)
    }

    private fun buildBoatSpaceFilters(params: BoatSpaceListParams): AndExpr {
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

        if (params.showOnlyFreeSpaces) {
            filters.add(ShowOnlyFreeSpacesExpr())
        }

        if (params.lengthFilter.isNotEmpty()) {
            filters.add(BoatSpaceLengthExpr(params.lengthFilter))
        }

        if (params.widthFilter.isNotEmpty()) {
            filters.add(BoatSpaceWidthExpr(params.widthFilter))
        }

        return AndExpr(
            filters
        )
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

    // Width filter shouldn't affect its own options
    fun getBoatWidthOptions(params: BoatSpaceListParams) = boatSpaceRepo.getBoatWidthOptions(buildBoatSpaceFilters(params.copy(widthFilter = emptyList())))

    // Length filter shouldn't affect its own options
    fun getBoatLengthOptions(params: BoatSpaceListParams) = boatSpaceRepo.getBoatLengthOptions(buildBoatSpaceFilters(params.copy(lengthFilter = emptyList())))

    fun editBoatSpaces(
        boatSpaceIds: List<Int>,
        editBoatSpaceParams: EditBoatSpaceParams
    ) {
        var editParams = editBoatSpaceParams

        // If multiple boat spaces are edited, section and place number should not be changed
        if (boatSpaceIds.size > 1) {
            editParams = editParams.copy(section = null, placeNumber = null)
        }
        boatSpaceRepo.editBoatSpaces(boatSpaceIds, editParams)
    }

    fun createBoatSpace(params: CreateBoatSpaceParams): Int = boatSpaceRepo.createBoatSpace(params)

    fun deleteBoatSpaces(boatSpaceIds: List<Int>) {
        val hasReservations = boatSpaceRepo.checkIfAnyBoatSpacesHaveReservations(boatSpaceIds)
        if (hasReservations == true) {
            throw IllegalArgumentException("Some of the boat spaces have reservations")
        }
        boatSpaceRepo.deleteBoatSpaces(boatSpaceIds)
    }

    fun getBoatSpace(boatSpaceId: Int): BoatSpace? = boatSpaceRepo.getBoatSpace(boatSpaceId)

    fun getBoatSpaceHistory(boatSpaceId: Int) = boatSpaceRepo.getBoatSpaceHistory(boatSpaceId)
}
