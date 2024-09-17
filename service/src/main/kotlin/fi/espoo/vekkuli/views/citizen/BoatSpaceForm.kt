package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.ReservationInput
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.utils.cmToM
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BoatSpaceForm {
    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var formComponents: FormComponents

    @Autowired
    lateinit var sessionTimer: SessionTimer

    @Autowired
    lateinit var stepIndicator: StepIndicator

    fun t(key: String): String = messageUtil.getMessage(key)

    fun boatSpaceForm(
        reservation: ReservationWithDependencies,
        boats: List<Boat>,
        citizen: CitizenWithDetails?,
        input: ReservationInput,
        showBoatSizeWarning: Boolean,
        reservationTimeInSeconds: Long,
        userType: UserType
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
            """.trimIndent()

        val boatTypeSelect =
            formComponents.select(
                "boatApplication.boatType",
                "boatType",
                boatTypes.first(),
                boatTypes.map { it to formComponents.t("boatApplication.boatTypeOption.$it") },
            )

        val widthInput =
            formComponents.decimalInput(
                "boatApplication.boatWidthInMeters",
                "width",
                input.width,
                required = true,
                """
                hx-trigger="change"
                hx-post="/${userType.path}/venepaikka/varaus/${reservation.id}/validate"
                hx-swap="outerHTML"
                hx-select="#warning"
                hx-target="#warning"
                hx-sync="closest #form:replace"
                """.trimIndent()
            )

        val lengthInput =
            formComponents.decimalInput(
                "boatApplication.boatLengthInMeters",
                "length",
                input.length,
                required = true,
                """
                hx-trigger="change"
                hx-post="/${userType.path}/venepaikka/varaus/${reservation.id}/validate"
                hx-swap="outerHTML"
                hx-select="#warning"
                hx-target="#warning"
                hx-sync="closest #form:replace"
                """.trimIndent()
            )

        // language=HTML
        val warning =
            """
            <div class="warning" id="boatSize-warning">
                <p class="block">${t("boatSpaceApplication.boatSizeWarning")}</p>
                <p class="block">${t("boatSpaceApplication.boatSizeWarningExplanation")}</p>
                <button class="icon-text"
                        type="button"
                        id="size-warning-back-button"
                        x-on:click="modalOpen = true">
                    <span class="icon">
                        <!--<div th:replace="~{fragments/icons :: chevron-left}"></div>-->
                    </span>
                    <span>${t("boatSpaces.goBack")}</span>
                </button>
            </div>
            """.trimIndent()

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
                            selected="${input.ownership.toString() == opt}"
                        />
                        <label for="ownership-$opt">${t("boatApplication.ownershipOption.$opt")}</label>
                    </div>
                    """.trimIndent()
                }
            }
                     </div>
                 </div> 
            """.trimIndent()

        val citizenInformation =
            """
            <div class='block'
                <h3 class="header">
                    ${t("boatApplication.personalInformation")}
                </h3> 
                <div class="field">
                    <p>${citizen?.firstName} ${citizen?.lastName}</p>
                    <p>${citizen?.nationalId}</p>
                    <p>${citizen?.address}</p>
                    <p>${citizen?.postalCode} ${citizen?.municipalityName}</p>
                </div>
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
                citizen?.address ?: "",
                required = true
            )

        val postalCode =
            formComponents.textInput(
                "boatApplication.postalCode",
                "postalCode",
                citizen?.postalCode ?: "",
                required = true
            )

        val municipality =
            formComponents.textInput(
                "boatApplication.municipality",
                "municipality",
                citizen?.municipalityName ?: "",
                required = true
            )

        val citizenInputFields =
            """
                
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
                $municipality
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
                        class="column is-half"
                        action="/${userType.path}/venepaikka/varaus/${reservation.id}"
                        method="post"
                        novalidate>
                        
                        $boatSpaceInformation
                        
                        <div class="block">
                            <h3 class="header">${t("boatApplication.boatInformation")}</h3>
                            $boatRadioButtons
                        </div>
                       
                        $boatTypeSelect
                        <div class="block">
                            <div class="columns">
                                <div class="column">
                                    $widthInput
                                </div>
                                <div class="column">
                                    $lengthInput
                                </div>
                            </div>
                            
                            <div id="warning" >
                                ${if (showBoatSizeWarning) warning else ""} 
                            </div>
                        
                            <div class="columns">
                                <div class="column">
                                    $depthInput
                                </div>
                                <div class="column">
                                    $weightInput
                                </div>
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
                                                ${if (input.noRegistrationNumber == true) "checked" else ""}
                                                />
                                        <span>${t("boatApplication.noRegistrationNumber")}</span>
                                   </label> 
                               </div>
                           </div>
                           $otherIdentifierInput
                           $extraInformationInput
                           $ownership
                        </div>
                        ${if (userType == UserType.CITIZEN) citizenInformation else citizenInputFields}
                        <div class="block">
                            $email
                            $phone 
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
                                    <span> ${t("boatApplication.agreementCheckbox")} </span>
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
}
