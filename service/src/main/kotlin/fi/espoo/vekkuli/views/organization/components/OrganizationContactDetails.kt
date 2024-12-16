package fi.espoo.vekkuli.views.organization.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.Organization
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import org.springframework.stereotype.Component

@Component
class OrganizationContactDetails(
    private val formComponents: FormComponents,
    private val icons: Icons
) : BaseView() {
    private fun getOrganizationContactDetailsFields(
        organizationNameValue: String,
        organizationIdValue: String,
        municipalityField: String,
        phoneNumberField: String,
        emailField: String,
        addressField: String,
    ): String {
        // language=HTML
        return """
            <div class="columns">
                <div class="field column is-one-quarter">
                   $organizationNameValue
                </div>
                <div class="field column is-one-quarter">
                    $organizationIdValue
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
            </div>
            """.trimIndent()
    }

    fun render(organization: Organization): String {
        val organizationNameField =
            formComponents.field(
                "organizationDetails.title.name",
                "firstNameField",
                organization.name,
            )
        val organizationBusinessIdField =
            formComponents.field(
                "organizationDetails.title.businessId",
                "lastNameField",
                organization.businessId,
            )
        val municipalityField =
            formComponents.field(
                "organizationDetails.title.municipality",
                "municipalityCodeField",
                organization.municipalityName,
            )
        // TODO: add postal city
        val addressField =
            formComponents.field(
                "organizationDetails.title.address",
                "addressField",
                "${organization.streetAddress}, ${organization.postalCode}, ${organization.municipalityName} "
            )

        val phoneNumberValue =
            formComponents.field("organizationDetails.title.phoneNumber", "phoneNumberField", organization.phone)
        val emailValue = formComponents.field("organizationDetails.title.email", "emailField", organization.email)

        // TODO add organization edti
        val editUrl = "/yhteiso/kayttaja/${organization.id}/muokkaa"
        val editOrganizationInformation =
            """
            <div class="column">
                        <div>
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
                    </div>
            """.trimIndent()
        // language=HTML
        return (
            """
                <div class="block" id="organization-information">
                    <div class="columns">
                        <div class="column is-narrow">
                            <h4 class="header mb-none">${t("organizationDetails.title.contactInformation")}</h4>
                        </div>
                        
                        <!-- $editOrganizationInformation --> 
                    </div>
                    ${
                getOrganizationContactDetailsFields(
                    organizationNameField,
                    organizationBusinessIdField,
                    municipalityField,
                    phoneNumberValue,
                    emailValue,
                    addressField
                )
            }
            </div> 
            """
        )
    }
}
