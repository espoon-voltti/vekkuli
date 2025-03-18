package fi.espoo.vekkuli.controllers.reservation.modal

import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.config.ensureEmployeeId
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.ReserverService
import fi.espoo.vekkuli.views.citizen.details.reservation.ReservationValidityUpdateModal
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*

@Controller
class UpdateReservationTypeModal {
    @Autowired
    private lateinit var reservationValidityUpdateModal: ReservationValidityUpdateModal

    @Autowired
    private lateinit var reservationService: BoatReservationService

    @Autowired
    private lateinit var reserverService: ReserverService

    @Autowired
    private lateinit var seasonalService: SeasonalService

    @GetMapping("/reservation/modal/update-type/{reservationId}/{reserverId}")
    @ResponseBody
    fun updatePaymentStatusModal(
        @PathVariable reservationId: Int,
        @PathVariable reserverId: UUID,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.ensureEmployeeId()

        val reservation =
            reservationService.getBoatSpaceReservation(reservationId)
                ?: throw IllegalArgumentException("Reservation not found")
        val reserver = reserverService.getReserverById(reserverId) ?: throw IllegalArgumentException("Reserver not found")
        val fixedTermEndDate = seasonalService.getBoatSpaceReservationEndDateForNew(reservation.type, ReservationValidity.FixedTerm)

        return ResponseEntity.ok(
            reservationValidityUpdateModal.render(reserver.id, reservation, fixedTermEndDate)
        )
    }
}
