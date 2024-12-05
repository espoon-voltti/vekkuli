package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.Trailer
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.views.BaseView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TrailerCard : BaseView() {
    @Autowired
    lateinit var cardHeading: ReservationCardHeading

    fun render(trailer: Trailer): String {
        // language=HTML
        return """
            ${cardHeading.render("Trailerin tiedot")}
            <div class="columns">
               <div class="column">
                   <div class="field">
                      <label class="label">${t("trailer.registrationNumber")}</label>
                      <p>${trailer.registrationCode}</p>
                   </div>
               </div>
               <div class="column">
                   <div class="field">
                      <label class="label">${t("boatApplication.widthInMeters")}</label>
                      <p>${trailer.widthCm.cmToM()}</p>
                   </div>
               </div>
               <div class="column">
                  <div class="field">
                     <label class="label">${t("boatApplication.lengthInMeters")}</label>
                     <p>${trailer.lengthCm.cmToM()}</p>
                  </div>
               </div>
            </div>
            """.trimIndent()
    }
}
