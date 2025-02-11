package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationService
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/citizen")
class BoatSpaceSwitchController(
    private val switchService: BoatSpaceSwitchService,
    private val reservationService: ReservationService,
    private val switchSourceResponseAssembler: SwitchSourceResponseAssembler
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/reservation/{originalReservationId}/switch/{spaceId}")
    fun postStartSwitch(
        @PathVariable originalReservationId: Int,
        @PathVariable spaceId: Int,
        request: HttpServletRequest
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "START_SWITCH_RESERVATION",
                mapOf("targetId" to spaceId.toString(), "originalReservationId" to originalReservationId.toString())
            )
        }
        switchService.startReservation(spaceId, originalReservationId)
    }

    @GetMapping("/reservation/{originalReservationId}/switch-source")
    fun getReservationSwitchSource(
        @PathVariable originalReservationId: Int,
        request: HttpServletRequest
    ): SwitchSourceResponse {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_RESERVATION_SWITCH_SOURCE",
                mapOf("targetId" to originalReservationId.toString())
            )
        }
        val reservation = reservationService.getReservation(originalReservationId)
        return switchSourceResponseAssembler.toSwitchSourceResponse(reservation)
    }
}
