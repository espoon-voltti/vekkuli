package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.WarningBox
import org.springframework.stereotype.Component

@Component
class ReservationList(
    private val cardHeading: ReservationCardHeading,
    private val cardInfo: ReservationCardInformation,
    private val citizenButtons: CitizenCardButtons,
    private val employeeButtons: EmployeeCardButtons,
    private val warningBox: WarningBox,
) : BaseView() {
    fun render(
        citizen: CitizenWithDetails,
        boatSpaceReservations: List<BoatSpaceReservationDetails>,
        userType: UserType = UserType.CITIZEN
    ): String {
        // language=HTML
        return """
            <div class="reservation-list form-section" ${addTestId("reservation-list")}>
                ${createReservationCards(boatSpaceReservations, citizen, userType)}
            </div>
            """.trimIndent()
    }

    private fun createReservationCards(
        boatSpaceReservations: List<BoatSpaceReservationDetails>,
        citizen: CitizenWithDetails,
        userType: UserType
    ): String =
        boatSpaceReservations.joinToString("\n") { reservation ->
            // language=HTML
            """
            <div class="reservation-card" ${addTestId("reservation-list-card")}>
                ${cardHeading.render(reservation)}
                ${cardInfo.render(reservation)}
                ${if (reservation.canRenew) warningBox.render(t("reservationWarning.renewInfo")) else ""}
                ${
                if (userType == UserType.EMPLOYEE) {
                    employeeButtons.render(reservation, citizen)
                } else {
                    citizenButtons.render(reservation, citizen)
                }}
            </div>
            """.trimIndent()
        }
}
