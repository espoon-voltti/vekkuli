package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component
import java.util.*

@Component
class EditLink : BaseView() {
    fun render(
        url: String,
        target: String = "#modal-container",
        id: String
    ): String {
        // language=HTML
        return (
            """
             <div>
                <a class="is-link is-icon-link edit-link"
                    id="$id"
                    ${addTestId(id)}
                    hx-get="$url"
                    hx-target="$target"
                    hx-swap="innerHTML">
                    <span class="icon">
                        ${icons.edit}
                    </span>
                </a>
            </div>
            """.trimIndent()
        )
    }
}
