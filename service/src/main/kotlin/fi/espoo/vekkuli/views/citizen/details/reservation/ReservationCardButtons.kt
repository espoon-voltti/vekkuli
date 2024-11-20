package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.controllers.UserType
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

    fun createRenewPlaceButton(
        reservation: BoatSpaceReservationDetails,
        userType: UserType
    ): String {
        if (!reservation.canRenew) {
            return ""
        }

        val renewUrl =
            if (userType == UserType.CITIZEN) {
                "/kuntalainen/venepaikka/jatka/${reservation.id}"
            } else {
                "/virkailija/venepaikka/jatka/${reservation.id}/lasku"
            }
        return """
            <button 
              class="button is-primary"
              id="renew-place-button-${reservation.id}"
              hx-get="$renewUrl"
              hx-target="body"
              hx-push-url="true">
                ${t("boatSpaceReservation.$userType.button.renewPlace")}
            </button>
            """.trimIndent()
    }
}
