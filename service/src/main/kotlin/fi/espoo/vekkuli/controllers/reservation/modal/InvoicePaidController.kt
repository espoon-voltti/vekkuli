package fi.espoo.vekkuli.controllers.reservation.modal

import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.views.citizen.details.reservation.InvoicePaidModal
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
    private lateinit var invoicePaidModal: InvoicePaidModal

    @Autowired
    private lateinit var reservationService: BoatReservationService

    @Autowired
    private lateinit var citizenService: CitizenService

    @GetMapping("/reservation/modal/mark-invoice-paid/{reservationId}/{citizenId}")
    @ResponseBody
    fun citizenProfile(
        @PathVariable reservationId: Int,
        @PathVariable citizenId: UUID,
    ): ResponseEntity<String> {
        val reservation =
            reservationService.getBoatSpaceReservation(reservationId)
                ?: throw IllegalArgumentException("Reservation not found")
        val citizen = citizenService.getCitizen(citizenId) ?: throw IllegalArgumentException("Reservation not found")

        return ResponseEntity.ok(
            invoicePaidModal.render(citizen, reservation)
        )
    }
}
