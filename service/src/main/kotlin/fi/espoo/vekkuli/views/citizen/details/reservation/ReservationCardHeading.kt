package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.stereotype.Component

@Component
class ReservationCardHeading(
    private val reservationWarningView: ReservationGeneralWarningView
) : BaseView() {
    fun render(
        @SanitizeInput reservation: BoatSpaceReservationDetails,
        enableGeneralWarning: Boolean? = true
    ): String {
        val warningView = if (enableGeneralWarning == true) reservationWarningView.render(reservation.id, reservation.reserverId) else ""
        // language=HTML
        return """
            <div class="columns is-vcentered">
                <div class="column is-narrow">
                    <h4>
                        ${t("shared.title.boatSpace.${reservation.type}")}: ${reservation.locationName} ${reservation.place}
                    </h4>                    
                </div>
                $warningView                
            </div>
            """.trimIndent()
    }
}
