package fi.espoo.vekkuli.views.admin

import org.springframework.stereotype.Service

@Service
class ReportingView {
    fun render(): String {
        // language=HTML
        return """
            <section class="section">
                <div class="container">
                    <h2>Raportit</h2>
                    <a href="/admin/reporting/raw-report">Raakaraportti</a>
                </div>
            </section>
            """.trimIndent()
    }
}
