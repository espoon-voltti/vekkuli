package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.common.getAppUser
import fi.espoo.vekkuli.config.BoatSpaceConfig.winterStorageLocations
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Routes.Companion.USERTYPE
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.controllers.Utils.Companion.isAuthenticated
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatSpaceService
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.views.citizen.BoatSpaceSearch
import fi.espoo.vekkuli.views.citizen.Layout
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.Min
import mu.KotlinLogging
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.math.BigDecimal
import java.net.URI

data class BoatFilter(
    val width: BigDecimal?,
    val length: BigDecimal?,
    val type: BoatType?
)

@Controller
class BoatSpaceSearchController {
    @Autowired
    private lateinit var employeeLayout: EmployeeLayout

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

    private val logger = KotlinLogging.logger {}

    @RequestMapping("/$USERTYPE/venepaikat")
    @ResponseBody
    fun boatSpaceSearchPage(
        request: HttpServletRequest,
        @PathVariable usertype: String
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "BOAT_SPACE_SEARCH")
        }
        val userType = UserType.fromPath(usertype)
        if (userType == UserType.EMPLOYEE) {
            val authenticatedUser = request.getAuthenticatedUser() ?: return ResponseEntity(HttpStatus.FORBIDDEN)
            val user =
                authenticatedUser.let {
                    jdbi.inTransactionUnchecked { tx ->
                        tx.getAppUser(authenticatedUser.id)
                    }
                }
            if (user == null) {
                return ResponseEntity(HttpStatus.FORBIDDEN)
            }
            val reservation = reservationService.getUnfinishedReservationForEmployee(user.id)
            if (reservation != null) {
                val headers = org.springframework.http.HttpHeaders()
                headers.location = URI(getServiceUrl("/${userType.path}/venepaikka/varaus/${reservation.id}"))
                return ResponseEntity(headers, HttpStatus.FOUND)
            }
            val locations = reservationService.getHarbors()

            return ResponseEntity.ok(
                employeeLayout.render(
                    true,
                    request.requestURI,
                    boatSpaceSearch.render(locations, winterStorageLocations, true)
                )
            )
        }
        val citizen = getCitizen(request, citizenService)
        if (citizen != null) {
            val reservation =
                reservationService.getUnfinishedReservationForCitizen(citizen.id)

            if (reservation != null) {
                val headers = org.springframework.http.HttpHeaders()
                headers.location = URI(getServiceUrl("/${userType.path}/venepaikka/varaus/${reservation.id}"))
                return ResponseEntity(headers, HttpStatus.FOUND)
            }
        }
        val locations = reservationService.getHarbors()

        return ResponseEntity.ok(
            layout.render(
                isAuthenticated(userType, request),
                citizen?.fullName,
                request.requestURI,
                boatSpaceSearch.render(locations, winterStorageLocations)
            )
        )
    }

    @RequestMapping("/$USERTYPE/partial/vapaat-paikat")
    @ResponseBody
    fun searchResultPartial(
        @PathVariable usertype: String,
        @RequestParam(required = false) boatType: BoatType?,
        @RequestParam @Min(0) width: BigDecimal?,
        @RequestParam @Min(0) length: BigDecimal?,
        @RequestParam amenities: List<BoatSpaceAmenity>?,
        @RequestParam storageType: BoatSpaceAmenity?,
        @RequestParam boatSpaceType: BoatSpaceType?,
        @RequestParam harbor: List<String>?,
        request: HttpServletRequest
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "BOAT_SPACE_SEARCH_RESULTS")
        }
        val userType = UserType.fromPath(usertype)

        val harbors =
            boatSpaceService.getUnreservedBoatSpaceOptions(
                boatType,
                width,
                length,
                amenities,
                storageType,
                boatSpaceType,
                harbor,
            )

        return boatSpaceSearch.renderResults(
            harbors.first,
            BoatFilter(width, length, boatType),
            harbors.second,
            isAuthenticated(userType, request),
            userType == UserType.EMPLOYEE
        )
    }
}
