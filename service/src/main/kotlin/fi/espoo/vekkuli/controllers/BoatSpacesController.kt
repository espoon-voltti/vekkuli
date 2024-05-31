package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.controllers.AppController.Companion.WIDTH_MAX_TOLERANCE
import fi.espoo.vekkuli.controllers.AppController.Companion.WIDTH_MIN_TOLERANCE
import fi.espoo.vekkuli.domain.*
import jakarta.validation.constraints.Min
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class BoatSpacesController {
    @Autowired
    lateinit var jdbi: Jdbi

    @GetMapping("/venepaikat2")
    fun boatSpaces(
        @RequestParam @Min(1) page: Int = 1,
        @RequestParam pageSize: Int = 25,
        @RequestParam @Min(0) width: Float?,
        @RequestParam @Min(0) length: Float?,
        @RequestParam locationId: Int?,
        @RequestParam amenity: BoatSpaceAmenity?,
        @RequestParam boatSpaceType: BoatSpaceType?,
        model: Model
    ): String {
        val locations =
            jdbi.inTransactionUnchecked { tx ->
                tx.getLocations()
            }

        val results =
            jdbi.inTransactionUnchecked { tx ->
                tx.getBoatSpaces(
                    BoatSpaceFilter(
                        page = page,
                        pageSize = pageSize,
                        minWidth = width?.mToCm()?.plus(WIDTH_MIN_TOLERANCE),
                        maxWidth = width?.mToCm()?.plus(WIDTH_MAX_TOLERANCE),
                        minLength = length?.mToCm(),
                        maxLength = null,
                        amenity = amenity,
                        locationId = locationId,
                        boatSpaceType = boatSpaceType,
                    )
                )
            }

        model.addAttribute("page", page)
        model.addAttribute("pageSize", pageSize)
        model.addAttribute("width", width)
        model.addAttribute("length", length)
        model.addAttribute("locations", locations)
        model.addAttribute("locationId", locationId)
        model.addAttribute(
            "amenities",
            listOf(
                BoatSpaceAmenity.None,
                BoatSpaceAmenity.Buoy,
                BoatSpaceAmenity.RearBuoy,
                BoatSpaceAmenity.Beam,
                BoatSpaceAmenity.WalkBeam
            )
        )
        model.addAttribute("amenity", amenity)
        model.addAttribute("boatSpaceType", boatSpaceType)
        model.addAttribute("results", results)

        return "boat-spaces"
    }

    @GetMapping("/partial/venepaikat2")
    fun boatSpaceTable(
        @RequestParam @Min(1) page: Int = 1,
        @RequestParam pageSize: Int = 25,
        @RequestParam @Min(0) width: Float?,
        @RequestParam @Min(0) length: Float?,
        @RequestParam locationId: Int?,
        @RequestParam amenity: BoatSpaceAmenity?,
        @RequestParam boatSpaceType: BoatSpaceType?,
        model: Model
    ): String {
        val results =
            jdbi.inTransactionUnchecked { tx ->
                tx.getBoatSpaces(
                    BoatSpaceFilter(
                        page = page,
                        pageSize = pageSize,
                        minWidth = width?.mToCm()?.plus(WIDTH_MIN_TOLERANCE),
                        maxWidth = width?.mToCm()?.plus(WIDTH_MAX_TOLERANCE),
                        minLength = length?.mToCm(),
                        maxLength = null,
                        amenity = amenity,
                        locationId = locationId,
                        boatSpaceType = boatSpaceType,
                    )
                )
            }
        model.addAttribute("results", results)
        return "boat-space-table"
    }
}
