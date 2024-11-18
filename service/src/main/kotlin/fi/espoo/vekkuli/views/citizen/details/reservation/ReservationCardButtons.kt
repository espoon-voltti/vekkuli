package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class ReservationCardButtons : BaseView() {
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
}
