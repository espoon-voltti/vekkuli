package fi.espoo.vekkuli.boatSpace.admin.reporting

import fi.espoo.vekkuli.DateInputOptions
import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class ReportingView(
    private val timeProvider: TimeProvider,
    private val formComponents: FormComponents
) : BaseView() {
    fun render(): String {
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
            </section>
            """.trimIndent()
    }
}
