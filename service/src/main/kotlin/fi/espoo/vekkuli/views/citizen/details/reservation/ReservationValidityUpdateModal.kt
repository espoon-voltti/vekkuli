package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.boatSpace.reservationForm.components.ReservationValidityContainer
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.utils.reservationToText
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ReservationValidityUpdateModal : BaseView() {
    @Autowired
    private lateinit var reservationValidityContainer: ReservationValidityContainer

    @Autowired
    private lateinit var modal: Modal

    fun render(
        reserverId: UUID,
        @SanitizeInput reservation: BoatSpaceReservationDetails
    ): String {
        val formId = "reservation-validity-update-form"
        val reservationValiditySelectorInput = reservationValidityContainer.render(reservation.validity)

        val modalBuilder = modal.createModalBuilder()
        return modalBuilder
            .setTitle(t("citizenDetails.updateReservationValidity"))
            .setCloseModalOnPost(true)
            .setReloadPageOnClose(true)
            // language=HTML
            .setContent(
                """
                <form
                    id="$formId"
                    hx-post="/virkailija/venepaikat/varaukset/tyyppi" 
                    hx-target="#reserver-details"
                    hx-select="#reserver-details"
                    hx-swap="outerHTML"
                    >
                    <div class="form-section" x-data="{ reservationValidity: '${reservation.validity}' }">
                        ${reservationToText(reservation)}
                        $reservationValiditySelectorInput
                        <input hidden name="reservationId" value="${reservation.id}" />
                        <input hidden name="reserverId" value="$reserverId" />
                    </div>
                </form>
                """.trimIndent()
            ).addButton {
                setText(t("cancel"))
                setType(ModalButtonType.Cancel)
                addAttribute("id", "reservation-validity-modal-cancel")
            }.addButton {
                addAttribute("id", "reservation-validity-modal-confirm")
                setText(t("citizenDetails.saveChanges"))
                setType(ModalButtonType.Submit)
                setStyle(ModalButtonStyle.Primary)
                setTargetForm(formId)
            }.build()
    }
}
