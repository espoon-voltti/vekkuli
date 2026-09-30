package fi.espoo.vekkuli.boatSpace.reservationEndDate

import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.ensureEmployeeId
import fi.espoo.vekkuli.config.getAuthenticatedEmployee
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDate
import java.util.UUID

@Controller
class ReservationEndDateController(
    private val reservationEndDateService: ReservationEndDateService,
    private val modalView: ReservationEndDateModalView,
    private val validationView: ReservationEndDateValidationView,
    private val successModalView: ReservationEndDateSuccessModalView
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/reservation/modal/update-end-date/{reservationId}/{reserverId}")
    @ResponseBody
    fun updateEndDateModal(
        @PathVariable reservationId: Int,
        @PathVariable reserverId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        request.ensureEmployeeId()

        val reservation = reservationEndDateService.getEditableReservation(reservationId)
        val validation = reservationEndDateService.validate(reservation, reservation.endDate)

        return ResponseEntity.ok(modalView.render(reserverId, reservation, reservation.endDate, validation))
    }

    @PostMapping("/virkailija/venepaikat/varaukset/loppupaiva/tarkista")
    @ResponseBody
    fun checkEndDate(
        @RequestParam reservationId: Int,
        @RequestParam(required = false) endDate: LocalDate?,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        request.ensureEmployeeId()

        val reservation = reservationEndDateService.getEditableReservation(reservationId)
        val validation =
            endDate?.let { reservationEndDateService.validate(reservation, it) }
                ?: EndDateValidationResult(EndDateError.Missing, emptyList())

        return ResponseEntity.ok(validationView.render(validation, END_DATE_FORM_ID))
    }

    @PostMapping("/virkailija/venepaikat/varaukset/loppupaiva")
    @ResponseBody
    fun updateEndDate(
        @RequestParam reservationId: Int,
        @RequestParam reserverId: UUID,
        @RequestParam(required = false) endDate: LocalDate?,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val employee = request.getAuthenticatedEmployee()
        logger.audit(
            employee,
            "RESERVATION_UPDATE_END_DATE",
            mapOf(
                "targetId" to reservationId.toString(),
                "endDate" to endDate.toString(),
                "reserverId" to reserverId.toString()
            )
        )

        val reservation = reservationEndDateService.getEditableReservation(reservationId)
        val validation =
            endDate?.let { reservationEndDateService.validate(reservation, it) }
                ?: EndDateValidationResult(EndDateError.Missing, emptyList())
        if (endDate == null || !validation.isValid) {
            return ResponseEntity.ok(modalView.render(reserverId, reservation, endDate ?: reservation.endDate, validation))
        }

        reservationEndDateService.updateEndDate(reservationId, endDate, employee.id)

        return ResponseEntity.ok(successModalView.render())
    }
}
