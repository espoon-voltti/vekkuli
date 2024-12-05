package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.Trailer
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class TrailerCard : BaseView() {
    @Autowired
    lateinit var icons: Icons

    fun render(
        trailer: Trailer,
        reserverId: UUID,
    ): String {
        // language=HTML
        return """
            <div class="columns is-vcentered">
                <div class="column is-narrow">
                    <h4>${t("boatApplication.trailerInformation")}</h4>
                </div>
                
            </div>
            <div id="trailer-${trailer.id}" class="columns">
               <div class="column">
                   <div class="field">
                      <label class="label">${t("citizenDetails.trailer.registrationNumber")}</label>
                      <p>${trailer.registrationCode}</p>
                   </div>
               </div>
               <div class="column">
                   <div class="field">
                      <label class="label">${t("shared.label.widthInMeters")}</label>
                      <p>${trailer.widthCm.cmToM()}</p>
                   </div>
               </div>
               <div class="column">
                  <div class="field">
                     <label class="label">${t("shared.label.lengthInMeters")}</label>
                     <p>${trailer.lengthCm.cmToM()}</p>
                  </div>
               </div>
            </div>
            """.trimIndent()
    }

    fun renderEdit(trailer: Trailer): String = "EDIT TRAILER $trailer"

    fun getEditUrl(
        trailerId: Int,
        reserverId: UUID,
    ) = "/kuntalainen/traileri/$trailerId/muokkaa"

    fun editTrailerButton(
        trailerId: Int,
        reserverId: UUID,
    ): String {
        // language=HTML
        return """
            <div class="column is-narrow ml-auto">
                <a class="is-icon-link is-link"
                   hx-get="${getEditUrl(trailerId, reserverId)}"
                   hx-target="#trailer-$trailerId"
                   hx-swap="innerHTML">
                    <span class="icon">
                        ${icons.edit}
                    </span>
                    <span id="edit-trailer-$trailerId"> ${t("citizenDetails.trailer.editTrailerDetails")}</span>
                </a>
            </div>
            """.trimIndent()
    }
}
