package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.stereotype.Component

@Component
class ReservationGeneralWarningView : BaseView() {
    private fun reservationWarningContainer(
        reservationId: Int,
        hasGeneralWarning: Boolean
    ): String {
        // language=HTML
        return """
            <div class="general-warning-checkbox column is-narrow ml-auto checkbox">            
                <input type="checkbox" id="general-warning-checkbox-$reservationId" ${addTestId("general-warning-checkbox")}
                    hx-trigger="click"
                    ${if (hasGeneralWarning) "checked" else "" }
                    hx-patch="${getServiceUrl("/virkailija/kayttaja/reservation/partial/general-warning/$reservationId/toggle-warning/${!hasGeneralWarning}")}"                    
                    hx-swap="innerHTML"
                >                
                <span>${t("employee.reserverDetails.exceptions.general.reservation")}</span>                
            </div>
            """.trimIndent()
    }

    fun render(reservationId: Int): String {
        // language=HTML
        return """
            <div
                hx-get="/virkailija/kayttaja/reservation/partial/general-warning/$reservationId"
                hx-trigger="load"
                hx-swap="outerHTML"            
                >
            </div>
            """.trimIndent()
    }

    fun renderContent(
        reservationId: Int,
        hasGeneralWarning: Boolean
    ): String {
        // language=HTML
        return reservationWarningContainer(reservationId, hasGeneralWarning)
    }
}
