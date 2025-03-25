package fi.espoo.vekkuli.controllers.reservation.modal

import fi.espoo.vekkuli.config.ensureEmployeeId
import fi.espoo.vekkuli.service.ReserverService
import fi.espoo.vekkuli.views.employee.modals.AddBoatToReserverModal
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.UUID

@Controller
@RequestMapping("/virkailija/venepaikat/varaukset/uusi-vene")
class AddBoatController(
    private var reserverService: ReserverService,
    private var addBoatModal: AddBoatToReserverModal
) {
    @GetMapping("/{reserverId}")
    @ResponseBody
    fun updatePaymentStatusModal(
        @PathVariable reserverId: UUID,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.ensureEmployeeId()

        val reserver =
            reserverService.getReserverById(reserverId)
                ?: throw IllegalArgumentException("Reservation not found")

        return ResponseEntity.ok(
            addBoatModal.render(reserver)
        )
    }
}
