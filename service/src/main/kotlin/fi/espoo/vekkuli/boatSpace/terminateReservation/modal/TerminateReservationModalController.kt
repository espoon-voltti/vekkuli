package fi.espoo.vekkuli.boatSpace.terminateReservation.modal

import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.service.BoatReservationService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/boat-space/terminate-reservation/modal")
class TerminateReservationModalController {
    @Autowired
    private lateinit var terminateReservationModalView: TerminateReservationModalView

    @Autowired
    private lateinit var reservationService: BoatReservationService

    @GetMapping("/terminate-reservation/{reservationId}")
    @ResponseBody
    fun citizenProfile(
        @PathVariable reservationId: Int,
    ): ResponseEntity<String> {
        val reservation =
            reservationService.getBoatSpaceReservation(reservationId)
                ?: throw IllegalArgumentException("Reservation not found")

        return ResponseEntity.ok(
            terminateReservationModalView.render(reservation)
        )
    }

    @GetMapping("/terminate-reservation-for-other-user/{reservationId}")
    @ResponseBody
    fun citizenProfile(
        request: HttpServletRequest,
        @PathVariable reservationId: Int,
    ): ResponseEntity<String> {
        if (request.getAuthenticatedUser()?.isEmployee() != true) {
            throw Unauthorized()
        }

        val reservation =
            reservationService.getBoatSpaceReservation(reservationId)
                ?: throw IllegalArgumentException("Reservation not found")

        return ResponseEntity.ok(
            terminateReservationModalView.render(reservation)
        )
    }
}
