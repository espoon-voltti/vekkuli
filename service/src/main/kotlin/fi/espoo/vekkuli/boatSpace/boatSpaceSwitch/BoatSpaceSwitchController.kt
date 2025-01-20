package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/citizen")
class BoatSpaceSwitchController(
    private val switchService: BoatSpaceSwitchService,
) {
    @PostMapping("/reservation/{originalReservationId}/switch/{spaceId}")
    fun postStartSwitch(
        @PathVariable originalReservationId: Int,
        @PathVariable spaceId: Int,
    ) {
        switchService.startReservation(spaceId, originalReservationId)
    }
}
