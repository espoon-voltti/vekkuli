package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.boatSpace.reservationForm.components.StorageTypeContainer
import fi.espoo.vekkuli.domain.StorageType
import fi.espoo.vekkuli.domain.Trailer
import fi.espoo.vekkuli.utils.intToDecimal
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ReservationStorageTypeUpdateModal : BaseView() {
    @Autowired
    private lateinit var storageTypeContainer: StorageTypeContainer

    @Autowired
    private lateinit var modal: Modal

    fun render(
        reserverId: UUID,
        reservationId: Int,
        storageType: StorageType? = null,
        trailer: Trailer? = null
    ): String {
        val modalBuilder = modal.createModalBuilder()
        val formId = "reservation-storage-type-modal-form"
        return modalBuilder
            .setTitle(t("citizenDetails.updateStorageType"))
            .setReloadPageAfterPost(true)
            // language=HTML
            .setContent(
                """
                    <form
                        id="$formId"
                        hx-post="/virkailija/venepaikat/varaukset/varustetyyppi" 
                        hx-target="#reserver-details"
                        hx-select="#reserver-details"
                        hx-swap="outerHTML"
                        >
                        <input hidden name="reservationId" value="$reservationId" />
                        <input hidden name="reserverId" value="$reserverId" />
                            <div class='form-section' x-data="{ storageType: '${if (storageType?.name !== null) storageType.name else StorageType.Trailer.name}' }">
                                ${storageTypeContainer.render(
                    trailer?.registrationCode,
                    intToDecimal(trailer?.widthCm),
                    intToDecimal(trailer?.lengthCm),
                    storageType,
                    true
                )}
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
            }.setIsWide(true)
            .build()
    }
}
