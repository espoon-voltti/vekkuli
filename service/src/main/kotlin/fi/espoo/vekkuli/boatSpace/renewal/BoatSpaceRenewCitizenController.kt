package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationResponse
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationResponseMapper
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/citizen")
class BoatSpaceRenewCitizenController(
    private val renewalService: BoatSpaceRenewalService,
    private val reservationResponseMapper: ReservationResponseMapper
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/reservation/{originalReservationId}/renew")
    fun startRenewReservation(
        @PathVariable originalReservationId: Int,
        request: HttpServletRequest,
    ): ReservationResponse {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "START_RENEW_RESERVATION",
            )
        }
        val reservation = renewalService.startReservation(originalReservationId)
        return reservationResponseMapper.toReservationResponse(reservation)
    }
}
