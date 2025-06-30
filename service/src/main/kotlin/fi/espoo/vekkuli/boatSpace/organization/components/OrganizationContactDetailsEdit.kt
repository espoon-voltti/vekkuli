package fi.espoo.vekkuli.boatSpace.organization.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Municipality
import fi.espoo.vekkuli.domain.Organization
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.stereotype.Component

@Component
class OrganizationContactDetailsEdit(
    private val formComponents: FormComponents,
    private val organizationContactDetails: OrganizationContactDetails,
    private val organizationMembersContainer: OrganizationMembersContainer,
) : BaseView() {
    fun render(
        @SanitizeInput organization: Organization,
        municipalities: List<Municipality>,
        @SanitizeInput organizationMembers: List<CitizenWithDetails>,
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

        val billingName =
            formComponents.textInput(
                "organizationDetails.title.billingName",
                "billingName",
                organization.billingName,
                required = false,
            )
        val billingStreetAddress =
            formComponents.textInput(
                "organizationDetails.title.address",
                "billingStreetAddress",
                organization.billingStreetAddress,
                required = true,
            )
        val billingPostalCode =
            formComponents.textInput(
                "organizationDetails.title.postalCode",
                "billingPostalCode",
                organization.billingPostalCode,
                required = true,
            )
        val billingPostOffice =
            formComponents.textInput(
                "organizationDetails.title.postOffice",
                "billingPostOffice",
                organization.billingPostOffice,
                required = true,
            )

        val editUrl = "/virkailija/yhteiso/${organization.id}/muokkaa"

        val editContainer =
            organizationContactDetails.getOrganizationContactDetailsFields(
                null,
                "/virkailija/yhteiso/${organization.id}",
                organizationNameInput,
                businessIdInput,
                municipalityInput,
                phoneNumberInput,
                emailInput,
                addressInput,
                addressPostalCode,
                addressPostOffice,
                billingName,
                billingStreetAddress,
                billingPostalCode,
                billingPostOffice
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
                ${organizationMembersContainer.render(organization.id, organizationMembers)}
            </form>
            <script>
                validation.init({forms: ['edit-organization-form']})
            </script>
            """.trimIndent()
    }
}
