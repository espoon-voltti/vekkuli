package fi.espoo.vekkuli.boatSpace.citizenBoat

import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/citizen/boats")
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

    @DeleteMapping("/{boatId}")
    fun deleteBoatById(
        @PathVariable boatId: Int,
        request: HttpServletRequest
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "DELETE_BOAT",
                mapOf(
                    "targetId" to boatId.toString()
                ),
            )
        }

        citizenBoatService.deleteBoat(boatId)
    }
}
