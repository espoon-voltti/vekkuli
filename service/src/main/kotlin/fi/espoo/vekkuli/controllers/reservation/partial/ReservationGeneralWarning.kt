package fi.espoo.vekkuli.controllers.reservation.partial

import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.config.ReservationWarningType
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.service.ReservationWarningRepository
import fi.espoo.vekkuli.views.citizen.details.reservation.ReservationGeneralWarningView
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
@RequestMapping("/virkailija/kayttaja/reservation/partial")
class ReservationGeneralWarning {
    @Autowired
    lateinit var reservationWarningView: ReservationGeneralWarningView

    @Autowired
    lateinit var warningsRepository: ReservationWarningRepository

    private val generalWarningsKey = ReservationWarningType.GeneralReservationWarning.name

    @GetMapping("/general-warning/{reservationId}")
    @ResponseBody
    fun generalWarningForReservationPartial(
        request: HttpServletRequest,
        @PathVariable reservationId: Int,
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser()
        val isEmployee = authenticatedUser?.isEmployee() == true

        if (!isEmployee) {
            throw UnauthorizedException()
        }

        val warnings = warningsRepository.getWarningsForReservation(reservationId)
        val hasGeneralWarning = warnings.any { it.key == generalWarningsKey }

        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                hasGeneralWarning
            )
        )
    }

    @PatchMapping("/general-warning/{reservationId}/toggle-warning/{hasGeneralWarning}")
    @ResponseBody
    fun toggleGeneralWarningForReservationPartial(
        request: HttpServletRequest,
        @PathVariable reservationId: Int,
        @PathVariable hasGeneralWarning: Boolean,
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser()
        val isEmployee = authenticatedUser?.isEmployee() == true

        if (!isEmployee) {
            throw UnauthorizedException()
        }

        if (hasGeneralWarning) {
            warningsRepository.addReservationWarnings(
                reservationId,
                null,
                null,
                listOf(generalWarningsKey)
            )
        } else {
            warningsRepository.deleteReservationWarningsForReservation(reservationId, generalWarningsKey)
        }

        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                hasGeneralWarning
            )
        )
    }
}
