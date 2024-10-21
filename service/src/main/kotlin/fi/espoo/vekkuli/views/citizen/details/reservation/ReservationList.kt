package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.components.modal.*
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ReservationList : BaseView() {
    @Autowired
    lateinit var icons: Icons

    @Autowired private lateinit var cardHeading: ReservationCardHeading

    @Autowired private lateinit var cardInfo: ReservationCardInformation

    @Autowired private lateinit var cardButtons: ReservationCardButtons

    fun render(
        @SanitizeInput citizen: CitizenWithDetails,
        @SanitizeInput boatSpaceReservations: List<BoatSpaceReservationDetails>,
    ): String {
        // language=HTML
        return """
                        <div class="reservation-list form-section" ${addTestId("reservation-list")}>
                            ${boatSpaceReservations.joinToString("\n") { reservation ->
            """
            <div class="reservation-card" ${addTestId("reservation-list-card")}>
                ${cardHeading.render(reservation)}
                ${cardInfo.render(reservation)}
                ${cardButtons.render(reservation, citizen)}
            </div>
            """.trimIndent()
        }}
                        </div>
            """.trimIndent()
    }
}
