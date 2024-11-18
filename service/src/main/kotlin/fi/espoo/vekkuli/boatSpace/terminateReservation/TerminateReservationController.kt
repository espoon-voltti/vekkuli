package fi.espoo.vekkuli.boatSpace.terminateReservation

import fi.espoo.vekkuli.boatSpace.terminateReservation.modal.TerminationFailModalView
import fi.espoo.vekkuli.boatSpace.terminateReservation.modal.TerminationSuccessModalView
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.utils.TimeProvider
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Controller
@RequestMapping("/boat-space/terminate-reservation")
class TerminateReservationController(
    private val terminateService: TerminateBoatSpaceReservationService,
    private val terminationSuccessModalView: TerminationSuccessModalView,
    private val terminationFailModalView: TerminationFailModalView,
    private val timeProvider: TimeProvider
) {
    @PostMapping
    @ResponseBody
    fun terminateReservation(
        request: HttpServletRequest,
        @RequestParam("reservationId") reservationId: Int,
    ): ResponseEntity<String> {
        try {
            val user = request.getAuthenticatedUser() ?: throw Unauthorized()
            terminateService.terminateBoatSpaceReservationAsOwner(reservationId, user.id)
            return ResponseEntity.ok(
                terminationSuccessModalView.render()
            )
        } catch (e: Exception) {
            return ResponseEntity.ok(
                terminationFailModalView.render()
            )
        }
    }

    @PostMapping("/as-employee")
    @ResponseBody
    fun terminateReservationAsEmployee(
        request: HttpServletRequest,
        @RequestParam("reservationId") reservationId: Int,
        @RequestParam("terminationReason") reason: ReservationTerminationReason,
        @RequestParam("endDate") endDate: LocalDate?,
        @RequestParam("explanation") explanation: String?
    ): ResponseEntity<String> {
        try {
            val user = request.getAuthenticatedUser() ?: throw Unauthorized()

            terminateService.terminateBoatSpaceReservationAsEmployee(
                reservationId,
                user.id,
                reason,
                endDate ?: timeProvider.getCurrentDate(),
                explanation
            )

            return ResponseEntity.ok(
                terminationSuccessModalView.render()
            )
        } catch (e: Exception) {
            return ResponseEntity.ok(
                terminationFailModalView.render()
            )
        }
    }
}
