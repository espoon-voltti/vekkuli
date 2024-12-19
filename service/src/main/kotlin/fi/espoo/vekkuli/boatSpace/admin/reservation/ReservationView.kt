package fi.espoo.vekkuli.boatSpace.admin.reservation

import fi.espoo.vekkuli.controllers.EnvType
import fi.espoo.vekkuli.controllers.Utils.Companion.getEnv
import org.springframework.stereotype.Service

@Service
class ReservationView {
    fun render(resultMessage: String? = null): String {
        if (getEnv() !in setOf(EnvType.Staging, EnvType.Local)) {
            return ""
        }
        return """
            <section class="section" id="reservation-clear">
                <div class="container">
                   <h2>Varausten putsaus</h2>
                   <form hx-post="/admin/reservations"
                         hx-target="#reservation-clear">
                      <div class="field">
                        <label class="label">Varaajan nimi</label>
                        <div class="control">
                            <input type="text" name="reserverName"/>
                        </div>
                      </div>
                      <button class="button is-primary">Putsaa varaukset</button>
                      ${if (resultMessage.isNullOrBlank()) "" else """<p class="has-text-success"">$resultMessage</p>"""}
                   </form>
                </div>
            </section>
            """.trimIndent()
    }
}
