package fi.espoo.vekkuli.boatSpace.reservationForm

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.boatSpace.reservationForm.components.CitizenContainer
import fi.espoo.vekkuli.boatSpace.reservationForm.components.SlipHolder
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.citizen.SessionTimer
import fi.espoo.vekkuli.views.citizen.StepIndicator
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

data class BoatFormParams(
    val userType: UserType,
    val citizen: CitizenWithDetails?,
    val boats: List<Boat>,
    val reservationId: Int,
    val input: BoatFormInput,
)

@Service
class ReservationFormView(
    private val markDownService: MarkDownService,
    private val icons: Icons,
    private val formComponents: FormComponents,
    private val sessionTimer: SessionTimer,
    private val stepIndicator: StepIndicator,
    private val commonComponents: CommonComponents,
    private val citizenContainer: CitizenContainer,
    private val slipHolder: SlipHolder,
) : BaseView() {
    fun boatForm(params: BoatFormParams): String {
        val (userType, citizen, boats, reservationId, input) = params
        val boatTypes = BoatType.entries.map { it.name }

        // language=HTML
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
                "shared.label.widthInMeters",
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
                "shared.label.lengthInMeters",
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

    fun boatSpaceForm(
        reservation: ReservationForApplicationForm,
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
                "${reservation.place}",
            )
        val boatSpaceTypeField =
            formComponents.field(
                "boatApplication.boatSpaceType",
                "boatSpaceType",
                t("boatSpaces.typeOption.${reservation.boatSpaceType}"),
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
                "boatSpaceReservation.label.reservationValidity",
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

        val wholeLocationName = "${reservation.locationName} ${reservation.place}"

        val boatForm =
            """
                <div id="boatForm">
               ${boatForm(
                BoatFormParams(
                    userType,
                    citizen,
                    boats,
                    reservation.id,
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
            )}
            </div>
            """.trimIndent()

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
                        action="/${userType.path}/venepaikka/varaus/${reservation.id}"
                        method="post"
                        novalidate>
                         <h1 class="title pb-l" id='boat-space-form-header'>
                            ${t("boatApplication.title.reservation")} 
                            $wholeLocationName
                        </h1>
                        <div id="form-inputs" class="block">
                            <div class='form-section'>
                            ${citizenContainer.render(userType, reservation.id, input, citizen, municipalities)}  
                            </div>
                            
                             <div class='form-section'>
                            $slipHolder.render(organizations, input.isOrganization ?: false, input.organizationId, userType, reservation.id, municipalities)
                            </div> 
                            
                            <div class='form-section'>
                            $boatForm
                            </div>
                       
                             <div class='form-section'>
                            $boatSpaceInformation
                            </div>
                               
                            <div class="block">
                                <div id="certify-control" class="field">
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
                                <div id="agree-control" class="field">
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
                                hx-on-htmx-after-request="${if (userType == UserType.CITIZEN) "window.location = '/kuntalainen/venepaikat';" else "history.back()"}">
                                ${t("confirm")}
                            </button>
                        </div>
                    </div>
                    
                </div>
            </section>
            """.trimIndent()
        )
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
}
