package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.*
import org.springframework.stereotype.Service

data class BoatSpaceFilter(
    val boatType: BoatType? = null,
    val boatWidth: Int? = null,
    val boatLength: Int? = null,
    val amenities: List<BoatSpaceAmenity>? = null,
    val boatSpaceType: BoatSpaceType? = null,
    val locationIds: List<Int>? = null
)

interface BoatSpaceRepository {
    fun getUnreservedBoatSpaceOptions(params: BoatSpaceFilter): Pair<List<Harbor>, Int>
}

@Service
class BoatSpaceService(
    private val boatSpaceRepo: BoatSpaceRepository
) {
    fun getUnreservedBoatSpaceOptions(params: BoatSpaceFilter): Pair<List<Harbor>, Int> =
        boatSpaceRepo.getUnreservedBoatSpaceOptions(params)
}
