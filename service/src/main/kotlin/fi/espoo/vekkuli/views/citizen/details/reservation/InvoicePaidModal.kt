package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class InvoicePaidModal : BaseView() {
    @Autowired
    private lateinit var formComponents: FormComponents

    @Autowired
    private lateinit var modal: Modal

    fun render(
        citizen: CitizenWithDetails,
        reservation: BoatSpaceReservationDetails
    ): String {
        val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val formId = "invoice-paid-form"
        val modalBuilder = modal.createModalBuilder()
        return modalBuilder
            .setTitle(t("citizenDetails.markInvoicePaid"))
            // language=HTML
            .setContent(
                """
                <form
                    id="$formId"
                    hx-post="/virkailija/venepaikat/varaukset/merkitse-maksu-suoritetuksi" 
                    hx-target="#citizen-details"
                    hx-select="#citizen-details"
                    hx-swap="outerHTML"
                    >
                    ${formComponents.textInput("citizenDetails.info", "invoicePaidInfo", "")}
                    ${formComponents.dateInput("citizenDetails.paymentDate", "paymentDate", today)}
                    <input hidden name="reservationId" value="${reservation.id}" />
                    <input hidden name="citizenId" value="${citizen.id}" />
                </form>
                """.trimIndent()
            )
            .addButton {
                setText(t("cancel"))
                setType(ModalButtonType.Cancel)
                addAttribute("id", "invoice-modal-cancel")
            }
            .addButton {
                addAttribute("id", "invoice-modal-confirm")
                setText(t("confirm"))
                setType(ModalButtonType.Submit)
                setStyle(ModalButtonStyle.Primary)
                setTargetForm(formId)
            }
            .build()
    }
}
