package fi.espoo.vekkuli.boatSpace.terminateReservation.modal

import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.VariableService
import org.springframework.stereotype.Service

@Service
class TerminateModalService(
    val variableService: VariableService,
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
                "{{harbor}}" to reservation.locationName,
                "{{place}}" to reservation.place,
                "{{employeeEmail}}" to emailEnv.employeeAddress
            )
        val messageSubject = variableService.get("employee_reservation_termination_subject")?.value ?: ""
        val messageBody = variableService.get("employee_reservation_termination_template")?.value ?: ""
        return mapOf(
            "subject" to replaceWith.entries.fold(messageSubject) { acc, (key, value) -> acc.replace(key, value) },
            "body" to replaceWith.entries.fold(messageBody) { acc, (key, value) -> acc.replace(key, value) },
        )
    }
}
