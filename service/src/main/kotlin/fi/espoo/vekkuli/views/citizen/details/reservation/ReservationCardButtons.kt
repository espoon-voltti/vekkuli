package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.controllers.UserType
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
        userType: UserType
    ): String {
        val swapPlace =
            if (reservation.canSwitch) {
                """
                <button class="button is-primary">
                    ${t("boatSpaceReservation.button.swapPlace")}
                </button>
                """.trimIndent()
            } else {
                ""
            }
        val renewPlace =
            if (reservation.canRenew) {
                """
                <button 
                  class="button is-primary"
                  id="renew-place-button-${reservation.id}"
                  hx-get="/kuntalainen/venepaikka/jatka-varausta/${reservation.id}"
                  hx-target="body"
                  hx-push-url="true">
                    ${t("boatSpaceReservation.button.renewPlace")}
                </button>
                """.trimIndent()
            } else {
                ""
            }
        // language=HTML
        return """
            <div class="buttons">
                $renewPlace
                ${createInvoicePaidModalButton(reservation, citizen, userType)}
                $swapPlace
                ${createTerminateReservationModalButton(reservation)}
            </div>
            """.trimIndent()
    }

    fun createInvoicePaidModalButton(
        reservation: BoatSpaceReservationDetails,
        citizen: CitizenWithDetails,
        userType: UserType
    ): String {
        if (userType == UserType.EMPLOYEE && reservation.status == ReservationStatus.Invoiced) {
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
