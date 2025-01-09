package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.DateInputOptions
import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.RadioOption
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
    private lateinit var modal: Modal

    @Autowired
    private lateinit var timeProvider: TimeProvider

    fun render(
        reserverId: UUID,
        reservation: BoatSpaceReservationDetails
    ): String {
        val today = timeProvider.getCurrentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val formId = "reservation-status-update-form"

        val notPaid = "NotPaid"

        val reservationStatusInput =
            formComponents.radioButtons(
                "citizenDetails.reservationStatus",
                "reservationStatus",
                if (reservation.status == ReservationStatus.Info || reservation.status == ReservationStatus.Payment) {
                    notPaid
                } else {
                    reservation.status.toString()
                },
                listOf(
                    notPaid,
                    ReservationStatus.Invoiced.toString(),
                    ReservationStatus.Confirmed.toString(),
                ).map {
                    RadioOption(if (it == notPaid) ReservationStatus.Info.toString() else it, t("citizenDetails.reservationStatus.$it"))
                }
            )

        val modalBuilder = modal.createModalBuilder()
        return modalBuilder
            .setTitle(t("citizenDetails.updateReservationStatus"))
            .setCloseModalOnPost(true)
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
                    $reservationStatusInput
                    ${formComponents.textInput("citizenDetails.info", "paymentStatusText", "")}
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
