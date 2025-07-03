package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.ReservationWarningType
import fi.espoo.vekkuli.domain.Trailer
import fi.espoo.vekkuli.utils.formatInt
import fi.espoo.vekkuli.utils.intToDecimal
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.WarningBox
import org.springframework.stereotype.Component
import java.util.*

@Component
class TrailerCard(
    private val formComponents: FormComponents,
    private val warningBox: WarningBox,
) : BaseView() {
    fun trailerValueLabel(
        translationKey: String,
        showWarning: Boolean
    ): String {
        val warning =
            if (showWarning) {
                """<span class="icon ml-s">${icons.warningExclamation(false)}</span>"""
            } else {
                ""
            }
        return """
                <label class="label">${t(translationKey)}
            $warning
                </label> 
            """.trimIndent()
    }

    fun trailerValue(
        id: String,
        value: String,
        translationKey: String,
        showWarning: Boolean = false
    ): String =
        """
        <div class="field">
            ${trailerValueLabel(translationKey, showWarning)}
            <p data-testid="$id">$value</p>
        </div> 
        """.trimIndent()

    fun showTrailerWarnings(trailerHasWarnings: Boolean): String {
        if (trailerHasWarnings) {
            // language=HTML
            return """
                <div class="column">
                    <a class="is-link is-icon-link has-text-warning has-text-weight-semibold" x-on:click="modalOpen = true">
                        <span class="icon ml-s">
                            ${icons.warningExclamation(false)}
                        </span>
                        <span data-testid='acknowledge-warnings'>${t("citizenDetails.button.acknowledgeWarnings")}</span>
                    </a>
                </div>
                """
        }
        return ""
    }

    fun showWarningsDialog(
        trailer: Trailer,
        reserverId: UUID
    ): String {
        // language=HTML

        if (trailer.hasAnyWarnings()) {
            val warningLabels =
                trailer.warnings.joinToString("\n") { warning ->
                    """
                    <label class="checkbox pb-s">
                        <input type="checkbox" name="key" value="$warning">
                        <span>${t("reservationWarning.$warning")}</span>
                    </label>
                    """.trimIndent()
                }
            return """
                <div class="modal" x-show="modalOpen" style="display:none;">
                    <div class="modal-underlay" @click="modalOpen = false"></div>
                    <div class="modal-content">
                        <form hx-post="/virkailija/venepaikat/varaukset/kuittaa-traileri-varoitus"
                              hx-swap="outerHTML"
                              hx-target="#reserver-details"
                             >
                            <input type="hidden" name="trailerId" value="${trailer.id}" />
                            <input type="hidden" name="reserverId" value="$reserverId" />
                            <div class="block">
                                <div class="field">
                                    <h1 class="label">${t("citizenDetails.warnings.ackSelect")}</h1>
                                    <div class="control">
                                        $warningLabels
                                    </div>
                                </div>
                            </div>
                            <div class="block">
                                <h1 class="label">${t("citizenDetails.label.warningInfo")}</h1>
                                <div class="control">
                                    <textarea data-testid="warning-info-input" class="textarea" rows="1" name="infoText"></textarea>
                                </div>
                            </div>
                            ${warningBox.render(t("reservationWarning.ackInfo"))}
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
        return ""
    }

    fun render(
        trailer: Trailer?,
        reserverId: UUID,
        reservationId: Int
    ): String {
        val trailerRegNum =
            trailerValue(
                "trailer-registration-code",
                trailer?.registrationCode ?: "-",
                "citizenDetails.trailer.registrationNumber",
                false
            )
        val trailerWidth =
            trailerValue(
                "trailer-width",
                if (trailer?.widthCm != null) formatInt(trailer.widthCm) else "-",
                "shared.label.widthInMeters",
                trailer !== null && trailer.hasWarning(ReservationWarningType.TrailerWidth)
            )
        val trailerLength =
            trailerValue(
                "trailer-length",
                if (trailer?.lengthCm != null) formatInt(trailer.lengthCm) else "-",
                "shared.label.lengthInMeters",
                trailer !== null && trailer.hasWarning(ReservationWarningType.TrailerLength)
            )

        val warningText = if (trailer !== null) showTrailerWarnings(trailer.hasAnyWarnings()) else ""
        val warningDialog = if (trailer !== null) showWarningsDialog(trailer, reserverId) else ""

        // language=HTML
        return """
            <div id="trailer-for-reservation-$reservationId" class="pb-s" x-data="{ modalOpen: false }">
                <div class="columns is-vcentered">
                    <div class="column is-narrow">
                        <h4>${t("boatApplication.trailerInformation")}</h4>
                    </div>
                    $warningText
                    ${editTrailerButton(reserverId, reservationId)}
                </div>
                <div class="columns pb-s">
                   <div class="column is-one-quarter">
                       $trailerRegNum
                   </div>
                   <div class="column is-one-quarter">
                       $trailerWidth
                   </div>
                   <div class="column is-one-quarter">
                      $trailerLength
                   </div>
                </div>
                $warningDialog
            </div>
            """.trimIndent()
    }

    fun renderEdit(
        trailer: Trailer?,
        reserverId: UUID,
        reservationId: Int
    ): String {
        val regNum =
            formComponents.textInput(
                labelKey = "citizenDetails.trailer.registrationNumber",
                value = trailer?.registrationCode,
                id = "trailerRegistrationCode",
                required = true,
            )
        val width =
            formComponents.decimalInput(
                labelKey = "shared.label.widthInMeters",
                value = intToDecimal(trailer?.widthCm),
                id = "trailerWidth",
                required = true,
            )
        val length =
            formComponents.decimalInput(
                labelKey = "shared.label.lengthInMeters",
                value = intToDecimal(trailer?.lengthCm),
                id = "trailerLength",
                required = true,
            )

        val buttons =
            formComponents.buttons(
                "/virkailija/kayttaja/$reserverId",
                "#reserver-details",
                "#reserver-details",
                "trailer-edit-cancel",
                "trailer-edit-submit"
            )

        // language=HTML
        return """ 
            <div id="trailer-for-reservation-$reservationId" class="pb-s">
                <div class="columns is-vcentered">
                    <div class="column is-narrow">
                        <h4>${t("boatApplication.trailerInformation")}</h4>
                    </div>

                </div>
                <form hx-target="#trailer-for-reservation-$reservationId" 
                    hx-patch="${getSaveUrl(reservationId, reserverId)}" hx-swap="outerHTML">
                    <div class="columns" class="pb-s">
                       <div class="column is-one-quarter">
                           <div class="field">
                              $regNum
                           </div>
                       </div>
                       <div class="column is-one-quarter">
                           <div class="field">
                              $width
                           </div>
                       </div>
                       <div class="column is-one-quarter">
                          <div class="field">
                             $length
                          </div>
                       </div>
                    </div>
                    $buttons
                </form>
            </div>
            """.trimIndent()
    }

    private fun getEditUrl(
        reserverId: UUID,
        reservationId: Int
    ) = "/virkailija/$reserverId/varaus/$reservationId/traileri/muokkaa"

    private fun getSaveUrl(
        reservationId: Int,
        reserverId: UUID
    ) = "/virkailija/$reserverId/varaus/$reservationId/traileri/tallenna"

    private fun editTrailerButton(
        reserverId: UUID,
        reservationId: Int
    ): String {
        // language=HTML
        return """
            <div class="column is-narrow ml-auto">
                <a class="is-icon-link is-link"
                   hx-get="${getEditUrl(reserverId, reservationId)}"
                   hx-target="#trailer-for-reservation-$reservationId"
                   hx-swap="outerHTML">
                    <span class="icon">
                        ${icons.edit}
                    </span>
                    <span id="edit-trailer-$reservationId"> ${t("citizenDetails.trailer.editTrailerDetails")}</span>
                </a>
            </div>
            """.trimIndent()
    }
}
