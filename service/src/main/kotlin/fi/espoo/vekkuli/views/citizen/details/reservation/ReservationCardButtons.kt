package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.stereotype.Component

@Component
class ReservationCardButtons(
    private var modal: Modal
) : BaseView() {
    fun render(
        reservation: BoatSpaceReservationDetails,
        citizen: CitizenWithDetails
    ): String {
        // language=HTML
        return """
            <div class="buttons">
                ${createRenewPlaceButton(reservation)}
                ${createInvoicePaidModalButton(reservation, citizen)}
                ${createSwapPlaceButton(reservation)}
                ${createTerminateReservationModalButton(reservation)}
            </div>
            """.trimIndent()
    }

    fun createSwapPlaceButton(reservation: BoatSpaceReservationDetails): String {
        if (!reservation.canSwitch) {
            return ""
        }

        return """
            <button class="button is-primary">
                ${t("boatSpaceReservation.button.swapPlace")}
            </button>
            """.trimIndent()
    }

    fun createRenewPlaceButton(reservation: BoatSpaceReservationDetails): String {
        if (!reservation.canRenew) {
            return ""
        }

        return """
            <button 
              class="button is-primary"
              id="renew-place-button-${reservation.id}"
              hx-get="/kuntalainen/venepaikka/jatka-varausta/${reservation.id}"
              hx-target="body"
              hx-push-url="true">
                ${t("boatSpaceReservation.button.renewPlace")}
            </button>
            """.trimIndent()
    }

    fun createInvoicePaidModalButton(
        reservation: BoatSpaceReservationDetails,
        citizen: CitizenWithDetails
    ): String {
        if (reservation.status == ReservationStatus.Invoiced) {
            return modal
                .createOpenModalBuilder()
                .addAttribute("id", "invoice-paid-button")
                .setText(t("citizenDetails.markInvoicePaid"))
                .setPath("/reservation/modal/mark-invoice-paid/${reservation.id}/${citizen.id}")
                .setStyle(ModalButtonStyle.Primary)
                .build()
        }
        return ""
    }

    fun createTerminateReservationModalButton(reservation: BoatSpaceReservationDetails): String =
        modal
            .createOpenModalBuilder()
            .setText(t("boatSpaceReservation.button.terminateReservation"))
            .setPath("/reservation/modal/terminate-reservation/${reservation.id}")
            .setStyle(ModalButtonStyle.DangerOutline)
            .setTestId("open-terminate-reservation-modal")
            .build()
}
