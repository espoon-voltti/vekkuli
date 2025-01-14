package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.DateInputOptions
import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.boatSpace.reservationForm.components.ReservationStatusContainer
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter
import java.util.UUID

@Component
class ReservationStatusUpdateModal : BaseView() {
    @Autowired
    private lateinit var formComponents: FormComponents

    @Autowired
    private lateinit var reservationStatusContainer: ReservationStatusContainer

    @Autowired
    private lateinit var modal: Modal

    @Autowired
    private lateinit var timeProvider: TimeProvider

    fun render(
        reserverId: UUID,
        reservation: BoatSpaceReservationDetails
    ): String {
        val today = timeProvider.getCurrentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val formId = "reservation-status-update-form"
        val reservationStatusInput = reservationStatusContainer.render(reservation, reservation.status)

        val defaultDate =
            when (reservation.status) {
                ReservationStatus.Confirmed ->
                    reservation.paymentDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        ?: today
                ReservationStatus.Invoiced ->
                    reservation.invoiceDueDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        ?: today
                else -> today
            }

        val modalBuilder = modal.createModalBuilder()
        return modalBuilder
            .setTitle(t("citizenDetails.updateReservationStatus"))
            .setCloseModalOnPost(true)
            .setReloadPageOnClose(true)
            // language=HTML
            .setContent(
                """
                    <form
                        id="$formId"
                        hx-post="/virkailija/venepaikat/varaukset/status" 
                        hx-target="#reserver-details"
                        hx-select="#reserver-details"
                        hx-swap="outerHTML"
                        >
                        <div class="form-section" x-data="{ reservationStatus: '${reservation.status}' }">
                            $reservationStatusInput
                            <div x-show="reservationStatus === 'Invoiced' || reservationStatus === 'Confirmed'" >
                                ${formComponents.textInput(
                    "citizenDetails.reservationStatus.infoText",
                    "paymentStatusText",
                    reservation.paymentReference ?: ""
                )}
                                ${formComponents.dateInput(
                    DateInputOptions(
                        labelKey = if (reservation.status == ReservationStatus.Invoiced) "citizenDetails.dueDate" else "citizenDetails.paymentDate",
                        id = "paymentDate",
                        value = defaultDate
                    )
                )}
                            </div>
                            <input hidden name="reservationId" value="${reservation.id}" />
                            <input hidden name="reserverId" value="$reserverId" />
                        </div>
                    </form>
                """.trimIndent()
            ).addButton {
                setText(t("cancel"))
                setType(ModalButtonType.Cancel)
                addAttribute("id", "invoice-modal-cancel")
            }.addButton {
                addAttribute("id", "invoice-modal-confirm")
                setText(t("citizenDetails.saveChanges"))
                setType(ModalButtonType.Submit)
                setStyle(ModalButtonStyle.Primary)
                setTargetForm(formId)
            }.build()
    }
}
