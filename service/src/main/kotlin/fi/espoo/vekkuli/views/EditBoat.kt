package fi.espoo.vekkuli.views

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.controllers.CitizenUserController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class EditBoat {
    @Autowired
    lateinit var formComponents: FormComponents

    fun editBoatForm(
        boat: CitizenUserController.BoatUpdateForm,
        errors: Map<String, String>,
        citizenId: UUID,
        boatTypes: List<String>,
        ownershipOptions: List<String>
    ): String {
        val nameInput =
            formComponents.textInput(
                "boatSpaceReservation.title.boatName",
                "name",
                boat.name,
                errors,
                false,
                null,
            )

        val weightInput =
            formComponents.numberInput(
                "boatApplication.boatWeightInKg",
                "weight",
                boat.weight,
                required = false,
                errors = errors
            )

        val boatTypeSelect =
            formComponents.select(
                "boatApplication.boatType",
                "type",
                boat.type.toString(),
                boatTypes.map { it to formComponents.t("boatApplication.boatTypeOption.$it") },
                errors = errors
            )

        val boatDepthInput =
            formComponents.decimalInput(
                "boatApplication.boatDepthInMeters",
                "depth",
                boat.depth,
                required = true,
                errors = errors
            )

        val widthInput =
            formComponents.decimalInput(
                "boatApplication.boatWidthInMeters",
                "width",
                boat.width,
                required = true,
                errors = errors
            )

        val registrationNumberInput =
            formComponents.textInput(
                "boatSpaceReservation.title.registrationNumber",
                "registrationNumber",
                boat.registrationNumber,
                required = false,
                pattern = null,
                errors = errors
            )

        val lengthInput =
            formComponents.decimalInput(
                "boatApplication.boatLengthInMeters",
                "length",
                boat.length,
                required = true,
                errors = errors
            )

        val ownershipSelect =
            formComponents.select(
                "boatSpaceReservation.title.ownershipStatus",
                "ownership",
                boat.ownership.toString(),
                ownershipOptions.map { it to formComponents.t("boatApplication.ownershipOption.$it") },
                required = true,
                errors = errors
            )

        val otherIdentifierInput =
            formComponents.textInput(
                "boatSpaceReservation.title.otherIdentifier",
                "otherIdentifier",
                boat.otherIdentifier,
                required = false,
                pattern = null,
                errors = errors
            )

        val extraInformationInput =
            formComponents.textInput(
                "boatSpaceReservation.title.additionalInfo",
                "extraInformation",
                boat.extraInformation,
                required = false,
                pattern = null,
                errors = errors
            )

        //language=HTML
        return """
            <form id="form"
                  method="post" 
                  hx-patch="/virkailija/kayttaja/$citizenId/vene/${boat.id}"
                  novalidate
            >
                <input type="hidden" name="id" value="${boat.id}" />
                <div class="columns is-vcentered">
                    <div class="column is-narrow ">
                        <h4>${formComponents.t("citizenDetails.boat")} : ${boat.name}</h4>
                    </div>
                </div>
                
                <div class="columns">
                    <div class="column">
                        $nameInput
                        $weightInput
                    </div>
                    <div class="column">
                        $boatTypeSelect
                        $boatDepthInput
                    </div>
                    <div class="column">
                        $widthInput
                        $registrationNumberInput
                    </div>
                    <div class="column">
                        $lengthInput
                        $ownershipSelect
                    </div>
                </div>
                
                <div class="columns">
                    <div class="column">
                        $otherIdentifierInput
                    </div>
                    <div class="column">
                        $extraInformationInput
                    </div>
                    <div class="column"></div>
                    <div class="column"></div>
                </div>
                
                <div class="buttons">
                    <button
                            id="cancel"
                            class="button"
                            type="button"
                            hx-get="/virkailija/kayttaja/$citizenId"
                            hx-target="#citizen-details"
                            hx-select="#citizen-details"
                    >${formComponents.t("cancel")}</button>
                    <button
                            id="submit"
                            class="button is-primary"
                            type="submit"
                            hx-target="#citizen-details"
                            hx-select="#citizen-details"
                    >${formComponents.t("citizenDetails.saveChanges")}</button>
                </div>
            </form>
            <script>
                validation.init({forms: ['form']})
            </script>
            <div class="edit-buttons" hx-swap-oob="outerHTML:.edit-buttons"></div>
            """.trimIndent()
    }
}
