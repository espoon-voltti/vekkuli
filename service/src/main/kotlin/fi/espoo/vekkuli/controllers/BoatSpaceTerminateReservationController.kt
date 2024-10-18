package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.views.employee.BoatSpaceReservationList
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

    @Autowired lateinit var reservationService: BoatReservationService

    @Autowired lateinit var boatSpaceReservationList: BoatSpaceReservationList

    @Autowired lateinit var layout: EmployeeLayout

    @PostMapping("/boat-space/terminate-reservation")
    fun ackWarning(
        request: HttpServletRequest,
        @RequestParam("reservationId") reservationId: Int,
    ): ResponseEntity<Void> {
        val currentCitizen = citizenUserController.getAuthenticatedCitizen(request)
        reservationService.terminateBoatSpaceReservation(reservationId, currentCitizen)

        return ResponseEntity.noContent().build()
    }
}
/*


*/
