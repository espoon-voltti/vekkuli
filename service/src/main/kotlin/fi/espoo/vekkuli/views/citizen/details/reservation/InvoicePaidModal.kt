package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.DateInputOptions
import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter
import java.util.UUID

@Component
class InvoicePaidModal : BaseView() {
    @Autowired
    private lateinit var formComponents: FormComponents

    @Autowired
    private lateinit var modal: Modal

    @Autowired
    private lateinit var timeProvider: TimeProvider

    fun render(
        reserverId: UUID,
        reservation: BoatSpaceReservationDetails
    ): String {
        val today = timeProvider.getCurrentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val formId = "invoice-paid-form"
        val modalBuilder = modal.createModalBuilder()
        return modalBuilder
            .setTitle(t("citizenDetails.markInvoicePaid"))
            .setCloseModalOnPost(true)
            // language=HTML
            .setContent(
                """
                <form
                    id="$formId"
                    hx-post="/virkailija/venepaikat/varaukset/merkitse-maksu-suoritetuksi" 
                    hx-target="#reserver-details"
                    hx-select="#reserver-details"
                    hx-swap="outerHTML"
                    >
                    ${formComponents.textInput("citizenDetails.info", "invoicePaidInfo", "")}
                    ${formComponents.dateInput(
                    DateInputOptions(
                        labelKey = "citizenDetails.paymentDate",
                        id = "paymentDate",
                        value = today
                    )
                )}
                    <input hidden name="reservationId" value="${reservation.id}" />
                    <input hidden name="reserverId" value="$reserverId" />
                </form>
                """.trimIndent()
            ).addButton {
                setText(t("cancel"))
                setType(ModalButtonType.Cancel)
                addAttribute("id", "invoice-modal-cancel")
            }.addButton {
                addAttribute("id", "invoice-modal-confirm")
                setText(t("confirm"))
                setType(ModalButtonType.Submit)
                setStyle(ModalButtonStyle.Primary)
                setTargetForm(formId)
            }.build()
    }
}
