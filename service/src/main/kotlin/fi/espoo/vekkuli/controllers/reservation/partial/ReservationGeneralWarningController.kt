package fi.espoo.vekkuli.controllers.reservation.partial

import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.config.ReservationWarningType
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.ReservationWarning
import fi.espoo.vekkuli.service.MemoService
import fi.espoo.vekkuli.service.ReservationWarningRepository
import fi.espoo.vekkuli.views.citizen.details.reservation.ReservationGeneralWarningView
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Controller
@RequestMapping("/virkailija/kayttaja/reservation/partial")
class ReservationGeneralWarningController {
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
        val authenticatedUser = request.getAuthenticatedUser()
        val isEmployee = authenticatedUser?.isEmployee() == true

        if (!isEmployee) {
            throw UnauthorizedException()
        }
        warningsRepository.addReservationWarnings(
            reservationId,
            null,
            null,
            infoText,
            listOf(generalWarningsKey)
        )

        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                reserverId,
                ReservationWarning(
                    reservationId,
                    key = generalWarningsKey,
                    infoText = infoText,
                    boatId = null,
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
        val authenticatedUser = request.getAuthenticatedUser()
        val isEmployee = authenticatedUser?.isEmployee() == true

        if (!isEmployee) {
            throw UnauthorizedException()
        }

        warningsRepository.deleteReservationWarningsForReservation(reservationId, generalWarningsKey)
        warningsRepository.addReservationWarnings(
            reservationId,
            null,
            null,
            infoText,
            listOf(generalWarningsKey)
        )

        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                reserverId,
                ReservationWarning(
                    reservationId,
                    key = generalWarningsKey,
                    infoText = infoText,
                    boatId = null,
                    trailerId = null
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
        val authenticatedUser = request.getAuthenticatedUser()
        val isEmployee = authenticatedUser?.isEmployee() == true

        if (!isEmployee) {
            throw UnauthorizedException()
        }
        val userId = authenticatedUser?.id
        warningsRepository.deleteReservationWarningsForReservation(reservationId, generalWarningsKey)
        if (infoText.isNotEmpty()) {
            memoService.insertMemo(reserverId, userId, infoText)
        }
        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                reserverId,
                null
            )
        )
    }
}
