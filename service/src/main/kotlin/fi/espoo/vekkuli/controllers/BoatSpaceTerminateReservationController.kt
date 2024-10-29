package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.service.TerminateBoatSpaceReservationService
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class BoatSpaceTerminateReservationController {
    @Autowired lateinit var jdbi: Jdbi

    @Autowired lateinit var citizenUserController: CitizenUserController

    @Autowired lateinit var terminateService: TerminateBoatSpaceReservationService

    @Autowired lateinit var layout: EmployeeLayout

    @PostMapping("/boat-space/terminate-reservation")
    fun ackWarning(
        request: HttpServletRequest,
        @RequestParam("reservationId") reservationId: Int,
    ): ResponseEntity<Void> {
        val currentCitizen = citizenUserController.getAuthenticatedCitizen(request)
        terminateService.terminateBoatSpaceReservation(reservationId, currentCitizen)

        return ResponseEntity.noContent().build()
    }
}
/*


*/
