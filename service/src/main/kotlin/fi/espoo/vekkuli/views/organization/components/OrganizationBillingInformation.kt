package fi.espoo.vekkuli.views.organization.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.Organization
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class OrganizationBillingInformation(
    private val formComponents: FormComponents,
) : BaseView() {
    // language=HTML
    private fun getOrganizationBillingDetailsFields(
        organizationBillingNameValue: String,
        organizationBillingAddressValue: String,
    ): String {
        // language=HTML
        return """
            <div class="columns">
                <div class="field column is-one-quarter">
                   $organizationBillingNameValue
                </div>
                <div class="field column is-one-quarter">
                    $organizationBillingAddressValue
                </div>
            
            </div>
            """.trimIndent()
    }

    fun render(organization: Organization): String {
        val organizationBillingNameField =
            formComponents.field(
                "organizationDetails.title.billingName",
                "billingNameField",
                organization.billingName,
            )
        val organizationBillingAddressField =
            formComponents.field(
                "organizationDetails.title.billingAddress",
                "billingAddressField",
                "${organization.billingStreetAddress}, ${organization.billingPostalCode}, ${organization.billingPostOffice}",
            )

        // language=HTML
        return (
            """
                <div class="block" id="organization-information">
                    <div class="columns">
                        <div class="column is-narrow">
                            <h4>${t("organizationDetails.title.billingInformation")}</h4>
                        </div>
                    </div>
                    ${
                getOrganizationBillingDetailsFields(
                    organizationBillingNameField,
                    organizationBillingAddressField
                )
            }
            </div> 
            """
        )
    }
}
