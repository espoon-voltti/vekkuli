package fi.espoo.vekkuli.views.components

import fi.espoo.vekkuli.views.Icons
import org.springframework.stereotype.Component

enum class AlertLevel {
    GeneralWarning,
    SystemWarning,
    Error
}

@Component
class WarningBox(
    private val icons: Icons
) {
    fun render(content: String, alertLevel: AlertLevel = AlertLevel.SystemWarning): String =
        """
        <div class="ack-info">
            <div class="info-icon">${icons.warningExclamation(alertLevel)}</div>
            <div class="info-content">
                $content
            </div>
        </div>
        """.trimIndent()
}
