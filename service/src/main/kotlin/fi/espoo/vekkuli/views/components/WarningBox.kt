package fi.espoo.vekkuli.views.components

import fi.espoo.vekkuli.views.Icons
import org.springframework.stereotype.Component

@Component
class WarningBox(
    private val icons: Icons
) {
    fun render(content: String): String =
        """
        <div class="ack-info">
            <div class="info-icon">${icons.warningExclamation(false)}</div>
            <div class="info-content">
                $content
            </div>
        </div>
        """.trimIndent()
}
