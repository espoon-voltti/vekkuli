package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.accordion.Accordion
import org.springframework.stereotype.Component
import java.util.*

@Component
class ExpiredReservationList(
    private val cardHeading: ReservationCardHeading,
    private val cardInfo: ReservationCardInformation,
    private val accordion: Accordion,
    private val reservationTerminationReason: ReservationTerminationReason
) : BaseView() {
    fun render(
        boatSpaceReservations: List<BoatSpaceReservationDetails>,
        userType: UserType,
        reserverId: UUID,
    ): String {
        val accordionBuilder = accordion.createBuilder()

        // language=HTML
        val content =
            """
            <div class="reservation-list form-section" ${addTestId("expired-reservation-list")}>
                ${boatSpaceReservations.joinToString("\n") { reservation ->
                """
                <div class="reservation-card" ${addTestId("expired-reservation-list-card")}>
                    ${cardHeading.render(reservation, false)}
                    ${cardInfo.render(reservation, userType, reserverId)}
                    ${reservationTerminationReason.render(reservation)}
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
