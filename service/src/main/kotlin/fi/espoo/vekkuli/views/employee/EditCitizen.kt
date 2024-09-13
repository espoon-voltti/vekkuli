package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.views.CommonComponents
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
        errors: Map<String, String>,
    ): String {
        val firstNameInput =
            formComponents.textInput(
                "boatSpaceReservation.title.firstName",
                "firstName",
                citizen.firstName,
                required = true,
            )
        val lastNameInput =
            formComponents.textInput(
                "boatSpaceReservation.title.lastName",
                "lastName",
                citizen.lastName,
                required = true,
            )

        val nationalIdInput =
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

        val postalCodeInput =
            formComponents.textInput(
                "boatSpaceReservation.title.postalCode",
                "postalCode",
                citizen.postalCode
            )

        val cityInput =
            formComponents.textInput(
                "boatSpaceReservation.title.city",
                "city",
                citizen.municipalityName
            )

        val municipalityInput =
            formComponents.textInput(
                "boatSpaceReservation.title.municipality",
                "municipality",
                citizen.municipalityName
            )

        val addressInput =
            formComponents.textInput(
                "boatSpaceReservation.title.address",
                "address",
                citizen.address
            )

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

        val buttons =
            formComponents.buttons(
                "/virkailija/kayttaja/${citizen.id}",
                "#citizen-details",
                "#citizen-details",
                "cancel-boat-edit-form",
                "submit-boat-edit-form"
            )

        //language=HTML
        return (
            """
            <form id="edit-citizen-form"
                  method="post" 
                  hx-patch="/virkailija/kayttaja/${citizen.id}"
                  novalidate
                  hx-target="#citizen-details"
                  hx-select="#citizen-details"
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
