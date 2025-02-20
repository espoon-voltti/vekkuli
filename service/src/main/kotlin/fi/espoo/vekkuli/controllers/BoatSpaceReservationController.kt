package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.common.getAppUser
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.domain.BoatSpaceReservationFilter
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.actualAmenities
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatSpaceService
import fi.espoo.vekkuli.views.employee.BoatSpaceReservationList
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.net.URI

@Controller
@RequestMapping("/virkailija/venepaikat")
class BoatSpaceReservationController {
    @Autowired
    private lateinit var boatSpaceService: BoatSpaceService

    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var boatSpaceReservationList: BoatSpaceReservationList

    @Autowired
    lateinit var layout: EmployeeLayout

    private val logger = KotlinLogging.logger {}

    @GetMapping("/varaukset")
    @ResponseBody
    fun reservationSearchPage(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter,
        model: Model
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "EMPLOYEE_RESERVATION_SEARCH")
        }
        val reservations =
            reservationService.getBoatSpaceReservations(params)

        val harbors =
            reservationService.getHarbors()

        val boatSpaceTypes = BoatSpaceType.entries.toList()

        val authenticatedUser = request.getAuthenticatedUser()
        if (authenticatedUser == null) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/virkailija"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        }

        val employee =
            authenticatedUser.let {
                jdbi.inTransactionUnchecked { tx ->
                    tx.getAppUser(authenticatedUser.id)
                }
            }
        if (employee == null) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/virkailija"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        }

        val sections = boatSpaceService.getSections()

        return ResponseEntity.ok(
            layout.render(
                true,
                request.requestURI,
                boatSpaceReservationList.render(harbors, boatSpaceTypes, actualAmenities, reservations, params, sections, UserType.EMPLOYEE)
            )
        )
    }
}
