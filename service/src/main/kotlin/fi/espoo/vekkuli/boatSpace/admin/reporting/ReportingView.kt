package fi.espoo.vekkuli.boatSpace.admin.reporting

import org.springframework.stereotype.Service

@Service
class ReportingView {
    fun render(): String {
        // language=HTML
        return """
            <section class="section">
                <h2>Raportit</h2>
                <div class="reports-container">
                    <a href="/admin/reporting/sticker-report">Tarraraportti</a>
                    <a href="/admin/reporting/boat-space-report">Paikkaraportti (kaikki)</a>
                    <a href="/admin/reporting/raw-report">Raakaraportti</a>
                </div>
            </section>
            """.trimIndent()
    }
}
