package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/citizen")
class BoatSpaceSwitchController(
    private val reservationService: ReservationService,
    private val reservationResponseMapper: ReservationResponseMapper,
) {
    @PostMapping("/reservation/{originalReservationId}/switch/{spaceId}")
    fun postStartSwitch(
        @PathVariable originalReservationId: Int,
        @PathVariable spaceId: Int,
    ) {
        val originalReservation = reservationService.getReservationDetails(originalReservationId)
        val reservation = reservationService.startSwitchReservation(spaceId, originalReservationId)
    }

    @PostMapping("/reservation/{reservationId}/switch")
    fun postSwitchReservation(
        @PathVariable reservationId: Int,
        @RequestBody input: FillReservationInformationInput,
    ) {
        val information = input.toReservationInformation()
        reservationService.switchReservation(reservationId, input)
    }
}
