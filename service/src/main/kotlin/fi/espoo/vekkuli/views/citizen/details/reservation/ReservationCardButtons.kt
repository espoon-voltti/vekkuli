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
                            ${if (reservation.status == ReservationStatus.Invoiced) {
            modal.createOpenModalBuilder()
                .addAttribute("id","invoice-paid-button")
                .setText(t("citizenDetails.markInvoicePaid"))
                .setPath("/reservation/modal/mark-invoice-paid/${reservation.id}/${citizen.id}")
                .setStyle(ModalButtonStyle.Primary)
                .build()
        } else {
            ""
        }
        }
                            <button class="button is-primary">
                                ${t("boatSpaceReservation.button.swapPlace")}
                            </button>
                            ${
            modal.createOpenModalBuilder()
                .setText(t("boatSpaceReservation.button.terminateReservation"))
                .setPath("/reservation/modal/terminate-reservation/${reservation.id}")
                .setStyle(ModalButtonStyle.DangerOutline)
                .setTestId("open-terminate-reservation-modal")
                .build()
        }
                        </div>
            """.trimIndent()
    }
}
