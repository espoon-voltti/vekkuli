package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.boatSpace.reservationForm.BoatFormInput
import fi.espoo.vekkuli.boatSpace.reservationForm.BoatFormParams
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.common.CommonComponents
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.stereotype.Component

// language=HTML
@Component
class BoatForm(
    private val commonComponents: CommonComponents,
    private val formComponents: FormComponents
) : BaseView() {
    // language=HTML
    fun boatRadioButton(
        userType: UserType,
        reservationId: Int,
        boatId: Int,
        boat: Boat
    ) = """
        <div class="radio">
            <input type="radio" id="boat-${boat.id}-radio" value="${boat.id}"
                   hx-trigger="change"
                   hx-get="/${userType.path}/venepaikka/varaus/$reservationId/boat-form?boatId=${boat.id}"
                   hx-target="#boatForm"
                   hx-include="[name='citizenId'],[name='organizationId'],[name='isOrganization']"
                   hx-swap="outerHTML"
                   name="boatId"
                   ${if (boatId == boat.id) "checked" else ""}
            />
            <label for="boat-${boat.id}-radio">${boat.displayName}</label>
        </div>
        """.trimIndent()

    // language=HTML
    fun chooseBoatButtons(
        userType: UserType,
        citizen: CitizenWithDetails?,
        boats: List<Boat>,
        reservationId: Int,
        boatId: Int
    ) = // language=HTML
        if (citizen !== null && boats.isNotEmpty()) {
            """
            <div id="boatOptions" class="field" x-data="{ initialWidth: localStorage.getItem('width'), 
                                         initialLength: localStorage.getItem('length'), 
                                         initialType: localStorage.getItem('type') }" >
                <div class="radio">
                    <input type="radio" 
                        id="newBoat" 
                        name="boatId"
                        value="0"
                        hx-trigger="change"
                        x-bind:hx-get="`/${userType.path}/venepaikka/varaus/$reservationId/boat-form?boatId=0&width=${'$'}{initialWidth}&length=${'$'}{initialLength}&width=${'$'}{initialWidth}&type=${'$'}{initialType}`"
                        hx-include="[name='citizenId'],[name='organizationId'],[name='isOrganization']"
                        hx-target="#boatForm"
                        hx-swap="outerHTML"
                       ${if (boatId == 0) "checked" else ""}
                    />
                    <label for="newBoat" >${t("boatApplication.newBoat")}</label>
                </div>
                ${boats.joinToString("\n") { boatRadioButton(userType, reservationId, boatId, it) }}
            </div>
            """
        } else {
            """
                <input type="hidden" name="boatId" value="0">
                """
        }

    // language=HTML
    fun ownership(
        userType: UserType,
        ownerShip: OwnershipStatus
    ): String {
        val ownershipOptionValues = listOf("Owner", "User", "CoOwner", "FutureOwner")
        val ownerShipOptions =
            ownershipOptionValues.joinToString("\n") { opt ->
                """
                <div class="radio">
                    <input
                        type="radio"
                        name="ownership"
                        value="$opt"
                        id="ownership-$opt"
                        ${if (ownerShip.toString() == opt) "checked" else ""}
                    />
                    <label for="ownership-$opt">${t("boatApplication.$userType.ownershipOption.$opt")}</label>
                </div>
                """.trimIndent()
            }

        return """
            <div class="field">                                                                                                                                                       
                <h4 class="label required" >${t("boatApplication.ownerShipTitle")}</h4>
                <div class="control is-flex-direction-row">
                    $ownerShipOptions
                </div> 
            </div> 
            """.trimIndent()
    }

    // language=HTML
    fun registrationNumberContainer(input: BoatFormInput): String {
        val registrationNumberInput =
            formComponents.textInput(
                "boatSpaceReservation.title.registrationNumber",
                "boatRegistrationNumber",
                input.boatRegistrationNumber,
                required = true
            )

        return (
            """
            <div class="block" x-data="{ noReg: ${input.noRegistrationNumber} }">
                 
                <div class="columns is-vcentered" >
                    <template x-if="!noReg">
                         <div class="column">
                             $registrationNumberInput
                         </div>
                    </template>
                    <div class="column">
                        <label class="checkbox">
                             <input type="checkbox" 
                                     name="noRegistrationNumber" 
                                     id="noRegistrationNumber" 
                                     @click="noReg = !noReg"
                                     ${if (input.noRegistrationNumber == true) "checked" else ""}
                                     />
                             <span>${t("boatApplication.noRegistrationNumber")}</span>
                        </label> 
                    </div>
                </div>
            </div>
            """.trimIndent()
        )
    }

    fun boatForm(params: BoatFormParams): String {
        val (userType, citizen, boats, reservationId, input) = params
        val boatTypes = BoatType.entries.map { it.name }

        val boatTypeSelect =
            formComponents.select(
                "boatApplication.boatType",
                "boatType",
                input.boatType.toString(),
                boatTypes.map { it to formComponents.t("boatApplication.boatTypeOption.$it") },
                attributes =
                    """
                    hx-trigger="change, load" 
                    hx-get="/venepaikka/varaus/$reservationId/boat-type-warning" 
                    hx-target="#boat-type-warning"
                    """.trimIndent()
            )

        val widthInput =
            formComponents.decimalInput(
                "shared.label.widthInMeters",
                "width",
                input.width,
                required = true,
                attributes =
                    """
                    hx-trigger="change, load" 
                    hx-get="/venepaikka/varaus/$reservationId/boat-size-warning" 
                    hx-include="#length"
                    hx-target="#boat-size-warning"
                    """.trimIndent(),
                min = 0.1
            )

        val lengthInput =
            formComponents.decimalInput(
                "shared.label.lengthInMeters",
                "length",
                input.length,
                required = true,
                attributes =
                    """
                    hx-trigger="change, load" 
                    hx-get="/venepaikka/varaus/$reservationId/boat-size-warning" 
                    hx-include="#width"
                    hx-target="#boat-size-warning"
                    """.trimIndent(),
                min = 0.1
            )

        val depthInput =
            formComponents.decimalInput(
                "boatApplication.boatDepthInMeters",
                "depth",
                input.depth,
                required = true,
                step = 0.1,
                min = 0.0
            )

        val weightInput =
            formComponents.numberInput(
                "boatApplication.boatWeightInKg",
                "weight",
                input.weight,
                required = true,
                attributes =
                    """
                    hx-trigger="change, load" 
                    hx-get="/venepaikka/varaus/$reservationId/boat-weight-warning" 
                    hx-target="#boat-weight-warning"
                    """.trimIndent()
            )

        val boatNameInput =
            formComponents.textInput(
                "boatSpaceReservation.title.boatName",
                "boatName",
                input.boatName,
            )

        val otherIdentifierInput =
            formComponents.textInput(
                "boatSpaceReservation.title.otherIdentifier",
                "otherIdentification",
                input.otherIdentification,
                required = true
            )

        val extraInformationInput =
            formComponents.textInput(
                "boatSpaceReservation.title.additionalInfo",
                "extraInformation",
                input.extraInformation,
            )

        // language=HTML
        return(
            """
                <div id="boatForm">       
                   <div class="block">
                       <h3 class="header">${t("boatApplication.boatInformation")}</h3>
                       ${chooseBoatButtons(userType, citizen, boats, reservationId, input.id)}
                   </div>
                   
                   ${
                commonComponents.boatInformationFields(
                    boatNameInput,
                    boatTypeSelect,
                    widthInput,
                    lengthInput,
                    depthInput,
                    weightInput,
                    registrationNumberContainer(input),
                    otherIdentifierInput,
                    extraInformationInput,
                    ownership(userType, input.ownership)
                )
            }
                </div>
            """.trimIndent()
        )
    }

    fun render(@SanitizeInput params: BoatFormParams): String {
        val boatForm = """ ${
            boatForm(
                BoatFormParams(
                    params.userType,
                    params.citizen,
                    params.boats,
                    params.reservationId,
                    BoatFormInput(
                        id = params.input.id ?: 0,
                        boatName = params.input.boatName ?: "",
                        boatType = params.input.boatType ?: BoatType.OutboardMotor,
                        width = params.input.width,
                        length = params.input.length,
                        depth = params.input.depth,
                        weight = params.input.weight,
                        boatRegistrationNumber = params.input.boatRegistrationNumber ?: "",
                        otherIdentification = params.input.otherIdentification ?: "",
                        extraInformation = params.input.extraInformation ?: "",
                        ownership = params.input.ownership ?: OwnershipStatus.Owner,
                        noRegistrationNumber = params.input.noRegistrationNumber ?: false,
                    )
                )
            )
        }"""

        // language=HTML
        return """
            <div id="boatForm">
              $boatForm
            </div>
            """.trimIndent()
    }
}
