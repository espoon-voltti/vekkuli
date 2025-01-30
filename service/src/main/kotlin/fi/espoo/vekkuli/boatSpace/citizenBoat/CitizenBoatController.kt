package fi.espoo.vekkuli.boatSpace.citizenBoat

import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/citizen/boat")
class CitizenBoatController(
    private val citizenBoatService: CitizenBoatService,
) {
    private val logger = KotlinLogging.logger {}

    @PatchMapping("/{boatId}")
    fun patchBoat(
        @PathVariable boatId: Int,
        @RequestBody input: UpdateBoatInput,
        request: HttpServletRequest,
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "UPDATE_BOAT",
                mapOf(
                    "targetId" to boatId.toString()
                ),
            )
        }

        citizenBoatService.updateBoat(boatId, input)
    }
}
