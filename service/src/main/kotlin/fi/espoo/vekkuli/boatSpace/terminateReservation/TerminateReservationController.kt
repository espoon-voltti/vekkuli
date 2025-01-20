package fi.espoo.vekkuli.boatSpace.terminateReservation

import fi.espoo.vekkuli.boatSpace.terminateReservation.modal.TerminationFailModalView
import fi.espoo.vekkuli.boatSpace.terminateReservation.modal.TerminationSuccessModalView
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.utils.TimeProvider
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Controller
@RequestMapping("/boat-space/terminate-reservation")
class TerminateReservationController(
    private val terminateService: TerminateReservationService,
    private val terminationSuccessModalView: TerminationSuccessModalView,
    private val terminationFailModalView: TerminationFailModalView,
    private val timeProvider: TimeProvider
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping
    @ResponseBody
    fun terminateReservation(
        request: HttpServletRequest,
        @RequestParam("reservationId") reservationId: Int,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "TERMINATE_RESERVATION",
                mapOf(
                    "targetId" to reservationId.toString()
                )
            )
        }
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
        @RequestParam("explanation") explanation: String?,
        @RequestParam("messageTitle") messageTitle: String,
        @RequestParam("messageContent") messageContent: String,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "TERMINATE_RESERVATION_AS_EMPLOYEE",
                mapOf(
                    "targetId" to reservationId.toString(),
                )
            )
        }
        try {
            val user = request.getAuthenticatedUser() ?: throw Unauthorized()

            terminateService.terminateBoatSpaceReservationAsEmployee(
                reservationId,
                user.id,
                reason,
                endDate ?: timeProvider.getCurrentDate(),
                explanation,
                messageTitle,
                messageContent
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
