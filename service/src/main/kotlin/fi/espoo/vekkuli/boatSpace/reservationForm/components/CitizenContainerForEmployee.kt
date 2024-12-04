package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationInput
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Municipality
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.common.CommonComponents
import org.springframework.stereotype.Component

// language=HTML
@Component
class CitizenContainerForEmployee(
    private val commonComponents: CommonComponents,
    private val icons: Icons,
    private val formComponents: FormComponents
) : BaseView() {
    fun citizenInputFields(
        input: ReservationInput,
        citizen: CitizenWithDetails?,
        municipalities: List<Municipality>
    ): String {
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

        val citizenFields =
            """ ${
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
            }""".trimIndent()

        // language=HTML
        return """
            <div>
                <h3 class="header">
                    ${t("boatApplication.personalInformation")}
                </h3> 
               $citizenFields
                
            </div>
            """.trimIndent()
    }

    fun citizenSearch(
        reservationId: Int,
        citizen: CitizenWithDetails?,
        municipalities: List<Municipality>
    ) = // language=HTML

        """
        <div id="citizen-results-container" class="container" >
            <div class="field" id="customer-search-container">
                <label class="label">${t("boatApplication.select.citizen")}</label>
                <div class="control width-is-half">
                    <p class="control has-icons-left has-icons-right">
                        <input x-model="citizenFullName" id="customer-search" 
                            placeholder="${t("boatApplication.placeholder.searchCitizens")}"
                            name="nameParameter" class="input search-input" type="text" 
                            hx-get="/virkailija/venepaikka/varaus/$reservationId/kuntalainen/hae" hx-trigger="keyup changed delay:500ms" 
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

    fun customerTypeRadioButtons(
        userType: UserType,
        reservationId: Int,
        input: ReservationInput,
    ) = // language=HTML
        """
        <div class="field">
            <div class="control is-flex-direction-row">
                <div class="radio">
                    <input
                        type="radio"
                        name="citizenSelection"
                        value="newCitizen"
                        id="new-citizen-selector"
                        hx-get="/${userType.path}/venepaikka/varaus/$reservationId"
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
                        hx-get="/${userType.path}/venepaikka/varaus/$reservationId"
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
        
        
        """.trimIndent()

    fun citizenSelection(
        input: ReservationInput,
        citizen: CitizenWithDetails?,
        municipalities: List<Municipality>,
        reservationId: Int,
    ) = if (input.citizenSelection == "newCitizen") {
        citizenInputFields(
            input,
            citizen,
            municipalities
        )
    } else {
        citizenSearch(reservationId, citizen, municipalities)
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

    fun render(
        userType: UserType,
        reservationId: Int,
        input: ReservationInput,
        citizen: CitizenWithDetails?,
        municipalities: List<Municipality>
    ): String =
        // language=HTML
        """
        <div class='form-section'>
            <h3 class="header">${t("boatApplication.title.reserver")}</h3>
            <div>
                ${customerTypeRadioButtons(userType, reservationId, input)}
                ${citizenSelection(input, citizen, municipalities, reservationId)}
            </div>
        </div>
        """.trimIndent()
}
