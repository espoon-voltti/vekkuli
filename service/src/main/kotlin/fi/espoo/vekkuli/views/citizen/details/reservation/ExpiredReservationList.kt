package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.components.accordion.Accordion
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ExpiredReservationList : BaseView() {
    @Autowired
    lateinit var icons: Icons

    @Autowired private lateinit var cardHeading: ReservationCardHeading

    @Autowired private lateinit var cardInfo: ReservationCardInformation

    @Autowired private lateinit var accordion: Accordion

    fun render(
        @SanitizeInput citizen: CitizenWithDetails,
        @SanitizeInput boatSpaceReservations: List<BoatSpaceReservationDetails>,
    ): String {
        val accordionBuilder = accordion.createBuilder()

        // language=HTML
        val content =
            """
            <div class="reservation-list form-section" ${addTestId("expired-reservation-list")}>
                ${boatSpaceReservations.joinToString("\n") { reservation ->
                """
                <div class="reservation-card" ${addTestId("expired-reservation-list-card")}>
                    ${cardHeading.render(reservation)}
                    ${cardInfo.render(reservation)}
                </div>
                """.trimIndent()
            }}
            <div>
            """.trimIndent()

        return accordionBuilder
            .setTitle(t("boatSpaceExpiredReservation.title"))
            .setTestId("expired-reservation-list-accordion")
            .setContent(content)
            .build()
    }
}
