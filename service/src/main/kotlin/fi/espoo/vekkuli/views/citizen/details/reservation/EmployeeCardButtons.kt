package fi.espoo.vekkuli.views.citizen.details.reservation

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
        // language=HTML
        return """
            <div class="buttons">
                ${commonButtons.createRenewPlaceButton(reservation, UserType.EMPLOYEE)}
                ${createInvoicePaidModalButton(reservation, citizen)}
                ${createTerminateReservationModalButton(reservation)}
            </div>
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
