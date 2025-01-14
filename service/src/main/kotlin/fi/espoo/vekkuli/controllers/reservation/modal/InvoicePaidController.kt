package fi.espoo.vekkuli.controllers.reservation.modal

import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.ReserverService
import fi.espoo.vekkuli.views.citizen.details.reservation.ReservationStatusUpdateModal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*

@Controller
class InvoicePaidController {
    @Autowired
    private lateinit var reservationStatusUpdateModal: ReservationStatusUpdateModal

    @Autowired
    private lateinit var reservationService: BoatReservationService

    @Autowired
    private lateinit var reserverService: ReserverService

    @GetMapping("/reservation/modal/update-payment-status/{reservationId}/{reserverId}")
    @ResponseBody
    fun citizenProfile(
        @PathVariable reservationId: Int,
        @PathVariable reserverId: UUID,
    ): ResponseEntity<String> {
        val reservation =
            reservationService.getBoatSpaceReservation(reservationId)
                ?: throw IllegalArgumentException("Reservation not found")
        val reserver = reserverService.getReserverById(reserverId) ?: throw IllegalArgumentException("Reserver not found")

        return ResponseEntity.ok(
            reservationStatusUpdateModal.render(reserver.id, reservation)
        )
    }
}
