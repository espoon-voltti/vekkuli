package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.stereotype.Component

@Component
class EmployeeReservationCardButtons(
    private var modal: Modal
) : ReservationCardButtons(modal) {
    override fun createTerminateReservationModalButton(reservation: BoatSpaceReservationDetails): String =
        modal
            .createOpenModalBuilder()
            .setText(t("boatSpaceReservation.button.terminateReservation"))
            .setPath("/reservation/modal/terminate-reservation-for-other-user/${reservation.id}")
            .setStyle(ModalButtonStyle.DangerOutline)
            .setTestId("open-terminate-reservation-modal-for-employee")
            .build()
}
