package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.intToDecimal
import org.springframework.stereotype.Service
import java.math.BigDecimal

data class BoatSpaceResponse(
    val id: Int,
    val type: BoatSpaceType,
    val section: String,
    val placeNumber: Int,
    val amenity: BoatSpaceAmenity,
    val width: BigDecimal,
    val length: BigDecimal,
    val locationName: String?,
    val locationId: Int?
)

@Service
class BoatSpaceResponseMapper(
    private val boatSpaceRepository: BoatSpaceRepository,
) {
    fun toBoatSpaceResponse(spaceId: Int): BoatSpaceResponse = boatSpaceResponse(spaceId)

    private fun boatSpaceResponse(spaceId: Int): BoatSpaceResponse {
        val boatSpace = getBoatSpace(spaceId)

        return formatBoatSpace(
            boatSpace
        )
    }

    private fun getBoatSpace(spaceId: Int): BoatSpace = boatSpaceRepository.getBoatSpace(spaceId) ?: throw NotFound()

    private fun formatBoatSpace(boatSpace: BoatSpace): BoatSpaceResponse =
        BoatSpaceResponse(
            id = boatSpace.id,
            type = boatSpace.type,
            section = boatSpace.section,
            placeNumber = boatSpace.placeNumber,
            amenity = boatSpace.amenity,
            width = intToDecimal(boatSpace.widthCm),
            length = intToDecimal(boatSpace.lengthCm),
            locationName = boatSpace.locationName,
            locationId = boatSpace.locationId
        )
}
