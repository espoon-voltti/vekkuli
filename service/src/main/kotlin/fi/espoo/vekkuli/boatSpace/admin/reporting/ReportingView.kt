package fi.espoo.vekkuli.boatSpace.admin.reporting

import fi.espoo.vekkuli.DateInputOptions
import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class ReportingView(
    private val formComponents: FormComponents
) : BaseView() {
    fun render(): String {
        val stickerReportStartDateInputField =
            formComponents.dateInputContainer(
                DateInputOptions(
                    id = "reportingDate",
                    labelKey = "reporting.filter.creationDate",
                    value = timeProvider.getCurrentDate().toString(),
                    autoWidth = true
                )
            )

        val boatSpaceReportStartDateInputField =
            formComponents.dateInputContainer(
                DateInputOptions(
                    id = "reportingDate",
                    labelKey = "reporting.filter.reportingDate",
                    value = timeProvider.getCurrentDate().toString(),
                    autoWidth = true
                )
            )

        val freeBoatSpaceReportStartDateInputField =
            formComponents.dateInputContainer(
                DateInputOptions(
                    id = "reportingDate",
                    labelKey = "reporting.filter.reportingDate",
                    value = timeProvider.getCurrentDate().toString(),
                    autoWidth = true
                )
            )

        val reservedBoatSpaceReportStartDateInputField =
            formComponents.dateInputContainer(
                DateInputOptions(
                    id = "reportingDate",
                    labelKey = "reporting.filter.reportingDate",
                    value = timeProvider.getCurrentDate().toString(),
                    autoWidth = true
                )
            )

        val terminatedBoatSpaceReportStartDateInputField =
            formComponents.dateInputContainer(
                DateInputOptions(
                    id = "reportingDate",
                    labelKey = "reporting.filter.reportingDate",
                    value = timeProvider.getCurrentDate().toString(),
                    autoWidth = true
                )
            )

        // language=HTML
        return """
            <section class="section reports-container">
                <h2>Raportit</h2>
                <div class="container">
                    <form id="form"
                          method="get"
                          action="/virkailija/admin/reporting/sticker-report"
                          class="block"
                    >
                        <h2>${t("reporting.stickerReport")}</h2>
                     
                        <p class="reports-info">${t("reporting.stickerReportInfo")}</p>
                        
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
                          action="/virkailija/admin/reporting/boat-space-report"
                          class="block"
                    >
                        <h2>${t("reporting.boatSpaceReport")}</h2>
                        
                        <p class="reports-info">${t("reporting.boatSpaceReportInfo")}</p>
                                                
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
                
                <div class="container">
                    <form id="form"
                          method="get"
                          action="/virkailija/admin/reporting/boat-space-report/free"
                          class="block"
                    >
                        <h2>${t("reporting.freeBoatSpaceReport")}</h2>
                        
                        <p class="reports-info">${t("reporting.freeBoatSpaceReportInfo")}</p>
                                                
                        <div class='columns'>
                            <div class='column'>
                                $freeBoatSpaceReportStartDateInputField
                            </div>
                            <div class='column'>
                                <button id="submit-button"
                                    class="button is-primary"
                                    type="submit"
                                    data-testid="free-boat-space-report-button"
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
                          action="/virkailija/admin/reporting/boat-space-report/reserved"
                          class="block"
                    >
                        <h2>${t("reporting.reservedBoatSpaceReport")}</h2>
                        
                        <p class="reports-info">${t("reporting.reservedBoatSpaceReportInfo")}</p>
                                                
                        <div class='columns'>
                            <div class='column'>
                                $reservedBoatSpaceReportStartDateInputField
                            </div>
                            <div class='column'>
                                <button id="submit-button"
                                    class="button is-primary"
                                    type="submit"
                                    data-testid="reserved-boat-space-report-button"
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
                          action="/virkailija/admin/reporting/boat-space-report/terminated"
                          class="block"
                    >
                        <h2>${t("reporting.terminatedBoatSpaceReport")}</h2>
                        
                        <p class="reports-info">${t("reporting.terminatedBoatSpaceReportInfo")}</p>
                                                
                        <div class='columns'>
                            <div class='column'>
                                $terminatedBoatSpaceReportStartDateInputField
                            </div>
                            <div class='column'>
                                <button id="submit-button"
                                    class="button is-primary"
                                    type="submit"
                                    data-testid="terminated-boat-space-report-button"
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
                          action="/virkailija/admin/reporting/boat-space-report/warnings"
                          class="block"
                    >
                        <h2>${t("reporting.warningsReport")}</h2>
                        
                        <p class="reports-info">${t("reporting.warningsReportInfo")}</p>
                                                
                        <div class='columns'>
                            <div class='column'>
                                <button id="submit-button"
                                    class="button is-primary"
                                    type="submit"
                                    data-testid="warnings-report-button"
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
