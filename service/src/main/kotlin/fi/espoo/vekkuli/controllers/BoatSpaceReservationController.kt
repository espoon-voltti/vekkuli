package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.common.getAppUser
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceReservationFilter
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.views.employee.BoatSpaceReservationList
import fi.espoo.vekkuli.views.employee.EmployeeLayout
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
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var boatSpaceReservationList: BoatSpaceReservationList

    @Autowired
    lateinit var layout: EmployeeLayout

    @GetMapping("/varaukset")
    @ResponseBody
    fun reservationSearchPage(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter,
        model: Model
    ): ResponseEntity<String> {
        val reservations =
            reservationService.getBoatSpaceReservations(params)

        val harbors =
            reservationService.getHarbors()

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

        return ResponseEntity.ok(
            layout.render(
                true,
                employee.fullName,
                boatSpaceReservationList.render(harbors, BoatSpaceAmenity.entries.toList(), reservations, params, UserType.EMPLOYEE)
            )
        )
    }

    @PostMapping("/varaukset/kuittaa-varoitus")
    fun ackWarning(
        @RequestParam("reservationId") reservationId: Int,
        @RequestParam("boatId") boatId: Int,
        @RequestParam("key") key: String
    ): ResponseEntity<Void> {
        reservationService.acknowledgeWarning(reservationId, boatId, key)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/varaukset/luo")
    fun reservationCreatePage(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter,
        model: Model
    ): String = "boat-space-reservation-create"
}
