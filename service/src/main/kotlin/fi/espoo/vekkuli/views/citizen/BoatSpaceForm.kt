package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.ReservationInput
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Municipality
import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.views.Icons
import org.springframework.stereotype.Service

@Service
class BoatSpaceForm(
    private val markDownService: MarkDownService,
    private val icons: Icons,
    private val messageUtil: MessageUtil,
    private val formComponents: FormComponents,
    private val sessionTimer: SessionTimer,
    private val stepIndicator: StepIndicator
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun boatSpaceForm(
        reservation: ReservationWithDependencies,
        boats: List<Boat>,
        citizen: CitizenWithDetails?,
        input: ReservationInput,
        reservationTimeInSeconds: Long,
        userType: UserType,
        municipalities: List<Municipality>,
    ): String {
        val boatTypes = listOf("Rowboat", "OutboardMotor", "InboardMotor", "Sailboat", "JetSki")
        // language=HTML
        val boatSpaceInformation =
            """
            <div class="block">
                <h3 id="boat-space-form-header" class="header">${t("boatApplication.boatSpaceToApply")}</h3>
                <p>${reservation.locationName}</p>
                <p>${t("boatApplication.boatSpaceSection")} ${reservation.section}</p>
                <p>${t("boatApplication.boatSpacePlace")} ${reservation.section}${reservation.placeNumber}</p>
                <p>${reservation.widthCm.cmToM()} m x ${reservation.lengthCm.cmToM()} m</p>
                <p>${t("boatSpaces.amenityOption.${reservation.amenity}")}</p>
            </div>
            <div class="block">
                <h4 class="label">${t("boatApplication.boatSpacePrice")}</h4>
                <p>${t("boatApplication.boatSpaceFee")}: ${reservation.priceWithoutAlvInEuro} &euro;</p>
                <p>${t("boatApplication.boatSpaceFeeAlv")}: ${reservation.alvPriceInEuro} &euro;</p>
                <p>${t("boatApplication.boatSpaceFeeTotal")}: ${reservation.priceInEuro} &euro;</p>
            </div>
            """.trimIndent()

        // language=HTML
        fun boatRadioButton(boat: Boat) =
            """
            <div class="radio">
                <input type="radio" id="boat-${boat.id}-radio" value="${boat.id}"
                       hx-trigger="change"
                       hx-get="/${userType.path}/venepaikka/varaus/${reservation.id}?boatId=${boat.id}"
                       hx-target="body"
                       hx-swap="outerHTML"
                       name="boatId"
                       ${if (input.boatId == boat.id) "checked" else ""}
                />
                <label for="${boat.id}">${boat.displayName}</label>
            </div>
            """.trimIndent()

        // language=HTML
        val boatRadioButtons =
            if (citizen !== null) {
                """
            <div class="field" ">
                <div class="radio">
                    <input type="radio" 
                        id="newBoat" 
                        name="boatId"
                        value="0"
                        hx-trigger="change"
                        hx-get="/${userType.path}/venepaikka/varaus/${reservation.id}"
                        hx-target="body"
                        hx-swap="outerHTML"
                       ${if (input.boatId == 0) "checked" else ""}
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
                boatTypes.first(),
                boatTypes.map { it to formComponents.t("boatApplication.boatTypeOption.$it") },
                attributes =
                    """
                    hx-trigger='change' 
                    hx-get='/venepaikka/varaus/${reservation.id}/boat-type-warning' 
                    hx-target='#boat-type-warning'
                    hx-sync='closest #form:replace'
                    """.trimIndent()
            )

        val widthInput =
            formComponents.decimalInput(
                "boatApplication.boatWidthInMeters",
                "width",
                input.width,
                required = true,
                """
                hx-trigger='change' 
                hx-get='/venepaikka/varaus/${reservation.id}/boat-size-warning' 
                hx-include="#length"
                hx-target='#boat-size-warning'
                """.trimIndent()
            )

        val lengthInput =
            formComponents.decimalInput(
                "boatApplication.boatLengthInMeters",
                "length",
                input.length,
                required = true,
                """
                hx-trigger='change' 
                hx-get='/venepaikka/varaus/${reservation.id}/boat-size-warning' 
                hx-include="#width"
                hx-target='#boat-size-warning'
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
                    hx-trigger='change' 
                    hx-get='/venepaikka/varaus/${reservation.id}/boat-weight-warning' 
                    hx-target='#boat-weight-warning'
                    hx-sync='closest #form:replace'
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
                     
                ${ownershipOptions.joinToString("\n") { opt ->
                """
                <div class="radio">
                    <input
                        type="radio"
                        name="ownership"
                        value="$opt"
                        id="ownership-$opt"
                        ${if (input.ownership.toString() == opt) "checked" else ""}
                    />
                    <label for="ownership-$opt">${t("boatApplication.ownershipOption.$opt")}</label>
                </div>
                """.trimIndent()
            }
            }
                     </div>
                 </div> 
            """.trimIndent()

        val email =
            formComponents.textInput(
                "boatApplication.email",
                "email",
                input.email,
                true,
                pattern = Pair(".+@.+\\..+", "validation.email")
            )

        val phone =
            formComponents.textInput(
                "boatApplication.phone",
                "phone",
                input.phone,
                required = true
            )

        val citizenInformation =
            """
            <div class='block'
                <h4 class="label">
                    ${t("boatApplication.personalInformation")}
                </h4> 
                <div class="field">
                    <p>${citizen?.firstName} ${citizen?.lastName}</p>
                    <p>${citizen?.nationalId}</p>
                    <p>${citizen?.streetAddress}</p>
                    <p>${citizen?.postalCode} ${citizen?.municipalityName}</p>
                </div>
            </div>
            <div class="block">
                $email
                $phone
            </div> 
            """.trimIndent()

        val citizenFirstName =
            formComponents.textInput(
                "boatApplication.firstName",
                "firstName",
                citizen?.firstName ?: "",
                required = true
            )

        val citizenLastName =
            formComponents.textInput(
                "boatApplication.lastName",
                "lastName",
                citizen?.lastName ?: "",
                required = true
            )

        val citizenSsn =
            formComponents.textInput(
                "boatApplication.ssn",
                "ssn",
                citizen?.nationalId ?: "",
                required = true,
                serverValidate = Pair("/validate/ssn", "validation.uniqueSsn")
            )

        val address =
            formComponents.textInput(
                "boatApplication.address",
                "address",
                citizen?.streetAddress ?: "",
                required = true
            )

        val postalCode =
            formComponents.textInput(
                "boatApplication.postalCode",
                "postalCode",
                citizen?.postalCode ?: "",
                required = true
            )

        val municipalityInput =
            formComponents.select(
                "boatSpaceReservation.title.municipality",
                "municipalityCode",
                citizen?.municipalityCode.toString(),
                municipalities.map { Pair(it.code.toString(), it.name) },
                required = true
            )

        val citizenInputFields =
            """
            <div>
                <h3 class="header">
                    ${t("boatApplication.personalInformation")}
                </h3> 
                <div class="block">
                    $citizenFirstName
                </div>
                <div class="block">
                    $citizenLastName
                </div>
                <div class="block">
                    $citizenSsn
                </div>
                <div class="block">
                    $address
                </div>
                <div class="block">
                    $postalCode
                </div>
                <div class="block">
                    $municipalityInput
                </div>
                <div class="block">
                 $email
                 </div
                 <div class="block">
                $phone
                </div>
            </div>
            """.trimIndent()

        // language=HTML
        val citizenSearch =
            """
            <div id="citizen-results-container" class="container" 
                x-data='{citizenFullName: "", citizenId:"", updateFullName(event) {
                    const selectElement = event.target;
                    if (selectElement.selectedOptions.length > 0) {
                        const selectedOption = selectElement.selectedOptions[0];
                        this.citizenFullName = selectedOption.dataset.fullname;
                        this.citizenId = selectedOption.value;
                    } else {
                        this.citizenFullName = "";
                        this.citizenId = "";
                    };
                }}'>
                <div class="field" id="customer-search-container">
                    <label class="label">${t("boatApplication.select.citizen")}</label>
                    <div class="control width-is-half">
                        <p class="control has-icons-left has-icons-right">
                            <input x-model="citizenFullName" id="customer-search" 
                                placeholder="${t("boatApplication.placeholder.searchCitizens")}"
                                name="nameParameter" class="input search-input" type="text" 
                                hx-get="/virkailija/venepaikka/varaus/kuntalainen/hae" hx-trigger="keyup changed delay:500ms" 
                                hx-target="#citizen-results">
                            <span class="icon is-small is-left">
                                ${icons.search}
                            </span>
                            <span id="citizen-empty-input" x-show="citizenFullName != ''" class="icon is-small is-right is-clickable p-s" @click="citizenFullName = ''; citizenId = ''">
                                ${icons.xMark}
                            </span>
                        </p>
                               
                        <!-- Where the results will be displayed -->                    
                        <div id="citizen-results" class="select is-multiple" ></div>                   
                    </div>
                    <input id="citizenId" name="citizenId" x-model.fill="citizenId" data-required hidden />
                    <div id="citizenId-error-container">
                        <span id="citizenId-error" class="help is-danger" style="visibility: hidden" x-show="citizenId == ''">
                            ${t("validation.required")}
                        </span>
                    </div>
                </div>
                
                
                
                <template x-if="citizenFullName != ''">
                    <div id='citizen-details' class="block">
                    
                    </div>
                </template>
            </div>
            """.trimIndent()

        // language=HTML
        val customerTypeRadioButtons =
            """
            <div x-data='{citizenSelection: "newCitizen"}'>
                <div class="field">
                    <h4 class="label required" >${t("boatApplication.title.citizenType")}</h4>
                    <div class="control is-flex-direction-row">
                        <div class="radio">
                            <input
                                x-model="citizenSelection"
                                type="radio"
                                name="citizenSelection"
                                value="newCitizen"
                                id="new-citizen-selector"
                            />
                            <label for="newCitizen">${t("boatApplication.citizenOptions.newCitizen")}</label>
                        </div>
                        <div class="radio">
                            <input
                                x-model="citizenSelection"
                                type="radio"
                                name="citizenSelection"
                                value="existingCitizen"
                                id="existing-citizen-selector"
                            />
                            <label for="existingCitizen">${t("boatApplication.citizenOptions.existingCitizen")}</label>
                        </div>
                    </div>
                </div> 
                <template x-if="citizenSelection === 'newCitizen'">
                    $citizenInputFields
                </template>
                <template x-if="citizenSelection === 'existingCitizen'">
                    <div x-init="htmx.process(document.getElementById('citizen-results-container'))" class="block"> $citizenSearch</div>
                </template>
            </div>
            """.trimIndent()

        // language=HTML
        val citizenContainer =
            if (userType == UserType.CITIZEN) {
                citizenInformation
            } else {
                customerTypeRadioButtons
            }

        // language=HTML
        return """
            <section class="section">
                <div class="container" id="container" x-data="{ modalOpen: false }"> 
                    <div class="container">
                        <button x-on:click="modalOpen = true" class="icon-text">
                            <span class="icon">
                                <div></div>
                            </span>
                            <span >${t("boatSpaces.goBack")}</span>
                        </button>
                    </div> 
                    ${stepIndicator.render(2)}
                    ${sessionTimer.render(reservationTimeInSeconds)}
                    <form
                        id="form"
                        class="column"
                        action="/${userType.path}/venepaikka/varaus/${reservation.id}"
                        method="post"
                        novalidate>
                        
                        $boatSpaceInformation
                        $citizenContainer  
                        
                        <div class="block">
                            <h3 class="header">${t("boatApplication.boatInformation")}</h3>
                            $boatRadioButtons
                        </div>
                       
                        $boatTypeSelect
                        <div id="boat-type-warning" class="block"></div>
                        <div class="block">
                            <div class="columns">
                                <div class="column">
                                    $lengthInput
                                </div>
                                <div class="column">
                                    $widthInput
                                </div>
                            </div>
                            
                            <div id="boat-size-warning" class="block">
                            </div>
                        
                            <div class="columns">
                                <div class="column">
                                    $depthInput
                                </div>
                                <div class="column">
                                    $weightInput
                                </div>
                            </div>
                            
                            <div id="boat-weight-warning" class="block" >
                            </div>
                        </div>
                        
                        <div class="block" x-data="{ noReg: ${input.noRegistrationNumber} }">
                            
                           $boatNameInput
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
                                                ${ if (input.noRegistrationNumber == true) "checked" else "" }
                                                />
                                        <span>${t("boatApplication.noRegistrationNumber")}</span>
                                   </label> 
                               </div>
                           </div>
                           $otherIdentifierInput
                           $extraInformationInput
                           $ownership
                        </div>

                        <div class="block">
                            <div id="certify-control">
                                <label class="checkbox">
                                    <input
                                        type="checkbox"
                                        data-required
                                        id="certifyInformation"
                                        name="certifyInformation"
                                    >
                                    <span >${t("boatApplication.certifyInfoCheckbox")}</span>
                                </label>
                                <div id="certify-error-container">
                                    <span id="certifyInformation-error" class="help is-danger" style="visibility: hidden">
                                    ${t("validation.certifyInformation")}</span>
                                </div>
                            </div>
                            <div id="agree-control">
                                <label class="checkbox">
                                    <input
                                        type="checkbox"
                                        data-required
                                        id="agreeToRules"
                                        name="agreeToRules"
                                    />
                                    <span> ${markDownService.render(t("boatApplication.agreementCheckbox"))} </span>
                                </label>
                                <div id="agree-error-container">
                                    <span id="agreeToRules-error" class="help is-danger" style="visibility: hidden">
                                    ${t("validation.agreeToRules")}</span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="field block">
                            <div class="control">
                                <button id="cancel"
                                    class="button is-secondary"
                                    type="button"
                                    x-on:click="modalOpen = true">
                                    ${t("boatApplication.cancelReservation")}
                                </button>
                                <button id="submit"
                                    class="button is-primary"
                                    type="submit">
                                    ${t("boatApplication.continueToPaymentButton")}
                                </button>
                            </div>
                        </div> 
                    </form>
                    
                    <script>
                        validation.init({forms: ['form']})
                        window.addEventListener('load', function() {
                            const type = localStorage.getItem('type');
                            if (type) {
                              document.getElementById("boatType").value = type;
                              localStorage.removeItem('type');
                            }
                            const width = localStorage.getItem('width');
                            if (width) {
                              document.getElementById('width').value = width;
                              localStorage.removeItem('width');
                            }
                            const length = localStorage.getItem('length');
                            if (length) {
                              document.getElementById('length').value = length;
                              localStorage.removeItem('length');
                            }
                        });
                            
                    </script>
                    
                    <div id="confirm-cancel-modal" class="modal" x-show="modalOpen" style="display:none;" >
                        <div class="modal-underlay" @click="modalOpen = false"></div>
                        <div class="modal-content">
                            <p class="block has-text-left">${t("boatSpaceApplication.cancelConfirmation")}</p>
                            <p class="block has-text-left" ${t("boatSpaceApplication.cancelConfirmation2")}</p>
                            <button id="confirm-cancel-modal-cancel"
                                class="button"
                                x-on:click="modalOpen = false"
                                type="button">
                                ${t("cancel")}
                            </button>
                            <button id="confirm-cancel-modal-confirm"
                                class="button is-primary"
                                type="button"
                                hx-delete="/${userType.path}/venepaikka/varaus/${reservation.id}"
                                hx-on-htmx-after-request="window.location = '/kuntalainen/venepaikat';">
                                ${t("confirm")}
                            </button>
                        </div>
                    </div>
                    
                </div>
            </section>
            """.trimIndent()
    }

    fun boatTypeWarning() =
        """
        <div class="warning" id="boatSize-warning">
            <p class="block">${t("boatSpaceApplication.boatTypeWarning")}</p>
            <button class="icon-text"
                    type="button"
                    id="size-warning-back-button"
                    x-on:click="modalOpen = true">
                <span class="icon">
                    ${icons.chevronLeft}
                </span>
                <span>${t("boatSpaces.goBack")}</span>
            </button>
        </div>
        """.trimIndent()

    fun boatWeightWarning() =
        """
        <div class="warning" id="boatSize-warning">
            <p class="block">${t("boatSpaceApplication.boatWeightWarning")}</p>
            <button class="icon-text"
                    type="button"
                    id="size-warning-back-button"
                    x-on:click="modalOpen = true">
                <span class="icon">
                    ${icons.chevronLeft}
                </span>
                <span>${t("boatSpaces.goBack")}</span>
            </button>
        </div>
        """.trimIndent()

    fun boatSizeWarning() =
        """
        <div class="warning" id="boatSize-warning">
            <p class="block">${t("boatSpaceApplication.boatSizeWarning")}</p>
            <p class="block">${t("boatSpaceApplication.boatSizeWarningExplanation")}</p>
            <button class="icon-text"
                    type="button"
                    id="size-warning-back-button"
                    x-on:click="modalOpen = true">
                <span class="icon">
                    ${icons.chevronLeft}
                </span>
                <span>${t("boatSpaces.goBack")}</span>
            </button>
        </div>
        """.trimIndent()

    // language=HTML
    fun citizensSearchForm(citizens: List<CitizenWithDetails>): String {
        val listSize = if (citizens.size > 5) 5 else citizens.size

        return (
            """
            <select x-show="citizenFullName != ''" multiple size="$listSize" name='citizenIdOption' hx-get="/virkailija/venepaikka/varaus/kuntalainen"  
                hx-trigger="change" hx-target="#citizen-details" @change="updateFullName">
            ${
                citizens.joinToString("\n") { citizen ->
                    """
                    <option id="option-${citizen.id}" role="option" value="${citizen.id}" 
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
    fun citizenDetails(citizen: CitizenWithDetails): String {
        val firstNameField =
            formComponents.field(
                "boatSpaceReservation.title.firstName",
                "firstName",
                citizen.firstName,
            )
        val lastNameField = formComponents.field("boatSpaceReservation.title.lastName", "lastName", citizen.lastName)
        val birthdayField = formComponents.field("boatSpaceReservation.title.birthday", "birthday", citizen.birthday)
        val addressInput = formComponents.textInput("boatSpaceReservation.title.address", "address", citizen.streetAddress)
        val postalCodeField =
            formComponents.textInput("boatSpaceReservation.title.postalCode", "postalCode", citizen.postalCode)
        val cityField = formComponents.textInput("boatSpaceReservation.title.city", "postalOffice", citizen.municipalityName)
        val emailInput = formComponents.textInput("boatApplication.email", "email", citizen.email, true)
        val phoneInput = formComponents.textInput("boatApplication.phone", "phone", citizen.phone, true)

        return (
            """
             <div class="columns">
                <div class="column">
                    $firstNameField
                </div>
                <div class="column">
                    $lastNameField
                </div>
                <div class="column">
                    $birthdayField
                </div>
                <div class="column">
                    $addressInput
                </div>
             </div>
             <div class="columns">
                <div class="column">
                    $postalCodeField
                </div>
                <div class="column">
                    $cityField
                </div>
                <div class="column">
                    $emailInput
                </div>
                <div class="column">
                    $phoneInput
                </div>
            </div>
            """.trimIndent()
        )
    }
}
