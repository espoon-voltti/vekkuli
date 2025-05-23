package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.boatSpace.renewal.RenewalPolicyService
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class EmployeeCardButtons(
    private var modal: Modal,
    private val commonButtons: ReservationCardButtons,
    private val renewalPolicyService: RenewalPolicyService
) : BaseView() {
    fun render(
        reservation: BoatSpaceReservationDetails,
        reserverId: UUID
    ): String {
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
        if (!renewalPolicyService.employeeCanRenewReservation(reservation.id).success) {
            return ""
        }

        val renewUrl =
            "/virkailija/venepaikka/jatka/${reservation.id}/lasku"
        return """
            <button 
              class="button is-primary"
              id="renew-place-button-${reservation.id}"
              hx-get="$renewUrl"
              hx-target="body">
                ${t("boatSpaceReservation.${UserType.EMPLOYEE}.button.renewPlace")}
            </button>
            """.trimIndent()
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
