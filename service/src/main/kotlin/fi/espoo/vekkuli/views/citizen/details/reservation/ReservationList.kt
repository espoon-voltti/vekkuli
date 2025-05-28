package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.WarningBox
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReservationList(
    private val cardHeading: ReservationCardHeading,
    private val cardInfo: ReservationCardInformation,
    private val citizenButtons: CitizenCardButtons,
    private val employeeButtons: EmployeeCardButtons,
    private val reservationTerminationReason: ReservationTerminationReason,
    private val reservationCardWarningBox: ReservationCardWarningBox
) : BaseView() {
    fun render(
        boatSpaceReservations: List<BoatSpaceReservationDetails>,
        userType: UserType,
        reserverId: UUID,
    ): String {
        // language=HTML
        return """
            <div class="reservation-list form-section" ${addTestId("reservation-list")}>
                ${createReservationCards(boatSpaceReservations, userType, reserverId)}
            </div>
            """.trimIndent()
    }

    private fun createReservationCards(
        boatSpaceReservations: List<BoatSpaceReservationDetails>,
        userType: UserType,
        reserverId: UUID
    ): String =
        boatSpaceReservations.joinToString("\n") { reservation ->
            // language=HTML
            """
            <div class="reservation-card" ${addTestId("reservation-list-card")}>
                ${cardHeading.render(reservation)}
                ${cardInfo.render(reservation, userType, reserverId)}
                ${reservationTerminationReason.render(reservation)}
                ${reservationCardWarningBox.render(reservation, userType)}
                ${
                if (userType == UserType.EMPLOYEE) {
                    employeeButtons.render(reservation, reserverId)
                } else {
                    citizenButtons.render(reservation)
                }}
            </div>
            """.trimIndent()
        }
}
