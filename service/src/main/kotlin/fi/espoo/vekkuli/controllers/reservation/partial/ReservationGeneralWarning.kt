package fi.espoo.vekkuli.controllers.reservation.partial

import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.config.ReservationWarningType
import fi.espoo.vekkuli.config.getAuthenticatedUser
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
class ReservationGeneralWarning {
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
        val hasGeneralWarning = warnings.any { it.key == generalWarningsKey }

        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                reserverId,
                hasGeneralWarning
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
            listOf(generalWarningsKey)
        )

        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                reserverId,
                true
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
        /*
                warningsRepository.addReservationWarnings(
                    reservationId,
                    null,
                    null,
                    listOf(generalWarningsKey)
                )
         */
        // todo update text
        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                reserverId,
                true
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
        // todo: add note
        return ResponseEntity.ok(
            reservationWarningView.renderContent(
                reservationId,
                reserverId,
                false
            )
        )
    }
}
