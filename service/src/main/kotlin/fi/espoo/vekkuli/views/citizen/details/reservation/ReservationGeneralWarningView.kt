package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class ReservationGeneralWarningView : BaseView() {
    private fun reservationWarningContainer(
        reservationId: Int,
        hasGeneralWarning: Boolean
    ): String {
        if (hasGeneralWarning) {
            return generalWarningIsSet(reservationId)
        } else {
            // language=HTML
            return """
                <div class="general-warning-checkbox column is-narrow ml-auto checkbox">            
                    <input type="checkbox" id="general-warning-checkbox-$reservationId" ${addTestId("general-warning-checkbox")}
                        hx-trigger="click"
                        hx-vals='{"infoText": ""}'
                        hx-post="${getServiceUrl("/virkailija/kayttaja/reservation/partial/general-warning/$reservationId/toggle-warning/${!hasGeneralWarning}")}"                    
                        hx-swap="outerHTML"  
                        hx-target="#general-warning-$reservationId" 
                    >                
                    <span>${t("employee.reserverDetails.exceptions.general.reservation")}</span>                
                </div>
                """.trimIndent()
        }
    }

    fun generalWarningIsSet(reservationId: Int): String {
        // language=HTML
        return """
            <div class="column">
                <a class="is-link is-icon-link has-text-warning has-text-weight-semibold" x-on:click="modalOpen = true">
                    <span class="icon ml-s">${icons.warningExclamation(false)}</span> 
                    <span data-testid='acknowledge-warnings'>${t("citizenDetails.button.acknowledgeWarnings")}</span> 
                </a>
            </div>
            """.trimIndent()
    }

    fun showAcknowledgeWarningModal(
        reservationId: Int,
        hasGeneralWarning: Boolean
    ): String {
        // language=HTML
        return """
            <div class="modal" x-show="modalOpen" style="display:none;">
                <div class="modal-underlay" @click="modalOpen = false"></div>
                <div class="modal-content">
                    <form hx-post="${getServiceUrl("/virkailija/kayttaja/reservation/partial/general-warning/$reservationId/toggle-warning/${!hasGeneralWarning}")}"
                          hx-swap="outerHTML"   
                          hx-target="#general-warning-$reservationId"                               
                         >
                        <div class="block">
                            <h1 class="label">${t("citizenDetails.label.warningInfo")}</h1>
                            <div class="control">
                                <textarea data-testid="warning-info-input" class="textarea" rows="1" name="infoText"></textarea>
                            </div>
                        </div>
                        <div class="block">
                            <button id="ack-modal-cancel"
                                    class="button"
                                    x-on:click="modalOpen = false"
                                    type="button">
                                ${t("cancel")}
                            </button>
                            <button
                                    id="ack-modal-confirm"
                                    class="button is-primary"
                                    type="submit">
                                ${t("confirm")}
                            </button>
                        </div>
                    </form>
                </div>
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
        return """
            <div x-data="{ modalOpen: false }" id="general-warning-$reservationId">
                ${reservationWarningContainer(reservationId, hasGeneralWarning)}
                ${showAcknowledgeWarningModal(reservationId, hasGeneralWarning)}
            </div>
            """.trimIndent()
    }
}
