package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.stereotype.Component

@Component
class CitizenCardButtons(
    private val modal: Modal,
    private val commonButtons: ReservationCardButtons
) : BaseView() {
    fun render(
        reservation: BoatSpaceReservationDetails,
        citizen: CitizenWithDetails
    ): String {
        if (reservation.status == ReservationStatus.Cancelled) {
            return ""
        }
        // language=HTML
        return """
            <div class="buttons">
                ${commonButtons.createRenewPlaceButton(reservation)}
                ${commonButtons.createSwapPlaceButton(reservation)}
                ${createTerminateReservationModalButton(reservation)}
            </div>
            """.trimIndent()
    }

    fun createTerminateReservationModalButton(reservation: BoatSpaceReservationDetails): String =
        modal
            .createOpenModalBuilder()
            .setText(t("boatSpaceReservation.button.terminateReservation"))
            .setPath("/boat-space/terminate-reservation/modal/${reservation.id}")
            .setStyle(ModalButtonStyle.DangerOutline)
            .setTestId("open-terminate-reservation-modal")
            .build()
}
