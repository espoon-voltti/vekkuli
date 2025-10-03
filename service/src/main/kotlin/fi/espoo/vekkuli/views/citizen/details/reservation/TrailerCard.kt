package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.ReservationWarningType
import fi.espoo.vekkuli.domain.TrailerWithWarnings
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
        trailerWithWarnings: TrailerWithWarnings,
        reserverId: UUID
    ): String {
        // language=HTML

        if (trailerWithWarnings.hasAnyWarnings()) {
            val warningLabels =
                trailerWithWarnings.warnings.joinToString("\n") { warning ->
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
                            <input type="hidden" name="trailerId" value="${trailerWithWarnings.id}" />
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
        trailerWithWarnings: TrailerWithWarnings?,
        reserverId: UUID,
        reservationId: Int
    ): String {
        val trailerRegNum =
            trailerValue(
                "trailer-registration-code",
                trailerWithWarnings?.registrationCode ?: "-",
                "citizenDetails.trailer.registrationNumber",
                false
            )
        val trailerWidth =
            trailerValue(
                "trailer-width",
                if (trailerWithWarnings?.widthCm != null) formatInt(trailerWithWarnings.widthCm) else "-",
                "shared.label.widthInMeters",
                trailerWithWarnings !== null && trailerWithWarnings.hasWarning(ReservationWarningType.TrailerWidth)
            )
        val trailerLength =
            trailerValue(
                "trailer-length",
                if (trailerWithWarnings?.lengthCm != null) formatInt(trailerWithWarnings.lengthCm) else "-",
                "shared.label.lengthInMeters",
                trailerWithWarnings !== null && trailerWithWarnings.hasWarning(ReservationWarningType.TrailerLength)
            )

        val warningText = if (trailerWithWarnings !== null) showTrailerWarnings(trailerWithWarnings.hasAnyWarnings()) else ""
        val warningDialog = if (trailerWithWarnings !== null) showWarningsDialog(trailerWithWarnings, reserverId) else ""

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
        trailerWithWarnings: TrailerWithWarnings?,
        reserverId: UUID,
        reservationId: Int
    ): String {
        val regNum =
            formComponents.textInput(
                labelKey = "citizenDetails.trailer.registrationNumber",
                value = trailerWithWarnings?.registrationCode,
                id = "trailerRegistrationCode",
                required = true,
            )
        val width =
            formComponents.decimalInput(
                labelKey = "shared.label.widthInMeters",
                value = intToDecimal(trailerWithWarnings?.widthCm),
                id = "trailerWidth",
                required = true,
            )
        val length =
            formComponents.decimalInput(
                labelKey = "shared.label.lengthInMeters",
                value = intToDecimal(trailerWithWarnings?.lengthCm),
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
                <form id="trailer-reservation-form" hx-target="#trailer-for-reservation-$reservationId" 
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
                <script>validation.init({forms: ['trailer-reservation-form']})</script>
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
