package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.*
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
}

fun <T> getSingleOrEmptyList(item: T?): List<T> = if (item != null) listOf(item) else listOf()

@Service
class BoatSpaceService(
    private val boatSpaceRepo: BoatSpaceRepository
) {
    fun getUnreservedBoatSpaceOptions(
        boatType: BoatType? = null,
        width: BigDecimal? = null,
        length: BigDecimal? = null,
        amenities: List<BoatSpaceAmenity>? = null,
        storageType: BoatSpaceAmenity? = null,
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
