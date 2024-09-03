package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.getLocations
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatSpaceFilter
import fi.espoo.vekkuli.service.BoatSpaceService
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.utils.mToCm
import fi.espoo.vekkuli.views.citizen.BoatSpaceSearch
import fi.espoo.vekkuli.views.citizen.Layout
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.Min
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URI

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

    @Autowired
    lateinit var boatSpaceSearch: BoatSpaceSearch

    @Autowired
    lateinit var layout: Layout

    @RequestMapping("/venepaikat")
    @ResponseBody
    fun boatSpaceSearchPage(request: HttpServletRequest): ResponseEntity<String> {
        val citizen = getCitizen(request, citizenService)
        if (citizen != null) {
            val reservation =
                reservationService.getReservationForCitizen(citizen.id)

            if (reservation != null) {
                val headers = org.springframework.http.HttpHeaders()
                headers.location = URI("/kuntalainen/venepaikka/varaus/${reservation.id}")
                return ResponseEntity(headers, HttpStatus.FOUND)
            }
        }
        val locations =
            jdbi.inTransactionUnchecked { tx ->
                tx.getLocations()
            }
        return ResponseEntity.ok(
            layout.generateLayout(
                request.getAuthenticatedUser() != null,
                citizen?.fullName,
                boatSpaceSearch.render(locations)
            )
        )
    }

    @RequestMapping("/partial/vapaat-paikat")
    @ResponseBody
    fun searchResultPartial(
        @RequestParam(required = false) boatType: BoatType?,
        @RequestParam @Min(0) width: Double?,
        @RequestParam @Min(0) length: Double?,
        @RequestParam amenities: List<BoatSpaceAmenity>?,
        @RequestParam boatSpaceType: BoatSpaceType?,
        @RequestParam harbor: List<String>?,
        request: HttpServletRequest
    ): String {
        val params =
            BoatSpaceFilter(
                boatType,
                width?.mToCm(),
                length?.mToCm(),
                amenities,
                boatSpaceType,
                harbor?.map { s -> s.toInt() }
            )
        val harbors =
            boatSpaceService.getUnreservedBoatSpaceOptions(
                params
            )

        return boatSpaceSearch.renderResults(
            harbors.first,
            BoatFilter(width, length, boatType),
            harbors.second,
            request.getAuthenticatedUser() != null
        )
    }
}
