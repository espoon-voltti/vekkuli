package fi.espoo.vekkuli.boatSpace.organization.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Organization
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class OrganizationContactDetails(
    private val formComponents: FormComponents,
    private val organizationMembersContainer: OrganizationMembersContainer,
) : BaseView() {
    fun getOrganizationContactDetailsFields(
        editUrl: String?,
        cancelUrl: String?,
        organizationNameValue: String,
        businessIdValue: String,
        municipalityField: String,
        phoneNumberField: String,
        emailField: String,
        addressField: String,
        postalCode: String? = null,
        postOffice: String? = null,
        billingName: String,
        billingStreetAddress: String,
        billingPostalCode: String? = null,
        billingPostOffice: String? = null,
    ): String {
        // language=HTML
        val addressFields =
            if (postalCode != null && postOffice != null) {
                """
                <div class="field column is-one-eight">
                    $postalCode
                </div>
                <div class="field column is-one-eight">
                    $postOffice
                </div>
                """.trimIndent()
            } else {
                ""
            }

        val billingAddressFields =
            if (billingPostalCode != null && billingPostOffice != null) {
                """
                <div class="field column is-one-eight">
                    $billingPostalCode
                </div>
                <div class="field column is-one-eight">
                    $billingPostOffice
                </div>
                """.trimIndent()
            } else {
                ""
            }

        val editOrganizationInformation =
            if (editUrl.isNullOrEmpty()) {
                ""
            } else {
                """
                <div class="column is-narrow ml-auto">
                    <a class="is-link is-icon-link" 
                        id="edit-customer"
                        hx-get="$editUrl"
                        hx-target="#organization-information"
                        hx-swap="innerHTML">
                        <span class="icon">
                            ${icons.edit}
                        </span>
                        <span>${t("organizationDetails.button.editOrganizationInformation")}</span>
                    </a>
                </div>
                """.trimIndent()
            }

        val buttons =
            if (cancelUrl.isNullOrEmpty()) {
                ""
            } else {
                formComponents.buttons(
                    cancelUrl,
                    "#reserver-details",
                    "#reserver-details",
                    "cancel-organization-edit-form",
                    "submit-organization-edit-form"
                )
            }

        // language=HTML
        return """
            <div class='form-section'>
                <div class="columns">
                    <div class="column is-narrow">
                        <h4>${t("organizationDetails.title.contactInformation")}</h4>
                    </div>
                    $editOrganizationInformation
                </div>
                <div class="columns">
                    <div class="field column is-one-quarter">
                        $organizationNameValue
                    </div>
                    <div class="field column is-one-quarter">
                        $businessIdValue
                    </div>
                    <div class="field column is-one-quarter">
                        $municipalityField
                    </div>
                </div>
                <div class="columns">
                    <div class="field column is-one-quarter">
                        $phoneNumberField
                    </div>
                    <div class="field column is-one-quarter">
                        $emailField
                    </div>
                    <div class="field column is-one-quarter">
                        $addressField
                    </div>
            
                    $addressFields
                </div>
            </div>   
            
            <div class='form-section'>
                <div class="columns">
                    <div class="column is-narrow">
                        <h4>${t("organizationDetails.title.billingInformation")}</h4>
                    </div>
                </div>
                <div class="columns">
                    <div class="field column is-one-quarter">
                        $billingName
                    </div>
                    <div class="field column is-one-quarter">
                        $billingStreetAddress
                    </div>
                    $billingAddressFields
                </div>
                $buttons
            </div>
            """.trimIndent()
    }

    fun render(
        organization: Organization,
        organizationMembers: List<CitizenWithDetails>
    ): String {
        val organizationNameField =
            formComponents.field(
                "organizationDetails.title.name",
                "organizationNameField",
                organization.name,
            )
        val organizationBusinessIdField =
            formComponents.field(
                "organizationDetails.title.businessId",
                "businessIdField",
                organization.businessId,
            )
        val municipalityField =
            formComponents.field(
                "organizationDetails.title.municipality",
                "municipalityCodeField",
                organization.municipalityName,
            )
        val addressField =
            formComponents.field(
                "organizationDetails.title.address",
                "addressField",
                buildAddress(organization.streetAddress, organization.postalCode, organization.postOffice)
            )

        val billingNameField =
            formComponents.field(
                "organizationDetails.title.billingName",
                "billingNameField",
                organization.billingName
            )
        val billingAddressField =
            formComponents.field(
                "organizationDetails.title.billingAddress",
                "billingAddressField",
                buildAddress(organization.billingStreetAddress, organization.billingPostalCode, organization.billingPostOffice)
            )
        val phoneNumberValue =
            formComponents.field("organizationDetails.title.phoneNumber", "phoneNumberField", organization.phone)
        val emailValue = formComponents.field("organizationDetails.title.email", "emailField", organization.email)

        // language=HTML
        return (
            """
                ${getOrganizationContactDetailsFields(
                "/virkailija/yhteiso/kayttaja/${organization.id}/muokkaa",
                null,
                organizationNameField,
                organizationBusinessIdField,
                municipalityField,
                phoneNumberValue,
                emailValue,
                addressField,
                null,
                null,
                billingNameField,
                billingAddressField,
                null,
                null
            )}
            ${organizationMembersContainer.render(organization.id, organizationMembers)}
            """
        )
    }

    private fun buildAddress(
        address: String?,
        postalCode: String?,
        postOffice: String?
    ) = listOf(address, postalCode, postOffice)
        .filter {
            !it.isNullOrBlank()
        }.joinToString(", ")
}
