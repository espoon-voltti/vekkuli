package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.utils.PHONE_NUMBER_REGEX
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.common.CommonComponents
import org.springframework.stereotype.Component

// language=HTML
@Component
class CitizenContainerForCitizen(
    private val commonComponents: CommonComponents,
    private val formComponents: FormComponents
) : BaseView() {
    // language=HTML
    fun render(
        email: String?,
        phone: String?,
        citizen: CitizenWithDetails?
    ): String {
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

        val email =
            formComponents.textInput(
                "boatApplication.email",
                "email",
                email,
                true,
                pattern = Pair(".+@.+\\..+", "validation.email")
            )

        val phone =
            formComponents.textInput(
                "boatApplication.phone",
                "phone",
                phone,
                required = true,
                pattern =
                    Pair(
                        PHONE_NUMBER_REGEX,
                        "validation.phoneNumber"
                    )
            )

        return (
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
        )
    }
}
