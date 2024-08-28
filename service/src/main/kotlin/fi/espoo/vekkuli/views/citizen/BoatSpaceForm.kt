package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.ReservationInput
import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.Citizen
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

    fun t(key: String): String {
        return messageUtil.getMessage(key)
    }

    fun boatSpaceForm(
        reservation: ReservationWithDependencies,
        boats: List<Boat>,
        user: Citizen,
        input: ReservationInput,
        showBoatSizeWarning: Boolean
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
                       hx-get="/kuntalainen/venepaikka/varaus/${reservation.id}?boatId=${boat.id}"
                       hx-target="body"
                       hx-swap="outerHTML"
                       name="boatId"
                       ${if (input.boatId == boat.id) "checked" else ""}
                />
                <label for="${boat.id}"
                       th:text="${boat.displayName}">Mun vene</label>
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
                        hx-get="/kuntalainen/venepaikka/varaus/${reservation.id}"
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
                hx-trigger="blur delay:100ms"
                hx-post="/kuntalainen/venepaikka/varaus/${reservation.id}/validate"
                hx-swap="outerHTML"
                hx-select="#warning"
                hx-target="#warning"
                """.trimIndent()
            )

        val lengthInput =
            formComponents.decimalInput(
                "boatApplication.boatLengthInMeters",
                "length",
                input.length,
                required = true,
                """
                hx-trigger="blur delay:100ms"
                hx-post="/kuntalainen/venepaikka/varaus/${reservation.id}/validate"
                hx-swap="outerHTML"
                hx-select="#warning"
                hx-target="#warning"
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
                "registrationNumber",
                input.boatRegistrationNumber,
                required = true
            )

        val otherIdentifierInput =
            formComponents.textInput(
                "boatSpaceReservation.title.otherIdentifier",
                "otherIdentification",
                input.otherIdentification,
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

        val email =
            formComponents.textInput(
                "boatApplication.email",
                "email",
                input.email,
            )

        val phone =
            formComponents.textInput(
                "boatApplication.phone",
                "phone",
                input.phone,
            )

        // language=HTML
        return """
            <section class="section">
                <div class="container" id="container" x-data="{ modalOpen: false }"> 
                    <form
                        id="form"
                        class="column is-half"
                        action="/kuntalainen/venepaikka/varaus/${reservation.id}"
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
                        
                        
                        
                        <div class="block">
                            
                           $boatNameInput
                           <div x-data="{ noReg:false }">
                               <template x-if="!noReg">
                                    $registrationNumberInput
                               </template>
                               <label class="checkbox">
                                    <input type="checkbox" name="noRegistrationNumber" id="noRegistrationNumber" @click="noReg = ! noReg"/>
                                    <span>${t("boatApplication.noRegistrationNumber")}</span>
                               </label> 
                           </div>
                           $otherIdentifierInput
                           $extraInformationInput
                           $ownership
                        </div>
                        <div class='block'
                            <h3 class="header">
                                ${t("boatApplication.personalInformation")}
                            </h3> 
                            <div class="field">
                                <p>${user.firstName} ${user.lastName}</p>
                                <p>${user.nationalId}</p>
                                <p>${user.address}</p>
                                <p>${user.postalCode} ${user.municipality}</p>
                            </div>
                        </div>
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
                                hx-delete="/kuntalainen/venepaikka/varaus/${reservation.id}"
                                hx-on-htmx-after-request="window.location = '/kuntalainen/venepaikat';">
                                ${t("confirm")}
                            </button>
                        </div>
                    </div>
                    
                    <div class="container" th:fragment="backButton">
                        <button x-on:click="modalOpen = true" class="icon-text">
                            <span class="icon">
                                <div th:replace="~{fragments/icons :: chevron-left}"></div>
                            </span>
                            <span th:text="#{boatSpaces.goBack}">Takaisin</span>
                        </button>
                    </div> 
                </div>
            </section>
            """.trimIndent()
    }
}
