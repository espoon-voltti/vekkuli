package fi.espoo.vekkuli.views.employee.modals

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.ReserverWithDetails
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.stereotype.Service

@Service
class AddBoatToReserverModal(
    private val modal: Modal,
    private val formComponents: FormComponents
) : BaseView() {
    fun render(
        reserver: ReserverWithDetails,
    ): String {
        val formId = "add-boat-for-reserver-form"
        val modalBuilder = modal.createModalBuilder()

        return modalBuilder
            .setTitle(t("boatSpaceReservation.addBoat.title"))
            .setCloseModalOnPost(true)
            .setReloadPageOnClose(true)
            .setForm {
                setId(formId)
                setTestId(formId)
                setAttributes(
                    mapOf(
                        "hx-post" to "/virkailija/kayttaja/${reserver.id}/vene/uusi"
                    )
                )
            }
            // language=HTML
            .setContent(buildFormContent(formId)).addButton {
                setText(t("cancel"))
                setType(ModalButtonType.Cancel)
                setTestId("reservation-add-boat-cancel")
            }.addButton {
                setStyle(ModalButtonStyle.Danger)
                setType(ModalButtonType.Submit)
                setText(t("boatSpaceReservation.addBoat.confirm"))
                setTestId("reservation-add-boat-confirm")
            }.build()
    }

    private fun buildFormContent(formId: String): String{
        val boatTypes = BoatType.entries.map { it.name }
        val ownershipOptionValues = listOf("Owner", "User", "CoOwner", "FutureOwner")

        val nameInput =
            formComponents.textInput(
                labelKey = "boatSpaceReservation.title.boatName",
                id = "add-boat-name",
                name = "name",
                value = "",
                required = true,
            )

        val weightInput =
            formComponents.numberInput(
                labelKey = "boatApplication.boatWeightInKg",
                id = "weight",
                value = null,
                required = true,
            )

        val boatTypeSelect =
            formComponents.select(
                labelKey = "boatApplication.boatType",
                id = "type",
                selectedValue = null,
                options = boatTypes.map { it to t("boatApplication.boatTypeOption.$it") },
                required = true,
            )

        val depthInput =
            formComponents.decimalInput(
                labelKey = "boatApplication.boatDepthInMeters",
                id = "depth",
                value = null,
                required = true,
                min = 0.1
            )

        val widthInput =
            formComponents.decimalInput(
                labelKey =  "shared.label.widthInMeters",
                id = "width",
                value = null,
                required = true,
                min = 0.1
            )

        val registrationNumberInput =
            formComponents.textInput(
                labelKey = "boatSpaceReservation.title.registrationNumber",
                id = "add-boat-registration-number",
                name = "registrationNumber",
                value = "",
            )

        val lengthInput =
            formComponents.decimalInput(
                labelKey = "shared.label.lengthInMeters",
                id = "length",
                value = null,
                required = true,
                min = 0.1
            )

        val ownershipSelect =
            formComponents.select(
               labelKey =  "boatSpaceReservation.title.ownershipStatus",
                id = "ownership",
                selectedValue = null,
                options = ownershipOptionValues.map { it to formComponents.t("boatApplication.EMPLOYEE.ownershipOption.$it") },
                required = true,
            )

        val otherIdentifierInput =
            formComponents.textInput(
                labelKey = "boatSpaceReservation.title.otherIdentifier",
                name = "otherIdentifier",
                id = "add-boat-other-identifier",
                value = ""
            )

        val extraInformationInput =
            formComponents.textInput(
                labelKey = "boatSpaceReservation.title.additionalInfo",
                name = "extraInformation",
                id = "add-boat-extra-information",
                value = "",
            )

        // language=HTML
        return """
            <div class='columns mb-xl'>
                <div class='column'>
                    $nameInput
                </div>
            </div>
            <div class='columns mb-xl'>
                <div class='column is-half'>
                    $boatTypeSelect
                </div>
                <div class='column is-half'>
                   $ownershipSelect
                </div>
            </div>
            <div class="columns mb-xl">
                <div class="column is-one-quarter">
                    $widthInput
                </div>
                <div class="column is-one-quarter">
                  $lengthInput
                </div>
                 <div class="column is-one-quarter">
                    $depthInput
                </div>
                <div class="column is-one-quarter">
                    $weightInput
                </div>
             </div>
             <div class="columns mb-xl">
                <div class='column is-half' >
                   $registrationNumberInput
                </div>
                <div class='column is-half'>
                    $otherIdentifierInput
                </div>
            </div>
            <div class='columns mb-xl'>
                <div class='column'>
                   $extraInformationInput
                </div>
            </div>
           <script>
                validation.init({forms: ['${formId}']});
            </script>
        """.trimIndent()
    }
}
