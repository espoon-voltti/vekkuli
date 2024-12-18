package fi.espoo.vekkuli.views.organization.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.Municipality
import fi.espoo.vekkuli.domain.Organization
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import org.springframework.stereotype.Component

@Component
class OrganizationContactDetailsEdit(
    private val formComponents: FormComponents,
    private val icons: Icons,
    private val organizationContactDetails: OrganizationContactDetails
) : BaseView() {
    fun render(
        organization: Organization,
        municipalities: List<Municipality>
    ): String {
        val organizationNameInput =
            formComponents.textInput(
                "organizationDetails.title.name",
                "organizationName",
                organization.name,
                required = true
            )

        val businessIdInput =
            formComponents.textInput(
                "organizationDetails.title.businessId",
                "businessId",
                organization.businessId,
                required = true
            )

        val municipalityInput =
            formComponents.select(
                "organizationDetails.title.municipality",
                "municipalityCode",
                organization.municipalityCode.toString(),
                municipalities.map { Pair(it.code.toString(), it.name) },
                required = true
            )

        val phoneNumberInput =
            formComponents.textInput(
                "organizationDetails.title.phoneNumber",
                "phoneNumber",
                organization.phone
            )

        val emailInput = formComponents.textInput("organizationDetails.title.email", "email", organization.email)

        val addressInput =
            formComponents.textInput(
                "organizationDetails.title.address",
                "address",
                organization.streetAddress
            )

        val addressPostalCode = formComponents.textInput("organizationDetails.title.postalCode", "postalCode", organization.postalCode)
        val addressPostOffice = formComponents.textInput("organizationDetails.title.postOffice", "postOffice", organization.postOffice)

        val editContainer =
            organizationContactDetails.getOrganizationContactDetailsFields(
                organizationNameInput,
                businessIdInput,
                municipalityInput,
                phoneNumberInput,
                emailInput,
                addressInput,
                addressPostalCode,
                addressPostOffice
            )

        val editUrl = "/virkailija/yhteiso/${organization.id}/muokkaa"
        val cancelUrl = "/virkailija/yhteiso/${organization.id}"

        val buttons =
            formComponents.buttons(
                cancelUrl,
                "#reserver-details",
                "#reserver-details",
                "cancel-boat-edit-form",
                "submit-boat-edit-form"
            )
        // language=HTML
        return """
            <form id="edit-organization-form"
                  method="post" 
                  hx-patch="$editUrl"
                  novalidate
                  hx-target="#reserver-details"
                  hx-select="#reserver-details"
                  hx-swap="outerHTML"
            >
                $editContainer
                
                $buttons
            </form>
            <script>
                validation.init({forms: ['edit-organization-form']})
            </script>
            <div class="edit-buttons" hx-swap-oob="outerHTML:.edit-buttons"></div>
            """.trimIndent()
    }
}
