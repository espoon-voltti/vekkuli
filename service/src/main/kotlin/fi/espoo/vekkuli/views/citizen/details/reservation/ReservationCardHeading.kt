package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ReservationCardHeading : BaseView() {
    @Autowired
    lateinit var icons: Icons

    fun render(
        @SanitizeInput reservation: BoatSpaceReservationDetails,
    ): String {
        // language=HTML
        return """
            <div class="columns is-vcentered">
                <div class="column is-narrow">
                    <h4>${t("citizenDetails.boatSpace")}: ${reservation.locationName} ${reservation.place}</h4>
                </div>
            </div>
            """.trimIndent()
    }
}
