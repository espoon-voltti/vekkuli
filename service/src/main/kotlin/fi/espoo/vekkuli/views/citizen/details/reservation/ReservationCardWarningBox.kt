package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.WarningBox
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.stereotype.Component

@Component
class ReservationCardWarningBox(
    private val warningBox: WarningBox
) : BaseView() {
    fun render(
        @SanitizeInput reservation: BoatSpaceReservationDetails,
    ): String {
        // language=HTML
        if (!reservation.canRenew) {
            return ""
        }
        return warningBox.render(
            t("reservationWarning.EMPLOYEE.renewInfo", listOf(formatAsFullDate(reservation.endDate)))
        )
    }
}
