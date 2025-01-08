package fi.espoo.vekkuli.boatSpace.admin.systemTime

import fi.espoo.vekkuli.controllers.EnvType
import fi.espoo.vekkuli.controllers.Utils.Companion.getEnv
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.fullDateTimeFormat
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class SetCurrentSystemTimeView(
    private val timeProvider: TimeProvider
) {
    fun render(time: LocalDateTime): String {
        if (getEnv() == EnvType.Production) {
            return ""
        }
        val currentTime = timeProvider.getCurrentDateTime()
        val isOverwritten = timeProvider.isOverwritten()
        val currentTimeLabel = if (isOverwritten) "Ylikirjoitettu järjestelmän aika" else "Järjestelmän aikaa ei ole ylikirjoitettu"
        // language=HTML
        return """
            <section class="section">
                <div class="container">
                    <h2>$currentTimeLabel: ${currentTime.format(fullDateTimeFormat)}</h2>
                    <form id="set-staging-form"
                      class="is-inline-block"
                      method="post" 
                      hx-post="/admin/set-system-date"
                      hx-on-htmx-after-request="window.location.reload()"
                    >
                         <div class="field">
                            <div class="control">
                                <label class="label" for="newSystemDate" >Vaihda päivämäärä</label>
                                <input
                                    class="input"
                                    style="width: auto"
                                    type="date"
                                    id="newSystemDate"
                                    name="newSystemDate"
                                    ${if (isOverwritten) "value=\"${time.format(DateTimeFormatter.ISO_DATE)}\"" else ""}
                                   />
                                   <p class="help">Jos haluat palauttaa ennalleen järjestelmän ajan, avaa kalenteri ja valitse "clear" vasemmasta alanurkasta. Tallenna.</p>
                            </div>
                        </div>
                        <buttons>
                            <button type="submit" class="button is-primary">Tallenna</button>
                       </buttons>
                    </form>
                </div>
            </section>
            """.trimIndent()
    }
}
