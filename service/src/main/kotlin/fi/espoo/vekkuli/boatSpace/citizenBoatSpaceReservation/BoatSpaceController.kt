package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/citizen")
class BoatSpaceController(
    private val boatSpaceResponseMapper: BoatSpaceResponseMapper
) {
    @GetMapping("/boat-space/{spaceId}")
    fun getBoatSpace(@PathVariable spaceId: Int): BoatSpaceResponse {
        return boatSpaceResponseMapper.toBoatSpaceResponse(spaceId)
    }
}
