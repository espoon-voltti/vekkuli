package fi.espoo.vekkuli.controllers.reservation.modal

import fi.espoo.vekkuli.config.ensureEmployeeId
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.views.employee.modals.ChangeReservationBoatModal
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/virkailija/venepaikat/varaukset/vaihda-vene")
class ChangeBoatController(
    private var reservationService: BoatReservationService,
    private var boatService: BoatService,
    private var changeReservationBoatModal: ChangeReservationBoatModal
) {
    @GetMapping("/{reservationId}")
    @ResponseBody
    fun updatePaymentStatusModal(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.ensureEmployeeId()

        val reservation =
            reservationService.getBoatSpaceReservation(reservationId)
                ?: throw IllegalArgumentException("Reservation not found")
        val reserverBoats = boatService.getBoatsForReserver(reservation.reserverId)

        return ResponseEntity.ok(
            changeReservationBoatModal.render(reservation, reserverBoats)
        )
    }
}
