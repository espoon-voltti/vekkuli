package fi.espoo.vekkuli.boatSpace.reservationEndDate

import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.WarningBox
import org.springframework.stereotype.Component

@Component
class ReservationEndDateValidationView(
    private val warningBox: WarningBox
) : BaseView() {
    fun render(
        validation: EndDateValidationResult,
        formId: String
    ): String {
        // language=HTML
        return """
            ${validation.error?.let { renderError(it) } ?: ""}
            ${validation.warnings.joinToString("\n") { renderWarning(it) }}
            <div class="buttons">
                <button class="button" type="button" x-on:click="isOpen = false" id="reservation-end-date-modal-cancel">
                    ${t("cancel")}
                </button>
                <button
                    class="button is-primary"
                    type="submit"
                    id="reservation-end-date-modal-confirm"
                    form="$formId"
                    ${if (validation.isValid) "" else "disabled"}>
                    ${t("citizenDetails.saveChanges")}
                </button>
            </div>
            """.trimIndent()
    }

    private fun renderError(error: EndDateError): String {
        val message =
            when (error) {
                is EndDateError.Missing -> t("reservationEndDate.error.missing")
                is EndDateError.BeforeStartDate -> t("reservationEndDate.error.beforeStartDate")
                is EndDateError.OverlapsAnotherReservation ->
                    t(
                        "reservationEndDate.error.overlap",
                        listOf(
                            "${formatAsFullDate(error.conflict.startDate)} - ${formatAsFullDate(error.conflict.endDate)}"
                        )
                    )
            }
        // language=HTML
        return """<p class="help is-danger" ${addTestId("reservation-end-date-error")}>$message</p>"""
    }

    private fun renderWarning(warning: EndDateWarning): String {
        val message =
            when (warning) {
                is EndDateWarning.FreesThePlaceEarlier -> t("reservationEndDate.warning.shortening")
                is EndDateWarning.EndsInThePast -> t("reservationEndDate.warning.inThePast")
                is EndDateWarning.PendingReservationOnSameSpace ->
                    t(
                        "reservationEndDate.warning.pendingReservation",
                        listOf(
                            "${formatAsFullDate(warning.pending.startDate)} - ${formatAsFullDate(warning.pending.endDate)}"
                        )
                    )
            }
        // language=HTML
        return """<div ${addTestId("reservation-end-date-warning")}>${warningBox.render(message)}</div>"""
    }
}
