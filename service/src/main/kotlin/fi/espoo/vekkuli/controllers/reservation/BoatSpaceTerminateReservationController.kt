package fi.espoo.vekkuli.controllers.reservation

import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.service.TerminateBoatSpaceReservationService
import fi.espoo.vekkuli.views.citizen.details.reservation.TerminationFailModal
import fi.espoo.vekkuli.views.citizen.details.reservation.TerminationSuccessModal
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class BoatSpaceTerminateReservationController(
    private var citizenUserController: CitizenUserController,
    private var terminateService: TerminateBoatSpaceReservationService,
    private var terminationSuccessModal: TerminationSuccessModal,
    private var terminationFailModal: TerminationFailModal
) {
    @PostMapping("/boat-space/terminate-reservation")
    @ResponseBody
    fun ackWarning(
        request: HttpServletRequest,
        @RequestParam("reservationId") reservationId: Int,
    ): ResponseEntity<String> {
        try {
            val currentCitizen = citizenUserController.getAuthenticatedCitizen(request)
            terminateService.terminateBoatSpaceReservation(reservationId, currentCitizen.id)
            return ResponseEntity.ok(
                terminationSuccessModal.render()
            )
        } catch (e: Exception) {
            return ResponseEntity.ok(
                terminationFailModal.render()
            )
        }
    }
}
