package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.components.WarningBox
import fi.espoo.vekkuli.views.components.modal.*
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.stereotype.Component

@Component
class ReservationList(
    private val icons: Icons,
    private val cardHeading: ReservationCardHeading,
    private val cardInfo: ReservationCardInformation,
    private val cardButtons: ReservationCardButtons,
    private val warningBox: WarningBox,
) : BaseView() {
    fun render(
        @SanitizeInput citizen: CitizenWithDetails,
        @SanitizeInput boatSpaceReservations: List<BoatSpaceReservationDetails>,
    ): String {
        // language=HTML
        return """
            <div class="reservation-list form-section" ${addTestId("reservation-list")}>
                ${createReservationCards(boatSpaceReservations, citizen)}
            </div>
            """.trimIndent()
    }

    fun createReservationCards(
        boatSpaceReservations: List<BoatSpaceReservationDetails>,
        citizen: CitizenWithDetails
    ): String =
        boatSpaceReservations.joinToString("\n") { reservation ->
            // language=HTML
            """
            <div class="reservation-card" ${addTestId("reservation-list-card")}>
                ${cardHeading.render(reservation)}
                ${cardInfo.render(reservation)}
                ${if (reservation.canRenew) warningBox.render(t("reservationWarning.renewInfo")) else ""}
                ${cardButtons.render(reservation, citizen)}
            </div>
            """.trimIndent()
        }
}
