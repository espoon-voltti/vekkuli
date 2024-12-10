package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.Trailer
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import org.springframework.stereotype.Component

@Component
class TrailerCard(
    private val icons: Icons,
    private val formComponents: FormComponents
) : BaseView() {
    fun render(
        trailer: Trailer,
        userType: UserType
    ): String {
        // language=HTML
        return """
            <div id="trailer-${trailer.id}" class="pb-s">
                <div class="columns is-vcentered">
                    <div class="column is-narrow">
                        <h4>${t("boatApplication.trailerInformation")}</h4>
                    </div>
                    ${editTrailerButton(trailer.id, userType)}
                </div>
                <div class="columns pb-s">
                   <div class="column">
                       <div class="field">
                          <label class="label">${t("citizenDetails.trailer.registrationNumber")}</label>
                          <p data-testid='trailer-registration-code'>${trailer.registrationCode}</p>
                       </div>
                   </div>
                   <div class="column">
                       <div class="field">
                          <label class="label">${t("shared.label.widthInMeters")}</label>
                          <p data-testid='trailer-width'>${trailer.widthCm.cmToM()}</p>
                       </div>
                   </div>
                   <div class="column">
                      <div class="field">
                         <label class="label">${t("shared.label.lengthInMeters")}</label>
                         <p data-testid='trailer-length'>${trailer.lengthCm.cmToM()}</p>
                      </div>
                   </div>
                </div>
            </div>
            """.trimIndent()
    }

    fun renderEdit(
        trailer: Trailer,
        userType: UserType
    ): String {
        val regNum =
            formComponents.textInput(
                labelKey = "citizenDetails.trailer.registrationNumber",
                value = trailer.registrationCode,
                id = "trailerRegistrationCode",
                required = true,
            )
        val width =
            formComponents.decimalInput(
                labelKey = "shared.label.widthInMeters",
                value = trailer.widthCm.cmToM(),
                id = "trailerWidth",
                required = true,
            )
        val length =
            formComponents.decimalInput(
                labelKey = "shared.label.lengthInMeters",
                value = trailer.lengthCm.cmToM(),
                id = "trailerLength",
                required = true,
            )
        val buttons =
            formComponents.buttons(
                "/kuntalainen/omat-tiedot",
                "#citizen-details",
                "#citizen-details",
                "trailer-edit-cancel",
                "trailer-edit-submit"
            )
        // language=HTML
        return """ 
            <div id="trailer-${trailer.id}" class="pb-s">
                <div class="columns is-vcentered">
                    <div class="column is-narrow">
                        <h4>${t("boatApplication.trailerInformation")}</h4>
                    </div>

                </div>
                <form hx-target="#trailer-${trailer.id}" hx-patch="${getSaveUrl(trailer.id, userType)}">
                    <div class="columns" class="pb-s">
                       <div class="column">
                           <div class="field">
                              $regNum
                           </div>
                       </div>
                       <div class="column">
                           <div class="field">
                              $width
                           </div>
                       </div>
                       <div class="column">
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
        trailerId: Int,
        userType: UserType
    ) = "/${userType.path}/traileri/$trailerId/muokkaa"

    private fun getSaveUrl(
        trailerId: Int,
        userType: UserType
    ) = "/${userType.path}/traileri/$trailerId/tallenna"

    private fun editTrailerButton(
        trailerId: Int,
        userType: UserType
    ): String {
        // language=HTML
        return """
            <div class="column is-narrow ml-auto">
                <a class="is-icon-link is-link"
                   hx-get="${getEditUrl(trailerId, userType)}"
                   hx-target="#trailer-$trailerId"
                   hx-swap="outerHTML">
                    <span class="icon">
                        ${icons.edit}
                    </span>
                    <span id="edit-trailer-$trailerId"> ${t("citizenDetails.trailer.editTrailerDetails")}</span>
                </a>
            </div>
            """.trimIndent()
    }
}
