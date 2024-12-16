package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.boatSpace.reservationStatus.ReservationStatus
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.stereotype.Component

@Component
class EmployeeCardButtons(
    private var modal: Modal,
    private val commonButtons: ReservationCardButtons
) : BaseView() {
    fun render(
        reservation: BoatSpaceReservationDetails,
        citizen: CitizenWithDetails,
    ): String {
        if (reservation.status == ReservationStatus.Cancelled) {
            return ""
        }
        // language=HTML
        return """
            <div class="buttons">
                ${createRenewPlaceButton(reservation)}
                ${createInvoicePaidModalButton(reservation, citizen)}
                ${createTerminateReservationModalButton(reservation)}
            </div>
            """.trimIndent()
    }

    fun createRenewPlaceButton(reservation: BoatSpaceReservationDetails): String {
        if (!reservation.canRenew) {
            return ""
        }

        val renewUrl =
            "/virkailija/venepaikka/jatka/${reservation.id}/lasku"
        return """
            <button 
              class="button is-primary"
              id="renew-place-button-${reservation.id}"
              hx-get="$renewUrl"
              hx-target="body"
              hx-push-url="true">
                ${t("boatSpaceReservation.${UserType.EMPLOYEE}.button.renewPlace")}
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
            .setPath("/boat-space/terminate-reservation/modal/as-employee/${reservation.id}")
            .setStyle(ModalButtonStyle.DangerOutline)
            .setTestId("open-terminate-reservation-modal-for-employee")
            .build()
}
