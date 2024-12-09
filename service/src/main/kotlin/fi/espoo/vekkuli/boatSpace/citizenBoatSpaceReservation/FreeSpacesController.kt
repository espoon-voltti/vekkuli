package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.service.BoatSpaceService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.Min
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

data class FreeSpacesResponse(
    val placesWithFreeSpaces: List<PlacesWithFreeSpaces>,
    val count: Int
)

@RestController
@RequestMapping("/api/citizen")
class FreeSpacesController(
    private val boatSpaceService: BoatSpaceService,
) {
    @GetMapping("/public/free-spaces/search")
    fun getFreeSpaces(
        @RequestParam boatType: BoatType,
        @RequestParam spaceType: BoatSpaceType,
        @RequestParam @Min(0) width: BigDecimal?,
        @RequestParam @Min(0) length: BigDecimal?,
        @RequestParam amenities: List<BoatSpaceAmenity>?,
        @RequestParam storageType: BoatSpaceAmenity?,
        @RequestParam harbor: List<String>?,
        request: HttpServletRequest,
    ): FreeSpacesResponse {
        val freeSpacesByHarbor =
            boatSpaceService.getUnreservedBoatSpaceOptions(
                boatType,
                width,
                length,
                amenities,
                storageType,
                spaceType,
                harbor
            )
        val result =
            FreeSpacesResponse(
                count = freeSpacesByHarbor.second,
                placesWithFreeSpaces =
                    freeSpacesByHarbor.first.map { harbor ->
                        PlacesWithFreeSpaces(
                            place =
                                Place(
                                    id = harbor.location.id,
                                    name = harbor.location.name
                                ),
                            spaces =
                                harbor.boatSpaces.map { space ->
                                    FreeSpace(
                                        id = space.id,
                                        size =
                                            SpaceSize(
                                                width = space.widthCm,
                                                length = space.lengthCm
                                            ),
                                        amenity = space.amenity.name,
                                        price = space.priceCents,
                                        identifier = space.place
                                    )
                                }
                        )
                    }
            )
        return result
    }
}
