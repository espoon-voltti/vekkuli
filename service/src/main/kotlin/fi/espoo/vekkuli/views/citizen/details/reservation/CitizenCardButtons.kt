package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.stereotype.Component

@Component
class CitizenCardButtons(
    private val modal: Modal,
    private val commonButtons: ReservationCardButtons
) : BaseView() {
    fun render(reservation: BoatSpaceReservationDetails): String {
        if (reservation.status == ReservationStatus.Cancelled) {
            return ""
        }
        // language=HTML
        return """
            <div class="buttons">
                ${createRenewPlaceButton(reservation)}
                ${createTerminateReservationModalButton(reservation)}
            </div>
            """.trimIndent()
    }

    fun createRenewPlaceButton(reservation: BoatSpaceReservationDetails): String {
        if (!reservation.canRenew) {
            return ""
        }

        val renewUrl =
            "/kuntalainen/venepaikka/jatka/${reservation.id}"
        return """
            <button 
              class="button is-primary"
              id="renew-place-button-${reservation.id}"
              hx-get="$renewUrl"
              hx-target="body"
              hx-push-url="true">
                ${t("boatSpaceReservation.${UserType.CITIZEN}.button.renewPlace")}
            </button>
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
