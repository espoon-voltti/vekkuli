package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class ReservationCardHeading : BaseView() {
    fun render(heading: String,): String {
        // language=HTML
        return """
            <div class="columns is-vcentered">
                <div class="column is-narrow">
                    <h4>$heading</h4>
                </div>
            </div>
            """.trimIndent()
    }
}
