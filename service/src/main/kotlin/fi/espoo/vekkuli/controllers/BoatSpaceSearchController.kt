package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.mToCm
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.Min
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

data class BoatFilter(
    val width: Double?,
    val length: Double?,
    val type: BoatType?
)

@Controller
@RequestMapping("/kuntalainen")
class BoatSpaceSearchController {
    @Autowired
    lateinit var jdbi: Jdbi

    @RequestMapping("/venepaikat")
    fun boatSpaceSearchPage(
        request: HttpServletRequest,
        model: Model
    ): String {
        val citizen = getCitizen(request, jdbi)
        if (citizen != null) {
            val reservation =
                jdbi.inTransactionUnchecked {
                    it.getReservationForCitizen(citizen.id)
                }
            if (reservation != null) {
                return redirectUrl("/kuntalainen/venepaikka/varaus/${reservation.id}")
            }
        }
        val locations =
            jdbi.inTransactionUnchecked { tx ->
                tx.getLocations()
            }
        model.addAttribute(
            "amenities",
            BoatSpaceAmenity.entries.map { it.toString() }
        )
        model.addAttribute(
            "boatTypes",
            BoatType.entries.map { it.toString() }
        )
        model.addAttribute("locations", locations)

        return "boat-space-search"
    }

    @RequestMapping("/partial/vapaat-paikat")
    fun searchResultPartial(
        @RequestParam(required = false) boatType: BoatType?,
        @RequestParam @Min(0) width: Double?,
        @RequestParam @Min(0) length: Double?,
        @RequestParam amenities: List<BoatSpaceAmenity>?,
        @RequestParam boatSpaceType: BoatSpaceType?,
        @RequestParam harbor: List<String>?,
        model: Model
    ): String {
        val harbors =
            jdbi.inTransactionUnchecked {
                it.getUnreservedBoatSpaceOptions(
                    BoatSpaceFilter(
                        boatType,
                        width?.mToCm(),
                        length?.mToCm(),
                        amenities,
                        boatSpaceType,
                        harbor?.map { s -> s.toInt() }
                    )
                )
            }

        model.addAttribute("harbors", harbors.first)
        model.addAttribute("spaceCount", harbors.second)
        model.addAttribute("boat", BoatFilter(width, length, boatType))
        return "boat-space-search-results"
    }
}
