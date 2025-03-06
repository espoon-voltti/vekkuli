package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.domain.ReservationWarning
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReservationGeneralWarningView : BaseView() {
    private fun reservationWarningContainer(
        reservationId: Int,
        generalWarning: ReservationWarning?
    ): String {
        if (generalWarning != null) {
            // language=HTML
            return """
                <div class="column">
                    <a class="is-link is-icon-link has-text-warning has-text-weight-semibold" x-on:click="modalOpen = true">
                        <span class="icon ml-s">${icons.warningExclamation(false)}</span> 
                        <span data-testid='acknowledge-warnings'>${t("employee.reservation.warning.acknowledge")}</span> 
                    </a>
                </div>
                """.trimIndent()
        } else {
            // language=HTML
            return """
                <div class="column">
                    <a class="is-link is-icon-link" 
                        hx-trigger="click"
                        x-on:click="modalOpen = true"
                        hx-swap="outerHTML"  
                        hx-target="#general-warning-$reservationId">
                        <span class="icon mr-s">${icons.plus}</span>
                        <span>${t("employee.reserverDetails.exceptions.general.reservation")}</span> 
                    </a>
                </div>                
                """.trimIndent()
        }
    }

    fun showAcknowledgeWarningModal(
        reservationId: Int,
        reserverId: UUID,
        generalWarning: ReservationWarning?
    ): String {
        // language=HTML
        return """
            <div class="modal" x-show="modalOpen" style="display:none;">
                <div class="modal-underlay" @click="modalOpen = false"></div>
                <div class="modal-content">
                    <form hx-swap="outerHTML" hx-target="#general-warning-$reservationId">
                        <div class="block">
                            <h1 class="label">${t("employee.reservation.warning.infoText")}</h1>
                            <div class="control">
                                <textarea data-testid="warning-info-input" class="textarea" rows="1" name="infoText">${generalWarning?.infoText ?: ""}</textarea>
                            </div>
                        </div>
                        <div class="block">
                            ${renderButtons(reservationId, reserverId, generalWarning)}
                        </div>
                    </form>
                </div>
            </div>
            """.trimIndent()
    }

    private fun renderButtons(
        reservationId: Int,
        reserverId: UUID,
        generalWarning: ReservationWarning?
    ): String {
        val submitUrl =
            getServiceUrl("/virkailija/kayttaja/reservation/partial/general-warning/$reserverId/$reservationId")
        if (generalWarning == null) {
            // language=HTML
            return """
                <button id="ack-modal-cancel"
                    class="button"
                    x-on:click="modalOpen = false" 
                    type="button">
                        ${t("cancel")}
                </button>
                <button
                        id="ack-modal-save"
                        hx-post="$submitUrl"
                        class="button is-primary"
                        type="submit">
                    ${t("employee.reserverDetails.exceptions.general.reservation")}
                </button>
                """.trimIndent()
        } else {
            // language=HTML
            return """
                <button id="ack-modal-update"
                        hx-patch="$submitUrl"
                        class="button"
                        type="submit">
                    ${t("employee.reservation.warning.update")}
                </button>
                <button
                        id="ack-modal-delete"
                        hx-post="$submitUrl/acknowledge"
                        class="button is-primary"
                        type="submit">
                    ${t("employee.reservation.warning.acknowledge")}
                </button>
                """.trimIndent()
        }
    }

    fun render(
        reservationId: Int,
        reserverId: UUID
    ): String {
        // language=HTML
        return """
            <div
                hx-get="/virkailija/kayttaja/reservation/partial/general-warning/$reserverId/$reservationId"
                hx-trigger="load"
                hx-swap="outerHTML"            
                >
            </div>            
            """.trimIndent()
    }

    fun renderContent(
        reservationId: Int,
        reserverId: UUID,
        generalWarning: ReservationWarning?
    ): String {
        // language=HTML
        return """
            <div x-data="{ modalOpen: false }" id="general-warning-$reservationId">
                ${reservationWarningContainer(reservationId, generalWarning)}
                ${showAcknowledgeWarningModal(reservationId, reserverId, generalWarning)}
            </div>
            """.trimIndent()
    }
}
