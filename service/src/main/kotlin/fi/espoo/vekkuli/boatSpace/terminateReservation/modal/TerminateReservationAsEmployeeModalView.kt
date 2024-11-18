package fi.espoo.vekkuli.boatSpace.terminateReservation.modal

import fi.espoo.vekkuli.DateInputOptions
import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.TextAreaOptions
import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReasonOptions
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.stereotype.Service

@Service
class TerminateReservationAsEmployeeModalView(
    private var formComponents: FormComponents,
    private var modal: Modal,
    private var timeProvider: TimeProvider
) : BaseView() {
    fun render(reservation: BoatSpaceReservationDetails): String {
        val formId = "terminate-reservation-employee-form"
        val modalBuilder = modal.createModalBuilder()

        val endDateField =
            formComponents.dateInput(
                DateInputOptions(
                    id = "endDate",
                    labelKey = "boatSpaceTermination.fields.endDate",
                    value = timeProvider.getCurrentDate().toString(),
                    autoWidth = true
                )
            )

        val terminationReasonOptions: List<Pair<String, String>> =
            enumValues<ReservationTerminationReasonOptions>()
                .map { it.name to t("""boatSpaceReservation.terminateReason.${it.name.replaceFirstChar { char -> char.lowercase() }}""") }

        val reasonField =
            formComponents.select(
                "boatSpaceTermination.fields.reason",
                "terminationReason",
                null,
                terminationReasonOptions,
                true,
                "",
                true,
                t("boatSpaceTermination.fields.selectReason"),
            )
        val explanationField =
            formComponents.textArea(
                TextAreaOptions(
                    labelKey = "boatSpaceTermination.fields.explanation",
                    id = "termination-explanation",
                    name = "explanation",
                    rows = 2,
                )
            )

        return modalBuilder
            .setTitle(t("boatSpaceTermination.title"))
            // language=HTML
            .setContent(
                """
                <form
                    id="$formId"
                    ${addTestId(formId)}
                    hx-post="/boat-space/terminate-reservation/as-employee"
                    hx-swap="innerHTML"
                    hx-target="#modal-container"
                    xmlns="http://www.w3.org/1999/html">
                    <div class='columns is-multiline'>
                        <div class="column is-full">
                            <ul class="no-bullets">
                                <li ${addTestId("terminate-reservation-location")}>
                                    ${reservation.locationName} ${reservation.place}
                                </li>
                                <li ${addTestId("terminate-reservation-size")}>
                                    ${reservation.boatSpaceWidthInM} x ${reservation.boatSpaceLengthInM} m
                                </li>
                                <li ${addTestId("terminate-reservation-amenity")}>
                                    ${t("boatSpaces.amenityOption.${reservation.amenity}")}
                                </li>
                            </ul>
                        </div>
                        <div class="column is-full">
                            $endDateField
                        
                            $reasonField
                        
                            $explanationField
                        </div>
                    </div>
                    <input hidden name="reservationId" value="${reservation.id}" />
                </form>
                """.trimIndent()
            ).addButton {
                setText(t("cancel"))
                setType(ModalButtonType.Cancel)
                setTestId("terminate-reservation-modal-cancel")
            }.addButton {
                setStyle(ModalButtonStyle.Danger)
                setType(ModalButtonType.Submit)
                setText(t("boatSpaceTermination.button.confirm"))
                setTargetForm(formId)
                setTestId("terminate-reservation-modal-confirm")
            }.build()
    }
}
