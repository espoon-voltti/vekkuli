package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.components.modal.*
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ReservationCardButtons : BaseView() {
    @Autowired
    lateinit var icons: Icons

    @Autowired private lateinit var modal: Modal

    fun render(
        @SanitizeInput reservation: BoatSpaceReservationDetails,
        @SanitizeInput citizen: CitizenWithDetails,
    ): String {
        // language=HTML
        return """
            <div class="buttons">
                ${createInvoicePaidModalButton(reservation, citizen)}
                <button class="button is-primary">
                    ${t("boatSpaceReservation.button.swapPlace")}
                </button>
                ${createTerminateReservationModalButton(reservation)}
            </div>
            """.trimIndent()
    }

    fun createInvoicePaidModalButton(
        reservation: BoatSpaceReservationDetails,
        citizen: CitizenWithDetails
    ): String {
        if (reservation.status == ReservationStatus.Invoiced) {
            return modal.createOpenModalBuilder()
                .addAttribute("id", "invoice-paid-button")
                .setText(t("citizenDetails.markInvoicePaid"))
                .setPath("/reservation/modal/mark-invoice-paid/${reservation.id}/${citizen.id}")
                .setStyle(ModalButtonStyle.Primary)
                .build()
        }
        return ""
    }

    fun createTerminateReservationModalButton(reservation: BoatSpaceReservationDetails): String {
        return modal.createOpenModalBuilder()
            .setText(t("boatSpaceReservation.button.terminateReservation"))
            .setPath("/reservation/modal/terminate-reservation/${reservation.id}")
            .setStyle(ModalButtonStyle.DangerOutline)
            .setTestId("open-terminate-reservation-modal")
            .build()
    }
}
