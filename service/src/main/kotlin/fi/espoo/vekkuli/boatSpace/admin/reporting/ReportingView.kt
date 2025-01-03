package fi.espoo.vekkuli.boatSpace.admin.reporting

import fi.espoo.vekkuli.DateInputOptions
import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Service

@Service
class ReportingView(
    private val formComponents: FormComponents,
    private val timeProvider: TimeProvider
) : BaseView() {
    val stickerReportStartDateInputField =
        formComponents.dateInput(
            DateInputOptions(
                id = "startDate",
                labelKey = "reporting.filter.startDate",
                value = timeProvider.getCurrentDate().toString(),
                autoWidth = true
            )
        )

    val boatSpaceReportStartDateInputField =
        formComponents.dateInput(
            DateInputOptions(
                id = "startDate",
                labelKey = "reporting.filter.startDate",
                value = timeProvider.getCurrentDate().toString(),
                autoWidth = true
            )
        )

    fun render(): String {
        // language=HTML
        return """
            <section class="section">
                <h2>Raportit</h2>
                <div class="container">
                    <form id="form"
                          method="get"
                          action="/admin/reporting/sticker-report"
                          class="block"
                    >
                        <h2 class="subtitle">${t("reporting.stickerReport")}</h2>
                     
                        <div class='columns'>
                            <div class='column'>
                                $stickerReportStartDateInputField
                            </div>
                            <div class='column'>
                                <button id="submit-button"
                                    class="button is-primary"
                                    type="submit"
                                    data-testid="fetch-sticker-report-button"
                                >
                                    ${t("reporting.submit")}
                                </button>
                            </div>
                        </div>                                              
                    </form>
                </div>
                
                <div class="container">
                    <form id="form"
                          method="get"
                          action="/admin/reporting/boat-space-report"
                          class="block"
                    >
                        <h2 class="subtitle">${t("reporting.boatSpaceReport")}</h2>
                     
                        <div class='columns'>
                            <div class='column'>
                                $boatSpaceReportStartDateInputField
                            </div>
                            <div class='column'>
                                <button id="submit-button"
                                    class="button is-primary"
                                    type="submit"
                                    data-testid="boat-space-report-button"
                                >
                                    ${t("reporting.submit")}
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
                
                <div class='container'>
                    <h2 class="subtitle">${t("reporting.boatSpaceReport")}</h2>
                
                    <div class='columns'>
                        <div class='column'>
                            <a href="/admin/reporting/raw-report" class="button is-primary">
                                ${t("reporting.rawReport")}
                            </a>
                        </div>
                    </div>
                </div>
            </section>
            """.trimIndent()
    }

    fun render2(): String {
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
