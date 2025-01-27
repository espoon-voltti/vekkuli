package fi.espoo.vekkuli.boatSpace.admin

import fi.espoo.vekkuli.boatSpace.admin.reporting.ReportingView
import org.springframework.stereotype.Service

@Service("AdminDashboardView")
class DashboardView(
    private val reportingView: ReportingView,
) {
    fun render(): String {
        // language=HTML
        return """
            ${reportingView.render()}
            """.trimIndent()
    }
}
