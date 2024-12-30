package fi.espoo.vekkuli.boatSpace.admin.reporting

import org.springframework.stereotype.Service

@Service
class ReportingView {
    fun render(): String {
        // language=HTML
        return """
            <section class="section">
                <div class="container">
                    <h2>Raportit</h2>
                    <a href="/admin/reporting/sticker-report">Tarraraportti</a>
                    <br/>
                    <br/>
                    <a href="/admin/reporting/raw-report">Raakaraportti</a>
                </div>
            </section>
            """.trimIndent()
    }
}
