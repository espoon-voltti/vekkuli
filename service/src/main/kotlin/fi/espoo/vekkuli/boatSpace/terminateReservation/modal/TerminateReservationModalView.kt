package fi.espoo.vekkuli.boatSpace.terminateReservation.modal

import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TerminateReservationModalView : BaseView() {
    @Autowired
    private lateinit var modal: Modal

    fun render(reservation: BoatSpaceReservationDetails): String {
        val formId = "terminate-reservation-form"
        val modalBuilder = modal.createModalBuilder()
        return modalBuilder
            .setTitle(t("boatSpaceTermination.title"))
            .setForm {
                setId(formId)
                setTestId(formId)
                setAttributes(
                    mapOf(
                        "hx-post" to "/boat-space/terminate-reservation",
                        "hx-swap" to "innerHTML",
                        "hx-target" to "#modal-container"
                    )
                )
            }
            // language=HTML
            .setContent(
                """
                <div class='columns is-multiline'>
                    <div class="column is-full">
                        <ul class="no-bullets">
                            <li ${addTestId("terminate-reservation-location")}>${reservation.locationName} ${reservation.place}</li>
                            <li ${addTestId(
                    "terminate-reservation-size"
                )}>${reservation.boatSpaceWidthInM} x ${reservation.boatSpaceLengthInM} m</li>
                            <li ${addTestId(
                    "terminate-reservation-amenity"
                )}>${t("boatSpaces.amenityOption.${reservation.amenity}")}</li>
                        </ul>
                    </div>
                    <div class="column is-full">
                        <p>${t("boatSpaceTermination.messages.moveBoatImmediately")}</p>
                    </div>
                    <div class="column is-full">
                        <p>${t("boatSpaceTermination.messages.notEntitledToRefund")}</p>
                    </div>
                </div>
                <input hidden name="reservationId" value="${reservation.id}" />
                """.trimIndent()
            ).addButton {
                setText(t("cancel"))
                setType(ModalButtonType.Cancel)
                setTestId("terminate-reservation-modal-cancel")
            }.addButton {
                setStyle(ModalButtonStyle.Danger)
                setType(ModalButtonType.Submit)
                setText(t("boatSpaceTermination.button.confirm"))
                setTestId("terminate-reservation-modal-confirm")
            }.build()
    }
}
