package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatSpaceFilter
import fi.espoo.vekkuli.service.BoatSpaceService
import fi.espoo.vekkuli.service.CitizenService
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

    @Autowired
    lateinit var boatSpaceService: BoatSpaceService

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var citizenService: CitizenService

    @RequestMapping("/venepaikat")
    fun boatSpaceSearchPage(
        request: HttpServletRequest,
        model: Model
    ): String {
        val citizen = getCitizen(request, citizenService)
        if (citizen != null) {
            val reservation =
                reservationService.getReservationForCitizen(citizen.id)

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
        model: Model,
        request: HttpServletRequest
    ): String {
        val harbors =
            boatSpaceService.getUnreservedBoatSpaceOptions(
                BoatSpaceFilter(
                    boatType,
                    width?.mToCm(),
                    length?.mToCm(),
                    amenities,
                    boatSpaceType,
                    harbor?.map { s -> s.toInt() }
                )
            )

        model.addAttribute("harbors", harbors.first)
        model.addAttribute("spaceCount", harbors.second)
        model.addAttribute("boat", BoatFilter(width, length, boatType))
        model.addAttribute("isAuthenticated", request.getAuthenticatedUser() != null)
        return "boat-space-search-results"
    }
}
