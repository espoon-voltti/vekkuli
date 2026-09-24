package fi.espoo.vekkuli.boatSpace.reservationEndDate

import fi.espoo.vekkuli.DateInputOptions
import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.utils.formatAsTestDate
import fi.espoo.vekkuli.utils.reservationToText
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.WarningBox
import fi.espoo.vekkuli.views.components.modal.Modal
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.UUID

const val END_DATE_FORM_ID = "reservation-end-date-form"
const val END_DATE_VALIDATION_ID = "end-date-validation"

@Component
class ReservationEndDateModalView(
    private val modal: Modal,
    private val formComponents: FormComponents,
    private val warningBox: WarningBox,
    private val validationView: ReservationEndDateValidationView
) : BaseView() {
    fun render(
        reserverId: UUID,
        reservation: ReservationWithDependencies,
        selectedEndDate: LocalDate,
        validation: EndDateValidationResult
    ): String {
        val endDateInput =
            formComponents.dateInputContainer(
                DateInputOptions(
                    id = "endDate",
                    labelKey = "reservationEndDate.field.endDate",
                    value = formatAsTestDate(selectedEndDate),
                    required = true,
                    autoWidth = true,
                    attributes =
                        """
                        hx-post="/virkailija/venepaikat/varaukset/loppupaiva/tarkista"
                        hx-trigger="change"
                        hx-include="closest form"
                        hx-target="#$END_DATE_VALIDATION_ID"
                        hx-swap="innerHTML"
                        """.trimIndent()
                )
            )

        // language=HTML
        return modal
            .createModalBuilder()
            .setTitle(t("reservationEndDate.modal.title"))
            .setContent(
                """
                <form
                    id="$END_DATE_FORM_ID"
                    hx-post="/virkailija/venepaikat/varaukset/loppupaiva"
                    hx-target="#modal-container"
                    hx-swap="innerHTML"
                    >
                    <div class="form-section no-bottom-border">
                        ${warningBox.render(t("reservationEndDate.instructions"))}
                        ${formComponents.field(
                    "reservationEndDate.field.currentRange",
                    "currentRange",
                    "${reservationToText(reservation)}, ${formatAsFullDate(reservation.startDate)} - ${formatAsFullDate(
                        reservation.endDate
                    )}"
                )}
                        $endDateInput
                        <input hidden name="reservationId" value="${reservation.id}" />
                        <input hidden name="reserverId" value="$reserverId" />
                    </div>
                    <div id="$END_DATE_VALIDATION_ID">
                        ${validationView.render(validation, END_DATE_FORM_ID)}
                    </div>
                    <script>
                        validation.init({forms: ['$END_DATE_FORM_ID']});
                    </script>
                </form>
                """.trimIndent()
            ).setIsWide(true)
            .build()
    }
}
