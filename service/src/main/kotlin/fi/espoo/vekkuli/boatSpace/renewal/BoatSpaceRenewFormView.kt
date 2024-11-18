package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.citizen.BoatFormInput
import fi.espoo.vekkuli.views.citizen.BoatSpaceForm
import fi.espoo.vekkuli.views.citizen.SessionTimer
import fi.espoo.vekkuli.views.citizen.StepIndicator
import fi.espoo.vekkuli.views.common.CommonComponents
import org.springframework.stereotype.Service

data class BoatSpaceRenewViewParams(
    val reservation: ReservationWithDependencies,
    val boats: List<Boat>,
    val citizen: CitizenWithDetails? = null,
    val input: RenewalReservationInput,
    val reservationTimeInSeconds: Long,
    val userType: UserType,
    val municipalities: List<Municipality>,
)

@Service
class BoatSpaceRenewFormView(
    private val markDownService: MarkDownService,
    private val icons: Icons,
    private val messageUtil: MessageUtil,
    private val formComponents: FormComponents,
    private val sessionTimer: SessionTimer,
    private val stepIndicator: StepIndicator,
    private val commonComponents: CommonComponents,
    private val boatSpaceForm: BoatSpaceForm
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun boatSpaceRenewForm(boatSpaceRenewParams: BoatSpaceRenewViewParams): String {
        val (reservation, boats, citizen, input, reservationTimeInSeconds, userType, municipalities) = boatSpaceRenewParams
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
                t("boatSpaces.typeOption.${reservation.type}"),
            )
        val spaceDimensionField =
            formComponents.field(
                "boatApplication.boatSpaceDimensions",
                "boatSpaceDimension",
                "${reservation.widthCm.cmToM()} m x ${reservation.lengthCm.cmToM()} m",
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
                """ <p>${t("boatApplication.boatSpaceFee")}: ${reservation.priceWithoutAlvInEuro} &euro;</p>
                <p>${t("boatApplication.boatSpaceFeeAlv")}: ${reservation.alvPriceInEuro} &euro;</p>
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

        // language=HTML
        val citizenContainer =
            """
                <h3 class="header">${t("boatApplication.title.reserver")}</h3>
            ${
                if (userType == UserType.CITIZEN) {
                    citizenInformation
                } else {
                    ""
                }
            }
            """

        val wholeLocationName = "${reservation.locationName} ${reservation.section}${reservation.placeNumber}"
        val boatForm =
            boatSpaceForm.boatForm(
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

        // language=HTML
        return (
            """
            <section class="section">
                <div class="container" id="container" x-data='{modalOpen: false'> 
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
                        action="/${userType.path}/venepaikka/jatka/${reservation.id}"
                        method="post"
                        novalidate>
                         <h1 class="title pb-l" id='boat-space-form-header'>
                            ${t("boatApplication.title.reservation")} 
                            $wholeLocationName
                        </h1>
                        <div id="form-inputs">
                            <div class='form-section'>
                            $citizenContainer  
                            $boatForm
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
                        
                        
                        </div >
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
                                    ${t("boatApplication.$userType.continueToPaymentButton")}
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
                                hx-delete="/${userType.path}/venepaikka/jatka/${reservation.id}"
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
}
