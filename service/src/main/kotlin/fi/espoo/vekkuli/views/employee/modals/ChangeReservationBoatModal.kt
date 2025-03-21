package fi.espoo.vekkuli.views.employee.modals

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.stereotype.Service

@Service
class ChangeReservationBoatModal(
    private val modal: Modal,
    private val formComponents: FormComponents
) : BaseView() {
    fun render(
        reservation: BoatSpaceReservationDetails,
        boats: List<Boat>
    ): String {
        val formId = "change-reservation-boat-form"
        val modalBuilder = modal.createModalBuilder()
        val boatOptions =
            formComponents.select(
                labelKey = t("boatSpaceReservation.changeBoat.label"),
                id = "change-reservation-boat-select",
                selectedValue = reservation.boat?.id.toString(),
                boats.map { Pair<String, String>(it.id.toString(), it.name ?: "-") },
                required = true,
                name = "boatId",
            )
        return modalBuilder
            .setTitle(t("boatSpaceReservation.changeBoat.title", listOf("""${reservation.locationName} ${reservation.place}""")))
            .setCloseModalOnPost(true)
            .setReloadPageOnClose(true)
            .setForm {
                setId(formId)
                setTestId(formId)
                setAttributes(
                    mapOf(
                        "hx-post" to "/virkailija/venepaikat/varaukset/vaihda-vene"
                    )
                )
            }
            // language=HTML
            .setContent(
                """
                <input hidden name="reservationId" value="${reservation.id}" />
                $boatOptions
                """.trimIndent()
            ).addButton {
                setText(t("cancel"))
                setType(ModalButtonType.Cancel)
                setTestId("change-reservation-boat-cancel")
            }.addButton {
                setStyle(ModalButtonStyle.Danger)
                setType(ModalButtonType.Submit)
                setText(t("boatSpaceReservation.changeBoat.confirm"))
                setTestId("change-reservation-boat-confirm")
            }.build()
    }
}
