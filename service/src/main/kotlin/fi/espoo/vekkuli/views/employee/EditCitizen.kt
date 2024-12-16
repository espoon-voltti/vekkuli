package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Municipality
import fi.espoo.vekkuli.views.common.CommonComponents
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EditCitizen {
    @Autowired
    lateinit var formComponents: FormComponents

    @Autowired
    lateinit var commonComponents: CommonComponents

    fun editCitizenForm(
        citizen: CitizenWithDetails,
        municipalities: List<Municipality>,
        errors: Map<String, String>,
        userType: UserType
    ): String {
        val firstNameInput =
            if (userType == UserType.EMPLOYEE) {
                formComponents.textInput(
                    "boatSpaceReservation.title.firstName",
                    "firstName",
                    citizen.firstName,
                    required = true,
                )
            } else {
                formComponents.field(
                    "boatSpaceReservation.title.firstName",
                    "firstNameField",
                    citizen.firstName,
                )
            }

        val lastNameInput =
            if (userType == UserType.EMPLOYEE) {
                formComponents.textInput(
                    "boatSpaceReservation.title.lastName",
                    "lastName",
                    citizen.lastName,
                    required = true,
                )
            } else {
                formComponents.field(
                    "boatSpaceReservation.title.lastName",
                    "lastNameField",
                    citizen.lastName,
                )
            }

        // TODO: validate nationalId properly
        val nationalIdInput =
            if (userType == UserType.EMPLOYEE) {
                formComponents.textInput(
                    "boatSpaceReservation.title.nationalId",
                    "nationalId",
                    citizen.nationalId,
                    required = true,
                    pattern =
                        Pair(
                            "^(0[1-9]|[12][0-9]|3[01])(0[1-9]|1[0-2])\\d{2}[\\+\\-A]\\d{3}[0-9A-FHJ-NPR-Y]\$",
                            "validation.nationalId"
                        )
                )
            } else {
                formComponents.field(
                    "boatSpaceReservation.title.nationalId",
                    "nationalIdField",
                    citizen.nationalId,
                )
            }

        val postalCodeInput =
            if (userType == UserType.EMPLOYEE) {
                formComponents.textInput(
                    "boatSpaceReservation.title.postalCode",
                    "postalCode",
                    citizen.postalCode
                )
            } else {
                formComponents.field(
                    "boatSpaceReservation.title.postalCode",
                    "postalCodeField",
                    citizen.postalCode,
                )
            }

        val cityInput =
            if (userType == UserType.EMPLOYEE) {
                formComponents.textInput(
                    "boatSpaceReservation.title.city",
                    "city",
                    citizen.municipalityName
                )
            } else {
                formComponents.field(
                    "boatSpaceReservation.title.city",
                    "cityField",
                    citizen.municipalityName,
                )
            }

        val municipalityInput =
            if (userType == UserType.EMPLOYEE) {
                formComponents.select(
                    "boatSpaceReservation.title.municipality",
                    "municipalityCode",
                    citizen.municipalityCode.toString(),
                    municipalities.map { Pair(it.code.toString(), it.name) },
                    required = true
                )
            } else {
                formComponents.field(
                    "boatSpaceReservation.title.municipality",
                    "municipalityField",
                    citizen.municipalityName,
                )
            }

        val addressInput =
            if (userType == UserType.EMPLOYEE) {
                formComponents.textInput(
                    "boatSpaceReservation.title.address",
                    "address",
                    citizen.streetAddress
                )
            } else {
                formComponents.field(
                    "boatSpaceReservation.title.address",
                    "addressField",
                    citizen.streetAddress,
                )
            }

        val emailInput =
            formComponents.textInput(
                "boatSpaceReservation.title.email",
                "email",
                citizen.email,
                required = true,
                pattern = Pair(".+@.+\\..+", "validation.email")
            )

        val phoneNumberInput =
            formComponents.textInput(
                "boatSpaceReservation.title.phoneNumber",
                "phoneNumber",
                citizen.phone,
                required = true,
                pattern =
                    Pair(
                        "^[(]?[0-9]{3}[)]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}\$",
                        "validation.phoneNumber"
                    )
            )

        val goBackUrl =
            if (userType == UserType.EMPLOYEE) {
                "/virkailija/kayttaja/${citizen.id}"
            } else {
                "/kuntalainen/omat-tiedot"
            }

        val buttons =
            formComponents.buttons(
                goBackUrl,
                "#reserver-details",
                "#reserver-details",
                "cancel-boat-edit-form",
                "submit-boat-edit-form"
            )

        val submitUrl =
            if (userType == UserType.EMPLOYEE) {
                "/virkailija/kayttaja/${citizen.id}"
            } else {
                "/kuntalainen/omat-tiedot"
            }

        //language=HTML
        return (
            """
            <form id="edit-citizen-form"
                  method="post" 
                  hx-patch="$submitUrl"
                  novalidate
                  hx-target="#reserver-details"
                  hx-select="#reserver-details"
                  hx-swap="outerHTML"
            >
                <input type="hidden" name="id" value="${citizen.id}" />
                ${formComponents.formHeader("boatSpaceReservation.title.customerInformation")}
                <div>
                ${commonComponents.getCitizenFields(
                firstNameInput,
                lastNameInput,
                nationalIdInput,
                addressInput,
                postalCodeInput,
                cityInput,
                municipalityInput,
                phoneNumberInput,
                emailInput
            )}
                </div>
                $buttons
            </form>
             <script>
                validation.init({forms: ['edit-citizen-form']})
            </script>
            """.trimIndent()
        )
    }
}
