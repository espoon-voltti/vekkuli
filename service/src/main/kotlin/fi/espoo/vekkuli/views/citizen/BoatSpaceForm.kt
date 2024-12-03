package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.common.CommonComponents
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

data class BoatFormInput(
    val id: Int,
    val boatName: String,
    val boatType: BoatType,
    val width: BigDecimal?,
    val length: BigDecimal?,
    val depth: BigDecimal?,
    val weight: Int?,
    val boatRegistrationNumber: String,
    val otherIdentification: String,
    val extraInformation: String,
    val ownership: OwnershipStatus,
    val noRegistrationNumber: Boolean,
) {
    companion object {
        fun empty(): BoatFormInput =
            BoatFormInput(
                id = 0,
                boatName = "",
                boatType = BoatType.OutboardMotor,
                width = null,
                length = null,
                depth = null,
                weight = null,
                boatRegistrationNumber = "",
                otherIdentification = "",
                extraInformation = "",
                ownership = OwnershipStatus.Owner,
                noRegistrationNumber = false,
            )
    }
}

@Service
class BoatSpaceForm(
    private val markDownService: MarkDownService,
    private val icons: Icons,
    private val messageUtil: MessageUtil,
    private val formComponents: FormComponents,
    private val sessionTimer: SessionTimer,
    private val stepIndicator: StepIndicator,
    private val commonComponents: CommonComponents
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun boatForm(
        userType: UserType,
        citizen: CitizenWithDetails?,
        boats: List<Boat>,
        reservationId: Int,
        input: BoatFormInput,
    ): String {
        val boatTypes = BoatType.entries.map { it.name }

        fun boatRadioButton(boat: Boat) =
            """
            <div class="radio">
                <input type="radio" id="boat-${boat.id}-radio" value="${boat.id}"
                       hx-trigger="change"
                       hx-get="/${userType.path}/venepaikka/varaus/$reservationId/boat-form?boatId=${boat.id}"
                       hx-target="#boatForm"
                       hx-include="[name='citizenId'],[name='organizationId'],[name='isOrganization']"
                       hx-swap="outerHTML"
                       name="boatId"
                       ${if (input.id == boat.id) "checked" else ""}
                />
                <label for="boat-${boat.id}-radio">${boat.displayName}</label>
            </div>
            """.trimIndent()

        // language=HTML
        val chooseBoatButtons =
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
                       ${if (input.id == 0) "checked" else ""}
                    />
                    <label for="newBoat" >${t("boatApplication.newBoat")}</label>
                </div>
                ${boats.joinToString("\n") { boatRadioButton(it) }}
            </div>
            """
            } else {
                """
                <input type="hidden" name="boatId" value="0">
                """
            }

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
                "boatApplication.boatWidthInMeters",
                "width",
                input.width,
                required = true,
                """
                hx-trigger="change, load" 
                hx-get="/venepaikka/varaus/$reservationId/boat-size-warning" 
                hx-include="#length"
                hx-target="#boat-size-warning"
                """.trimIndent()
            )

        val lengthInput =
            formComponents.decimalInput(
                "boatApplication.boatLengthInMeters",
                "length",
                input.length,
                required = true,
                """
                hx-trigger="change, load" 
                hx-get="/venepaikka/varaus/$reservationId/boat-size-warning" 
                hx-include="#width"
                hx-target="#boat-size-warning"
                """.trimIndent()
            )

        val depthInput =
            formComponents.decimalInput(
                "boatApplication.boatDepthInMeters",
                "depth",
                input.depth,
                required = true,
                step = 0.1
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

        val registrationNumberInput =
            formComponents.textInput(
                "boatSpaceReservation.title.registrationNumber",
                "boatRegistrationNumber",
                input.boatRegistrationNumber,
                required = true
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
        val ownershipOptions = listOf("Owner", "User", "CoOwner", "FutureOwner")
        // language=HTML
        val ownership =
            """
            <div class="field">
                     <h4 class="label required" >${t("boatApplication.ownerShipTitle")}</h4>
                     <div class="control is-flex-direction-row">
                     
                ${
                ownershipOptions.joinToString("\n") { opt ->
                    """
                    <div class="radio">
                        <input
                            type="radio"
                            name="ownership"
                            value="$opt"
                            id="ownership-$opt"
                            ${if (input.ownership.toString() == opt) "checked" else ""}
                        />
                        <label for="ownership-$opt">${t("boatApplication.$userType.ownershipOption.$opt")}</label>
                    </div>
                    """.trimIndent()
                }
            }
                     </div>
                 </div> 
            """.trimIndent()
        // language=HTML
        val registrationNumberContainer =
            """
            <div class="block" x-data="{ noReg: ${input.noRegistrationNumber} }">
                 
                <div class="columns" >
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

        // language=HTML
        val boatContainer =
            """
            <div id="boatForm">       
               <div class="block">
                   <h3 class="header">${t("boatApplication.boatInformation")}</h3>
                   $chooseBoatButtons
               </div>
               
               ${
                commonComponents.boatInformationFields(
                    boatNameInput,
                    boatTypeSelect,
                    widthInput,
                    lengthInput,
                    depthInput,
                    weightInput,
                    registrationNumberContainer,
                    otherIdentifierInput,
                    extraInformationInput,
                    ownership
                )
            }
            </div>
            """.trimIndent()

        return boatContainer
    }

    // language=HTML
    fun errorPage(
        errorText: String,
        step: Int
    ): String =
        """
        <section class="section" id="error-page-container">
         ${stepIndicator.render(step)}
            <div class="container">
                <div class='column is-half'>
                    <h3 >${t("boatApplication.title.errorPage")}</h3>
                    <p >$errorText</p>
                </div>
            </div>
        </section>
        """.trimIndent()

    fun editOrganizationForm(
        org: Organization,
        municipalities: List<Municipality>
    ): String {
        val nameField = formComponents.field("boatApplication.organizationName", "orgName", org.name)
        val businessIdField = formComponents.field("boatApplication.organizationId", "orgBusinessId", org.businessId)
        val municipalityField = formComponents.field("boatApplication.municipality", "orgMunicipality", org.municipalityName)
        val phoneInput = formComponents.textInput("boatApplication.phone", "orgPhone", org.phone, true)
        val emailInput = formComponents.textInput("boatApplication.email", "orgEmail", org.email, true)

        val addressInput =
            formComponents.textInput(
                "boatApplication.address",
                "orgAddress",
                org.streetAddress,
            )

        val postalCodeInput =
            formComponents.textInput(
                "boatApplication.postalCode",
                "orgPostalCode",
                org.postalCode,
            )

        val cityFieldInput =
            formComponents.textInput(
                "boatSpaceReservation.title.city",
                "orgCity",
                org.postOffice,
            )
        // language=HTML
        return """
            <div>
                <div class='columns'>
                    <div class='column is-one-quarter'>
                      $nameField
                    </div>
                    <div class='column is-one-quarter'>
                      $businessIdField
                    </div>
                    <div class='column is-one-quarter'>
                      $municipalityField
                    </div>
                </div>
                <div class='columns'>
                    <div class='column is-one-quarter'>
                      $phoneInput
                    </div>
                    <div class='column is-one-quarter'>
                      $emailInput
                    </div>
                    <div class='column is-one-quarter'>
                      $addressInput
                    </div>
                    <div class='column is-one-eight'>
                       $postalCodeInput
                    </div>
                    <div class='column is-one-eight'>
                      $cityFieldInput
                    </div>
                </div>
            </div>
            """.trimIndent()
    }

    // language=HTML
    fun citizensSearchForm(
        citizens: List<CitizenWithDetails>,
        reservationId: Int
    ): String {
        val listSize = if (citizens.size > 5) 5 else citizens.size
        return (
            """
            <select 
                x-show="citizenFullName != ''" 
                multiple 
                size="$listSize" 
                name='citizenIdOption' 
                hx-get="/virkailija/venepaikka/varaus/$reservationId"  
                hx-include="#form"
                hx-trigger="change" 
                hx-select="#form-inputs"
                hx-target="#form-inputs" @change="updateFullName">
            ${
                citizens.withIndex().joinToString("\n") { (index, citizen) ->
                    """
                    <option id="option-$index" role="option" value="${citizen.id}" 
                        data-fullname="${citizen.fullName}">
                        <p>${citizen.fullName}
                        <span class='is-small'>${citizen.birthday}</span></p>
                    </option>
                    """.trimIndent()
                }
            }
            </select>

            """.trimIndent()
        )
    }

    // language=HTML
    fun citizenDetails(
        citizen: CitizenWithDetails,
        municipalities: List<Municipality>,
    ): String {
        val firstNameField =
            formComponents.field(
                "boatSpaceReservation.title.firstName",
                "firstName",
                citizen.firstName,
            )
        val lastNameField = formComponents.field("boatSpaceReservation.title.lastName", "lastName", citizen.lastName)
        val birthdayField = formComponents.field("boatSpaceReservation.title.birthday", "birthday", citizen.birthday)
        val addressInput =
            formComponents.textInput("boatSpaceReservation.title.address", "address", citizen.streetAddress)
        val postalCodeField =
            formComponents.textInput("boatSpaceReservation.title.postalCode", "postalCode", citizen.postalCode)
        val cityField =
            formComponents.textInput("boatSpaceReservation.title.city", "postalOffice", citizen.municipalityName)
        val emailInput = formComponents.textInput("boatApplication.email", "email", citizen.email, true)
        val phoneInput = formComponents.textInput("boatApplication.phone", "phone", citizen.phone, true)
        val municipalityInput =
            formComponents.select(
                "boatSpaceReservation.title.municipality",
                "municipalityCode",
                citizen.municipalityCode.toString(),
                municipalities.map { Pair(it.code.toString(), it.name) },
                required = true
            )
        return (
            """
             ${
                commonComponents.citizenFields(
                    firstNameField,
                    lastNameField,
                    birthdayField,
                    municipalityInput,
                    phoneInput,
                    emailInput,
                    addressInput,
                    postalCodeField,
                    cityField,
                )
            }
            """.trimIndent()
        )
    }
}
