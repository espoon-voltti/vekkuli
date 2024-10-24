package fi.espoo.vekkuli.views.admin

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class SetCurrentSystemTime {
    fun render(time: LocalDateTime): String {
        // language=HTML
        return """
            <section class="section">
                <div class="container">
                    <h2>Current Staging time: ${time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss"))}</h2>
                    <form id="set-staging-form"
                      class="is-inline-block"
                      method="post" 
                      hx-post="/admin/set-system-date"
                      hx-on-htmx-after-request="window.location.reload()"
                    >
                         <div class="field">
                            <div class="control">
                                <label class="label" for="newSystemDate" >Set system date</label>
                                <input
                                    class="input"
                                    type="date"
                                    id="newSystemDate"
                                    name="newSystemDate"
                                    ${if (time != null) "value=\"${time.format(DateTimeFormatter.ISO_DATE)}\"" else ""}
                                   />
                            </div>
                        </div>
                        <buttons>
                            <button type="submit" class="button is-primary">Set system date</button>
                       </buttons>
                    </form>
                </div>
            </section>
            """.trimIndent()
    }
}
