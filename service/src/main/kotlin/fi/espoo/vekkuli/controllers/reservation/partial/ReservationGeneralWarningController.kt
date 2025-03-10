package fi.espoo.vekkuli.controllers.reservation.partial

import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.config.AuthenticatedUser
import fi.espoo.vekkuli.config.ReservationWarningType
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.ReservationWarning
import fi.espoo.vekkuli.service.MemoService
import fi.espoo.vekkuli.service.ReservationWarningRepository
import fi.espoo.vekkuli.views.citizen.details.reservation.ReservationGeneralWarningView
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Controller
@RequestMapping("/virkailija/kayttaja/reservation/partial")
class ReservationGeneralWarningController {
    private val logger = KotlinLogging.logger {}

    @Autowired
    private lateinit var memoService: MemoService

    @Autowired
    lateinit var reservationWarningView: ReservationGeneralWarningView

    @Autowired
    lateinit var warningsRepository: ReservationWarningRepository

    private val generalWarningsKey = ReservationWarningType.GeneralReservationWarning.name

    @GetMapping("/general-warning/{reserverId}/{reservationId}")
    @ResponseBody
    fun generalWarningForReservationPartial(
        request: HttpServletRequest,
        @PathVariable reserverId: UUID,
        @PathVariable reservationId: Int,
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser()
        val isEmployee = authenticatedUser?.isEmployee() == true

        if (!isEmployee) {
            throw UnauthorizedException()
        }

        val warnings = warningsRepository.getWarningsForReservation(reservationId)
        val generalWarning = warnings.find { it.key == generalWarningsKey }

        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                reserverId,
                generalWarning
            )
        )
    }

    @PostMapping("/general-warning/{reserverId}/{reservationId}")
    @ResponseBody
    fun saveGeneralWarningForReservationPartial(
        request: HttpServletRequest,
        @PathVariable reserverId: UUID,
        @PathVariable reservationId: Int,
        @RequestParam("infoText") infoText: String,
    ): ResponseEntity<String> {
        logAndGetUser(request, reserverId, reservationId, "CITIZEN_PROFILE_SET_GENERAL_WARNING")

        warningsRepository.addReservationWarnings(
            UUID.randomUUID(),
            reservationId,
            null,
            null,
            null,
            infoText.trim(),
            listOf(generalWarningsKey)
        )

        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                reserverId,
                ReservationWarning(
                    UUID.randomUUID(),
                    reservationId,
                    key = generalWarningsKey,
                    infoText = infoText.trim(),
                    boatId = null,
                    invoiceNumber = null,
                    trailerId = null
                )
            )
        )
    }

    @PatchMapping("/general-warning/{reserverId}/{reservationId}")
    @ResponseBody
    fun updateGeneralWarningForReservationPartial(
        request: HttpServletRequest,
        @PathVariable reserverId: UUID,
        @PathVariable reservationId: Int,
        @RequestParam("infoText") infoText: String,
    ): ResponseEntity<String> {
        logAndGetUser(request, reserverId, reservationId, "CITIZEN_PROFILE_UPDATE_GENERAL_WARNING")

        warningsRepository.deleteReservationWarningsForReservation(reservationId, generalWarningsKey)
        warningsRepository.addReservationWarnings(
            UUID.randomUUID(),
            reservationId,
            null,
            null,
            null,
            infoText.trim(),
            listOf(generalWarningsKey)
        )

        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                reserverId,
                ReservationWarning(
                    UUID.randomUUID(),
                    reservationId,
                    key = generalWarningsKey,
                    infoText = infoText.trim(),
                    boatId = null,
                    trailerId = null,
                    invoiceNumber = null
                )
            )
        )
    }

    @PostMapping("/general-warning/{reserverId}/{reservationId}/acknowledge")
    @ResponseBody
    fun acknowledgeGeneralWarningForReservationPartial(
        request: HttpServletRequest,
        @PathVariable reserverId: UUID,
        @PathVariable reservationId: Int,
        @RequestParam("infoText") infoText: String,
    ): ResponseEntity<String> {
        val authenticatedUser = logAndGetUser(request, reserverId, reservationId, "CITIZEN_PROFILE_ACKNOWLEDGE_GENERAL_WARNING")
        val userId = authenticatedUser?.id

        warningsRepository.deleteReservationWarningsForReservation(reservationId, generalWarningsKey)
        if (infoText.isNotEmpty()) {
            memoService.insertMemo(reserverId, userId, infoText.trim())
        }
        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                reserverId,
                null
            )
        )
    }

    private fun logAndGetUser(
        request: HttpServletRequest,
        reserverId: UUID,
        reservationId: Int,
        eventCode: String
    ): AuthenticatedUser? {
        val authenticatedUser = request.getAuthenticatedUser()
        authenticatedUser?.let {
            logger.audit(
                it,
                eventCode,
                mapOf(
                    "reserverId" to reserverId.toString(),
                    "reservationId" to reservationId.toString()
                )
            )
        }
        val isEmployee = authenticatedUser?.isEmployee() == true

        if (!isEmployee) {
            throw UnauthorizedException()
        }
        return authenticatedUser
    }
}
