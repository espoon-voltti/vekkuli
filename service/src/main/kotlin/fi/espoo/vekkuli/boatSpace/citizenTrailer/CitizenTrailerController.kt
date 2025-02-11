package fi.espoo.vekkuli.boatSpace.citizenTrailer

import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/citizen/trailers")
class CitizenTrailerController(
    private val citizenTrailerService: CitizenTrailerService,
) {
    private val logger = KotlinLogging.logger {}

    @PatchMapping("/{trailerId}")
    fun patchTrailer(
        @PathVariable trailerId: Int,
        @RequestBody input: UpdateTrailerInput,
        request: HttpServletRequest,
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "UPDATE_TRAILER",
                mapOf(
                    "targetId" to trailerId.toString()
                ),
            )
        }

        citizenTrailerService.updateTrailer(trailerId, input)
    }
}
