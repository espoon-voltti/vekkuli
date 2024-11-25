package fi.espoo.vekkuli.boatSpace.terminateReservation.modal

import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.TemplateEmailService
import org.springframework.stereotype.Service

@Service
class TerminateModalService(
    val templateEmailService: TemplateEmailService,
    private val emailEnv: EmailEnv,
    private val reservationService: BoatReservationService
) {
    fun getViewVariablesForEmployeeReservationTerminationModal(reservationId: Int): TerminateReservationAsEmployeeModalViewParameters {
        val reservation =
            reservationService.getBoatSpaceReservation(reservationId)
                ?: throw IllegalArgumentException("Reservation not found")
        val terminationMessageTemplates = getEmployeeTerminateMessageTemplates(reservation)
        return TerminateReservationAsEmployeeModalViewParameters(
            reservation,
            terminationMessageTemplates
        )
    }

    private fun getEmployeeTerminateMessageTemplates(reservation: BoatSpaceReservationDetails): Map<String, String> {
        val replaceWith =
            mapOf(
                "harbor" to reservation.locationName,
                "place" to reservation.place,
                "employeeEmail" to emailEnv.employeeAddress
            )
        val messageTemplate = templateEmailService.getTemplate("marine_employee_reservation_termination_custom_message")
        val messageSubject = messageTemplate?.subject ?: ""
        val messageBody = messageTemplate?.body ?: ""
        return mapOf(
            "subject" to templateEmailService.replaceTags(messageSubject, replaceWith),
            "body" to templateEmailService.replaceTags(messageBody, replaceWith),
        )
    }
}
