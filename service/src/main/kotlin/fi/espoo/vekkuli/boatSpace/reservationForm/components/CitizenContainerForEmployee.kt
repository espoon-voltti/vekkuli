package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationInput
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Municipality
import fi.espoo.vekkuli.utils.PHONE_NUMBER_REGEX
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.common.CommonComponents
import org.springframework.stereotype.Component

// language=HTML
@Component
class CitizenContainerForEmployee(
    private val commonComponents: CommonComponents,
    private val formComponents: FormComponents,
    private val citizenSearch: CitizenSearch,
    private val citizensSearchContent: CitizensSearchContent
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
                "boatSpaceReservation.title.phoneNumber",
                "phone",
                input.phone,
                required = true,
                pattern =
                    Pair(
                        PHONE_NUMBER_REGEX,
                        "validation.phoneNumber"
                    )
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

    fun customerTypeRadioButtons(
        userType: UserType,
        reservationId: Int,
        citizenSelection: String? = "newCitizen"
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
                        hx-target="#form"
                        hx-select="#form"
                        hx-swap="outerHTML"
                        ${if (citizenSelection == "newCitizen") "checked" else ""}
                        
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
                        hx-target="#form"
                        hx-select="#form"
                        hx-swap="outerHTML"
                        ${if (citizenSelection == "existingCitizen") "checked" else ""}
                    />
                    <label for="existing-citizen-selector">${t("boatApplication.citizenOptions.existingCitizen")}</label>
                </div>
            </div>
        </div> 
        
        
        """.trimIndent()

    fun reservationFormCitizenSearchContent(
        citizens: List<CitizenWithDetails>,
        reservationId: Int
    ): String {
        val listSize = if (citizens.size > 5) 5 else citizens.size

        // language=HTML
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
                hx-select="#form"
                hx-target="#form" @change="updateFullName">
                ${citizensSearchContent.searchContentList(citizens)}
            </select>

            """.trimIndent()
        )
    }

    fun citizenSelection(
        input: ReservationInput,
        citizen: CitizenWithDetails?,
        municipalities: List<Municipality>,
        reservationId: Int,
    ): String {
        if (input.citizenSelection == "newCitizen") {
            return citizenInputFields(
                input,
                citizen,
                municipalities
            )
        } else {
            val citizenCopy = citizen?.copy(email = input.email ?: citizen.email, phone = input.phone ?: citizen.phone)
            return(
                """
                ${citizenSearch.render("/venepaikka/varaus/$reservationId/kuntalainen/hae")}
                ${if (citizenCopy != null) commonComponents.citizenDetails(citizenCopy, municipalities) else ""}
                """.trimIndent()
            )
        }
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
        <div>
            ${customerTypeRadioButtons(userType, reservationId, input.citizenSelection)}
            ${citizenSelection(input, citizen, municipalities, reservationId)}
        </div>
        """.trimIndent()
}
