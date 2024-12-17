package fi.espoo.vekkuli.views

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class EditBoat {
    @Autowired
    lateinit var formComponents: FormComponents

    fun editBoatForm(
        @SanitizeInput boat: CitizenUserController.BoatUpdateForm,
        errors: Map<String, String>,
        citizenId: UUID,
        boatTypes: List<String>,
        ownershipOptions: List<String>,
        userType: UserType,
    ): String {
        val nameInput =
            formComponents.textInput(
                "boatSpaceReservation.title.boatName",
                "name",
                boat.name,
            )

        val weightInput =
            formComponents.numberInput(
                "boatApplication.boatWeightInKg",
                "weight",
                boat.weight,
                required = true,
            )

        val boatTypeSelect =
            formComponents.select(
                "boatApplication.boatType",
                "type",
                boat.type.toString(),
                boatTypes.map { it to formComponents.t("boatApplication.boatTypeOption.$it") },
            )

        val boatDepthInput =
            formComponents.decimalInput(
                "boatApplication.boatDepthInMeters",
                "depth",
                boat.depth,
                required = true,
            )

        val widthInput =
            formComponents.decimalInput(
                "shared.label.widthInMeters",
                "width",
                boat.width,
                required = true,
            )

        val registrationNumberInput =
            formComponents.textInput(
                "boatSpaceReservation.title.registrationNumber",
                "registrationNumber",
                boat.registrationNumber,
            )

        val lengthInput =
            formComponents.decimalInput(
                "shared.label.lengthInMeters",
                "length",
                boat.length,
                required = true,
            )

        val ownershipSelect =
            formComponents.select(
                "boatSpaceReservation.title.ownershipStatus",
                "ownership",
                boat.ownership.toString(),
                ownershipOptions.map { it to formComponents.t("boatApplication.$userType.ownershipOption.$it") },
                required = true,
            )

        val otherIdentifierInput =
            formComponents.textInput(
                "boatSpaceReservation.title.otherIdentifier",
                "otherIdentifier",
                boat.otherIdentifier,
            )

        val extraInformationInput =
            formComponents.textInput(
                "boatSpaceReservation.title.additionalInfo",
                "extraInformation",
                boat.extraInformation,
            )

        val editUrl =
            if (userType == UserType.EMPLOYEE) {
                "$citizenId/vene/${boat.id}"
            } else {
                "/kuntalainen/vene/${boat.id}"
            }

        val cancelUrl =
            if (userType == UserType.EMPLOYEE) {
                "$citizenId"
            } else {
                "/kuntalainen/omat-tiedot"
            }
        //language=HTML
        return """
            <form id="form"
                  method="post" 
                  hx-patch="$editUrl"
                  novalidate
                  hx-target="#reserver-details"
                  hx-select="#reserver-details"
                  hx-swap="outerHTML"
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
                            hx-get="$cancelUrl"
                            hx-target="#reserver-details"
                            hx-select="#reserver-details"
                            hx-swap="outerHTML"
                    >${formComponents.t("cancel")}</button>
                    <button
                            id="submit-button"
                            class="button is-primary"
                            type="submit"
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
