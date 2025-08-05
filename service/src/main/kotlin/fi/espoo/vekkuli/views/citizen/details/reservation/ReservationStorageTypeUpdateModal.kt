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
                            <div class='form-section no-bottom-border' x-data="{ storageType: '${if (storageType?.name !== null) storageType.name else StorageType.Trailer.name}' }">
                                ${storageTypeContainer.render(
                    trailer?.registrationCode,
                    intToDecimal(trailer?.widthCm),
                    intToDecimal(trailer?.lengthCm),
                    storageType,
                    true
                )}
                            </div>
                            <div class="buttons">
                                <button class="button" type="button" x-on:click="isOpen = false" id="reservation-validity-modal-cancel">
                                    ${t("cancel")}
                                </button>
                                <button class="button is-primary" type="submit" id="reservation-validity-modal-confirm" form="reservation-storage-type-modal-form">
                                     ${t("citizenDetails.saveChanges")}
                                </button>
                            </div>
                        <script>
                            validation.init({forms: ['$formId']});
                        </script>
                    </form>
                """.trimIndent()
            ).setIsWide(true)
            .build()
    }
}
