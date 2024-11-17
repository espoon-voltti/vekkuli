package fi.espoo.vekkuli.boatSpace.admin

import fi.espoo.vekkuli.boatSpace.admin.reporting.ReportingView
import fi.espoo.vekkuli.boatSpace.admin.systemTime.SetCurrentSystemTimeView
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SettingsView(
    private val setCurrentSystemTimeView: SetCurrentSystemTimeView,
    private val reportingView: ReportingView
) {
    fun render(time: LocalDateTime): String {
        // language=HTML
        return """
            ${setCurrentSystemTimeView.render(time)}
            ${reportingView.render()}
            """.trimIndent()
    }
}
