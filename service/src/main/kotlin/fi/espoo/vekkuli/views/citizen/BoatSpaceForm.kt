package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.ReservationInput
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.common.CommonComponents
import org.springframework.stereotype.Service
import java.util.*

data class BoatFormInput(
    val id: Int,
    val boatName: String,
    val boatType: BoatType,
    val width: Double?,
    val length: Double?,
    val depth: Double?,
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
                    hx-trigger="change, intersect" 
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
                hx-trigger="change, intersect" 
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
                hx-trigger="change, intersect" 
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
                    hx-trigger="change, intersect" 
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

    fun boatSpaceForm(
        reservation: ReservationWithDependencies,
        boats: List<Boat>,
        citizen: CitizenWithDetails?,
        organizations: List<Organization>,
        input: ReservationInput,
        reservationTimeInSeconds: Long,
        userType: UserType,
        municipalities: List<Municipality>,
        isNewCustomer: Boolean = true
    ): String {
        val harborField =
            formComponents.field(
                "boatApplication.harbor",
                "harbor",
                reservation.locationName,
            )
        val placeField =
            formComponents.field(
                "boatApplication.place",
                "place",
                reservation.place,
            )
        val boatSpaceTypeField =
            formComponents.field(
                "boatApplication.boatSpaceType",
                "boatSpaceType",
                t("boatSpaces.typeOption.${reservation.type}"),
            )
        val spaceDimensionField =
            formComponents.field(
                "boatApplication.boatSpaceDimensions",
                "boatSpaceDimension",
                if (reservation.amenity != BoatSpaceAmenity.Buoy) {
                    "${reservation.widthCm.cmToM()} m x ${reservation.lengthCm.cmToM()} m"
                } else {
                    ""
                },
            )
        val amenityField =
            formComponents.field(
                "boatApplication.boatSpaceAmenity",
                "boatSpaceAmenity",
                t("boatSpaces.amenityOption.${reservation.amenity}"),
            )

        val reservationTimeField =
            formComponents.field(
                "boatApplication.reservationTime",
                "reservationTime",
                if (reservation.validity === ReservationValidity.FixedTerm) {
                    """<p>${formatAsFullDate(reservation.startDate)} - ${formatAsFullDate(reservation.endDate)}</p>"""
                } else {
                    (
                        """
                    <p>${t("boatApplication.Indefinite")}</p>
                """
                    )
                },
            )
        val priceField =
            formComponents.field(
                "boatApplication.price",
                "price",
                """ <p>${t("boatApplication.boatSpaceFee")}: ${reservation.priceWithoutVatInEuro} &euro;</p>
                <p>${t("boatApplication.boatSpaceFeeAlv")}: ${reservation.vatPriceInEuro} &euro;</p>
                <p>${t("boatApplication.boatSpaceFeeTotal")}: ${reservation.priceInEuro} &euro;</p>""",
            )

        // language=HTML
        val boatSpaceInformation =
            """
                <h3 class="header">${t("boatApplication.boatSpaceInformation")}</h3>
                ${
                commonComponents.reservationInformationFields(
                    harborField,
                    placeField,
                    boatSpaceTypeField,
                    spaceDimensionField,
                    amenityField,
                    reservationTimeField,
                    priceField
                )
            }
            
            """.trimIndent()

        // language=HTML

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

        val firstNameField = formComponents.field("boatApplication.firstName", "firstName", citizen?.firstName)
        val lastNameField = formComponents.field("boatApplication.lastName", "lastName", citizen?.lastName)
        val birthdayField = formComponents.field("boatApplication.birthday", "birthday", citizen?.birthday)
        val municipalityField =
            formComponents.field("boatApplication.municipality", "municipality", citizen?.municipalityName)
        val addressField =
            formComponents.field(
                "boatApplication.address",
                "address",
                "${citizen?.streetAddress}, ${citizen?.postalCode}, ${citizen?.municipalityName}"
            )

        val citizenInformation =
            """     
                ${
                commonComponents.citizenFields(
                    firstNameField,
                    lastNameField,
                    birthdayField,
                    municipalityField,
                    phone,
                    email,
                    addressField
                )
            }
            """.trimIndent()

        val citizenFirstName =
            formComponents.textInput(
                "boatApplication.firstName",
                "firstName",
                input.firstName,
                required = true
            )

        val citizenLastName =
            formComponents.textInput(
                "boatApplication.lastName",
                "lastName",
                input.lastName,
                required = true
            )

        val citizenSsn =
            formComponents.textInput(
                "boatApplication.ssn",
                "ssn",
                input.ssn,
                required = true,
                serverValidate = Pair("/validate/ssn", "validation.uniqueSsn")
            )

        val address =
            formComponents.textInput(
                "boatApplication.address",
                "address",
                input.address
            )

        val postalCode =
            formComponents.textInput(
                "boatApplication.postalCode",
                "postalCode",
                input.postalCode
            )

        val municipalityInput =
            formComponents.select(
                "boatSpaceReservation.title.municipality",
                "municipalityCode",
                input.municipalityCode.toString(),
                municipalities.map { Pair(it.code.toString(), it.name) },
                required = true
            )

        val cityField =
            formComponents.textInput(
                "boatSpaceReservation.title.city",
                "city",
                input.city
            )

        val citizenInputFields =
            """
            <div>
                <h3 class="header">
                    ${t("boatApplication.personalInformation")}
                </h3> 
                ${
                commonComponents.citizenFields(
                    citizenFirstName,
                    citizenLastName,
                    citizenSsn,
                    municipalityInput,
                    email,
                    phone,
                    address,
                    postalCode,
                    cityField,
                )
            }
                
            </div>
            """.trimIndent()

        // language=HTML
        val citizenSearch =
            """
            <div id="citizen-results-container" class="container" >
                <div class="field" id="customer-search-container">
                    <label class="label">${t("boatApplication.select.citizen")}</label>
                    <div class="control width-is-half">
                        <p class="control has-icons-left has-icons-right">
                            <input x-model="citizenFullName" id="customer-search" 
                                placeholder="${t("boatApplication.placeholder.searchCitizens")}"
                                name="nameParameter" class="input search-input" type="text" 
                                hx-get="/virkailija/venepaikka/varaus/${reservation.id}/kuntalainen/hae" hx-trigger="keyup changed delay:500ms" 
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
                        <span id="citizenId-error" class="help is-danger" style="display: none" x-show="citizenId == ''">
                            ${t("validation.required")}
                        </span>
                    </div>
                </div>
                ${ if (citizen != null) citizenDetails(citizen, municipalities) else "" }
            </div>
            """.trimIndent()

        // language=HTML
        val customerTypeRadioButtons =
            """
            <div>
                <div class="field">
                    <div class="control is-flex-direction-row">
                        <div class="radio">
                            <input
                                type="radio"
                                name="citizenSelection"
                                value="newCitizen"
                                id="new-citizen-selector"
                                hx-get="/${userType.path}/venepaikka/varaus/${reservation.id}"
                                hx-include="#form"
                                hx-target="#form-inputs"
                                hx-select="#form-inputs"
                                hx-swap="outerHTML"
                                ${if (input.citizenSelection == "newCitizen") "checked" else ""}
                                
                            />
                            <label for="new-citizen-selector">${t("boatApplication.citizenOptions.newCitizen")}</label>
                        </div>
                        <div class="radio">
                            <input
                                type="radio"
                                name="citizenSelection"
                                value="existingCitizen"
                                id="existing-citizen-selector"
                                hx-get="/${userType.path}/venepaikka/varaus/${reservation.id}"
                                hx-include="#form"
                                hx-target="#form-inputs"
                                hx-select="#form-inputs"
                                hx-swap="outerHTML"
                                ${if (input.citizenSelection == "existingCitizen") "checked" else ""}
                            />
                            <label for="existing-citizen-selector">${t("boatApplication.citizenOptions.existingCitizen")}</label>
                        </div>
                    </div>
                </div> 
                ${if (input.citizenSelection == "newCitizen") citizenInputFields else citizenSearch}
            </div>
            """.trimIndent()

        // language=HTML
        val citizenContainer =
            """
                <h3 class="header">${t("boatApplication.title.reserver")}</h3>
            ${
                if (userType == UserType.CITIZEN) {
                    citizenInformation
                } else {
                    customerTypeRadioButtons
                }
            }
            """

        val wholeLocationName = "${reservation.locationName} ${reservation.place}"
        val slipHolder =
            slipHolderAndBoatForm(
                organizations,
                input.isOrganization ?: false,
                citizen,
                boats,
                input.organizationId,
                userType,
                reservation.id,
                municipalities,
                BoatFormInput(
                    id = input.boatId ?: 0,
                    boatName = input.boatName ?: "",
                    boatType = input.boatType ?: BoatType.OutboardMotor,
                    width = input.width,
                    length = input.length,
                    depth = input.depth,
                    weight = input.weight,
                    boatRegistrationNumber = input.boatRegistrationNumber ?: "",
                    otherIdentification = input.otherIdentification ?: "",
                    extraInformation = input.extraInformation ?: "",
                    ownership = input.ownership ?: OwnershipStatus.Owner,
                    noRegistrationNumber = input.noRegistrationNumber ?: false,
                )
            )

        // language=HTML
        return (
            """
            <section class="section">
                <div class="container" id="container" x-data='{modalOpen: false, citizenFullName: "", citizenId:"", updateFullName(event) {
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
                    <div class="container">
                        <button x-on:click="modalOpen = true" class="icon-text">
                            <span class="icon">
                                <div>${icons.chevronLeft}</div>
                            </span>
                            <span >${t("boatSpaces.goBack")}</span>
                        </button>
                    </div> 
                    ${stepIndicator.render(2)}
                    ${sessionTimer.render(reservationTimeInSeconds)}
                    <form
                        id="form"
                        class="column"
                        hx-post="/${userType.path}/venepaikka/varaus/${reservation.id}"
                        hx-target="body"
                        hx-disabled-elt="button[type='submit']"
                        novalidate>
                         <h1 class="title pb-l" id='boat-space-form-header'>
                            ${t("boatApplication.title.reservation")} 
                            $wholeLocationName
                        </h1>
                        <div id="form-inputs" class="block">
                            <div class='form-section'>
                            $citizenContainer  
                            $slipHolder
                            </div>
                       
                             <div class='form-section'>
                            $boatSpaceInformation
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
                                        <span id="certifyInformation-error" class="help is-danger" style="display: none">
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
                                        <span id="agreeToRules-error" class="help is-danger" style="display: none">
                                        ${t("validation.agreeToRules")}</span>
                                    </div>
                                </div>
                            </div>
                            <div class="warning block form-validation-message" id="validation-warning" style="display: none">
                                <span class="icon">${icons.warningExclamation(false)}</span>
                                <span class="p-l">${t("boatApplication.validationWarning")}</span>
                            </div> 
                        </div >
                        <div class="field block">
                            <div class="control">
                                <button id="cancel"
                                    class="button is-secondary"
                                    type="button"
                                    x-on:click="modalOpen = true">
                                    ${t("boatApplication.cancelReservation")}
                                </button>
                                <button id="submit-button"
                                    class="button is-primary"
                                    type="submit">
                                    ${t("boatApplication.$userType.continueToPaymentButton")}
                                </button>
                            </div>
                        </div>             
                    </form>
                    
                    <script>
                        validation.init({forms: ['form']})
                        window.addEventListener('load', function() {
                            if (!document.getElementById("width").value && !document.getElementById("length").value) {
                                const type = localStorage.getItem('type');
                                if (type) {
                                    document.getElementById("boatType").value = type;
                                }
                                const width = localStorage.getItem('width');
                                if (width) {
                                    document.getElementById('width').value = width;
                                }
                                const length = localStorage.getItem('length');
                                if (length) {
                                    document.getElementById('length').value = length;
                                }
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
        )
    }

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

    fun newOrganizationForm(municipalities: List<Municipality>): String {
        val nameInput =
            formComponents.textInput(
                "boatApplication.organizationName",
                "orgName",
                null,
                required = true
            )

        //language=HTML
        val businessIdInput =
            """
            <div class="field">
                <div class="control">
                    <label class="label required" for="orgBusinessId" >${t("boatApplication.organizationId")}</label>
                    <input
                        class="input"
                        data-required
                        data-validate-url="/validate/businessid"
                        type="text"
                        id="orgBusinessId"
                        name="orgBusinessId" />
                   
                    <div id="orgBusinessId-error-container">
                        <span id="orgBusinessId-error" class="help is-danger" style="display: none">
                            ${t("validation.required")}
                        </span>
                    </div>
                </div>
            </div>
            """.trimIndent()

        val municipalityInput =
            formComponents.select(
                "boatSpaceReservation.title.municipality",
                "orgMunicipalityCode",
                null,
                municipalities.map { Pair(it.code.toString(), it.name) },
                required = true
            )

        val phoneInput = formComponents.textInput("boatApplication.phone", "orgPhone", null, true)

        val emailInput = formComponents.textInput("boatApplication.email", "orgEmail", null, true)

        val addressInput =
            formComponents.textInput(
                "boatApplication.address",
                "orgAddress",
                null,
            )

        val postalCodeInput =
            formComponents.textInput(
                "boatApplication.postalCode",
                "orgPostalCode",
                null,
            )

        val cityFieldInput =
            formComponents.textInput(
                "boatSpaceReservation.title.city",
                "orgCity",
                null,
            )
        // language=HTML
        return """
            <div>
                <div class='columns'>
                    <div class='column is-one-quarter'>
                        $nameInput
                    </div>
                    <div class='column is-one-quarter'>
                        $businessIdInput
                    </div>
                    <div class='column is-one-quarter'>
                         $municipalityInput
                    </div>
                </div>
                <div id="orgBusinessId-server-error" class="block" style="display: none"></div>
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

    fun slipHolderAndBoatForm(
        organizations: List<Organization>,
        isOrganization: Boolean,
        citizen: CitizenWithDetails?,
        boats: List<Boat>,
        selectedOrganizationId: UUID?,
        userType: UserType,
        reservationId: Int,
        municipalities: List<Municipality>,
        boatData: BoatFormInput,
    ): String =
        """
        <div id="shipHolderAndBoatForm">
           ${slipHolder(organizations, isOrganization, selectedOrganizationId, userType, reservationId, municipalities)}
           ${boatForm(userType, citizen, boats, reservationId, boatData)}
        </div>
        """.trimIndent()

    fun slipHolder(
        organizations: List<Organization>,
        isOrganization: Boolean,
        selectedOrganizationId: UUID?,
        userType: UserType,
        reservationId: Int,
        municipalities: List<Municipality>
    ): String {
        fun organizationRadioButton(org: Organization) =
            """
            <div class="radio">
                <input type="radio" id="org-${org.id}-radio" value="${org.id}" 
                   name="organizationId"
                   hx-trigger="change"
                   hx-get="/${userType.path}/venepaikka/varaus/$reservationId"
                   hx-include="#form"
                   hx-target="#form-inputs"
                   hx-select="#form-inputs"
                   hx-swap="outerHTML"
                   ${if (selectedOrganizationId == org.id) "checked" else ""}
                />
                <label for="${org.id}">${org.name}</label>
            </div>
            """.trimIndent()

        val organizationSelect =
            if (organizations.isNotEmpty()) {
                """
            <div class="field" style="margin-left: 32px">
                ${organizations.joinToString("\n") { organizationRadioButton(it) }}
                <div class="radio">
                    <input type="radio" 
                        id="newOrg" 
                        name="organizationId"
                        value=""
                        hx-trigger="change"
                        hx-get="/${userType.path}/venepaikka/varaus/$reservationId"
                        hx-include="#form"
                        hx-target="#form-inputs"
                        hx-select="#form-inputs"
                        hx-swap="outerHTML"
                       ${if (selectedOrganizationId == null) "checked" else ""}
                    />
                    <label for="newOrg" >${t("boatApplication.newOrg")}</label>
                </div>
            </div>
            """
            } else {
                ""
            }

        val organizationForm =
            if (isOrganization && selectedOrganizationId == null) {
                newOrganizationForm(municipalities)
            } else if (isOrganization) {
                val org = organizations.find { it.id == selectedOrganizationId }
                if (org == null) throw IllegalArgumentException("Organization not found")
                editOrganizationForm(org, municipalities)
            } else {
                ""
            }

        val reserverType =
            // language=HTML
            """
            <input type="hidden" name="citizenId" x-bind:value="`${'$'}{citizenId}`"/>
            <div class="field" id="slipHolder">
                <div class="radio">
                    <input type="radio" 
                        id="reseverTypePrivate" 
                        name="isOrganization"
                        value="false"
                        hx-trigger="change"
                        hx-get="/${userType.path}/venepaikka/varaus/$reservationId"
                        hx-include="#form"
                        hx-target="#form-inputs"
                        hx-select="#form-inputs"
                        hx-swap="outerHTML"
                       ${if (!isOrganization) "checked" else ""}
                    />
                    <label for="reseverTypePrivate" >${t("boatApplication.reserverType.private")}</label>
                </div>
                <div class="radio">
                    <input type="radio" 
                        id="reseverTypeOrg" 
                        name="isOrganization"
                        value="true"
                        hx-trigger="change"
                        hx-get="/${userType.path}/venepaikka/varaus/$reservationId"
                        hx-include="#form"
                        hx-target="#form-inputs"
                        hx-select="#form-inputs"
                        hx-swap="outerHTML"
                       ${if (isOrganization) "checked" else ""}
                    />
                    <label for="reseverTypeOrg" >${t("boatApplication.reserverType.organization")}</label>
                </div>
                ${if (isOrganization) organizationSelect else ""}
                $organizationForm
            </div>   
            """.trimIndent()

        return reserverType
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
