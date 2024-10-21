package fi.espoo.vekkuli.controllers.reservation.modal

import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.views.citizen.details.reservation.TerminateReservationModal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class TerminateReservationController {
    @Autowired
    private lateinit var terminateReservationModal: TerminateReservationModal

    @Autowired
    private lateinit var reservationService: BoatReservationService

    @GetMapping("/reservation/modal/terminate-reservation/{reservationId}")
    @ResponseBody
    fun citizenProfile(
        @PathVariable reservationId: Int,
    ): ResponseEntity<String> {
        val reservation =
            reservationService.getBoatSpaceReservation(reservationId)
                ?: throw IllegalArgumentException("Reservation not found")

        return ResponseEntity.ok(
            terminateReservationModal.render(reservation)
        )
    }
}
