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
}
