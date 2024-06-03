package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.domain.*
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.Min
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

const val TEXT_HTML_UTF8 = "${MediaType.TEXT_HTML_VALUE};charset=UTF-8"

fun Int.cmToM(): Float = this / 100F

fun Float.mToCm(): Int = (this * 100F).toInt()

@Controller
class BoatSpacesController {
    @Autowired
    lateinit var jdbi: Jdbi

    companion object {
        const val WIDTH_MIN_TOLERANCE = 40
        const val WIDTH_MAX_TOLERANCE = 100
    }

    @GetMapping("/venepaikat")
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

        addModelAttributes(model, page, pageSize, width, length, locations, locationId, amenity, boatSpaceType, results)

        return "boat-spaces"
    }

    @GetMapping("/partial/venepaikat")
    fun boatSpaceTable(
        @RequestParam @Min(1) page: Int = 1,
        @RequestParam pageSize: Int = 25,
        @RequestParam @Min(0) width: Float?,
        @RequestParam @Min(0) length: Float?,
        @RequestParam locationId: Int?,
        @RequestParam amenity: BoatSpaceAmenity?,
        @RequestParam boatSpaceType: BoatSpaceType?,
        model: Model,
        response: HttpServletResponse
    ): String {
        val qs = createQueryString(page, pageSize, width, length, locationId, amenity)
        response.setHeader(
            "HX-Push-Url",
            qs
        )
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
        val locations =
            jdbi.inTransactionUnchecked { tx ->
                tx.getLocations()
            }
        addModelAttributes(model, page, pageSize, width, length, locations, locationId, amenity, boatSpaceType, results)

        return "boat-space-table"
    }

    private fun addModelAttributes(
        model: Model,
        page: Int,
        pageSize: Int,
        width: Float?,
        length: Float?,
        locations: List<Location>,
        locationId: Int?,
        amenity: BoatSpaceAmenity?,
        boatSpaceType: BoatSpaceType?,
        results: List<BoatSpace>
    ) {
        model.addAttribute("page", page)
        model.addAttribute("pageSize", pageSize)
        model.addAttribute("width", width)
        model.addAttribute("length", length)
        model.addAttribute("locations", locations)
        model.addAttribute("locationId", locationId)
        model.addAttribute(
            "amenities",
            listOf(
                BoatSpaceAmenity.None.toString(),
                BoatSpaceAmenity.Buoy.toString(),
                BoatSpaceAmenity.RearBuoy.toString(),
                BoatSpaceAmenity.Beam.toString(),
                BoatSpaceAmenity.WalkBeam.toString()
            )
        )
        model.addAttribute("amenity", amenity)
        model.addAttribute("boatSpaceType", boatSpaceType)
        model.addAttribute("results", results)

        val pages =
            if (results.isEmpty()) {
                1
            } else {
                results[0].totalCount / pageSize + (if (results[0].totalCount % pageSize > 0) 1 else 0)
            }

        // page navigation
        val prevPageVal = if (page != 1) "js:{page: ${page - 1}}" else null
        model.addAttribute("prevPageVal", prevPageVal)

        model.addAttribute("currentPage", "$page/$pages")

        val nextPageVal = if (page < pages) "js:{page: ${page + 1}}" else null
        model.addAttribute("nextPageVal", nextPageVal)
    }

    fun createQueryString(
        page: Int = 1,
        pageSize: Int = 25,
        width: Float?,
        length: Float?,
        locationId: Int?,
        amenity: BoatSpaceAmenity?
    ): String {
        val queryString = StringBuilder("?")
        queryString.append("page=$page&pageSize=$pageSize")
        width?.let { queryString.append("&width=$it") }
        length?.let { queryString.append("&length=$it") }
        locationId?.let { queryString.append("&locationId=$it") }
        amenity?.let { queryString.append("&amenity=$it") }
        return queryString.toString()
    }
}
