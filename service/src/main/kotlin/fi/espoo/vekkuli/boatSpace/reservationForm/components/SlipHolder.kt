package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.Municipality
import fi.espoo.vekkuli.domain.Organization
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component
import java.util.*

// language=HTML
@Component
class SlipHolder(
    private val formComponents: FormComponents,
    private val businessIdInput: BusinessIdInput
) : BaseView() {
    fun organizationRadioButton(
        userType: UserType,
        reservationId: Int,
        selectedOrganizationId: UUID?,
        org: Organization
    ) = """
        <div class="radio">
            <input type="radio" id="org-${org.id}-radio" value="${org.id}" 
               name="organizationId"
               hx-trigger="change"
               hx-get="/${userType.path}/venepaikka/varaus/$reservationId"
               hx-include="#form"
               hx-target="#reserver-boat-information"
               hx-select="#reserver-boat-information"
               hx-swap="outerHTML"
               ${if (selectedOrganizationId == org.id) "checked" else ""}
            />
            <label for="org-${org.id}-radio">${org.name}</label>
        </div>
        """.trimIndent()

    fun organizationSelect(
        userType: UserType,
        reservationId: Int,
        selectedOrganizationId: UUID?,
        organizations: List<Organization>
    ) = if (organizations.isNotEmpty()) {
        """
            <div class="field" style="margin-left: 32px">
                ${organizations.joinToString("\n") { organizationRadioButton(userType, reservationId, selectedOrganizationId, it) }}
                <div class="radio">
                    <input type="radio" 
                        id="newOrg" 
                        name="organizationId"
                        value=""
                        hx-trigger="change"
                        hx-get="/${userType.path}/venepaikka/varaus/$reservationId"
                        hx-include="#form"
                        hx-target="#reserver-boat-information"
                        hx-select="#reserver-boat-information"
                        hx-swap="outerHTML"
                       ${if (selectedOrganizationId == null) "checked" else ""}
                    />
                    <label for="newOrg" >${t("boatApplication.newOrg")}</label>
                </div>
            </div>
            """
    } else {
        ""
    }

    fun newOrganizationForm(
        municipalities: List<Municipality>,
        userType: UserType
    ): String {
        val nameInput =
            formComponents.textInput(
                "boatApplication.organizationName",
                "orgName",
                null,
                required = true
            )

        //language=HTML
        val businessIdInput = businessIdInput.render(userType == UserType.EMPLOYEE)

        val municipalityInput =
            formComponents.select(
                "boatSpaceReservation.title.municipality",
                "orgMunicipalityCode",
                null,
                municipalities.map { Pair(it.code.toString(), it.name) },
                required = true
            )

        val phoneInput = formComponents.textInput("boatApplication.phone", "orgPhone", null, true)

        val emailInput = formComponents.textInput("boatApplication.email", "orgEmail", null, true)

        val addressInput =
            formComponents.textInput(
                "boatApplication.address",
                "orgAddress",
                null,
            )

        val postalCodeInput =
            formComponents.textInput(
                "boatApplication.postalCode",
                "orgPostalCode",
                null,
            )

        val cityFieldInput =
            formComponents.textInput(
                "boatSpaceReservation.title.city",
                "orgCity",
                null,
            )
        // language=HTML
        return """
            <div class='pt-m'>
                <div class='columns'>
                    <div class='column is-one-quarter'>
                        $nameInput
                    </div>
                    <div class='column is-one-quarter'>
                        $businessIdInput
                    </div>
                    <div class='column is-one-quarter'>
                         $municipalityInput
                    </div>
                </div>
                <div id="orgBusinessId-server-error" class="block" style="display: none"></div>
                <div id="orgBusinessId-server-info" class="block"></div>
                <div class='columns'>
                    <div class='column is-one-quarter'>
                        $phoneInput
                    </div>
                    <div class='column is-one-quarter'>
                        $emailInput
                    </div>
                    <div class='column is-one-quarter'>
                        $addressInput
                    </div>
                    <div class='column is-one-eight'>
                        $postalCodeInput
                    </div>
                    <div class='column is-one-eight'>
                        $cityFieldInput
                    </div>
                </div>
                ${billingInformation(null)}
            </div>
            """.trimIndent()
    }

    fun editOrganizationForm(
        org: Organization,
        municipalities: List<Municipality>
    ): String {
        val nameField = formComponents.field("boatApplication.organizationName", "orgName", org.name)
        val businessIdField = formComponents.field("boatApplication.organizationId", "orgBusinessId", org.businessId)
        val municipalityField =
            formComponents.field("boatApplication.municipality", "orgMunicipality", org.municipalityName)
        val phoneInput = formComponents.textInput("boatApplication.phone", "orgPhone", org.phone, true)
        val emailInput = formComponents.textInput("boatApplication.email", "orgEmail", org.email, true)

        val addressInput =
            formComponents.textInput(
                "boatApplication.address",
                "orgAddress",
                org.streetAddress,
            )

        val postalCodeInput =
            formComponents.textInput(
                "boatApplication.postalCode",
                "orgPostalCode",
                org.postalCode,
            )

        val cityFieldInput =
            formComponents.textInput(
                "boatSpaceReservation.title.city",
                "orgCity",
                org.postOffice,
            )

        // language=HTML
        return """
            <div>
                <div class='columns'>
                    <div class='column is-one-quarter'>
                      $nameField
                    </div>
                    <div class='column is-one-quarter'>
                      $businessIdField
                    </div>
                    <div class='column is-one-quarter'>
                      $municipalityField
                    </div>
                </div>
                <div class='columns'>
                    <div class='column is-one-quarter'>
                      $phoneInput
                    </div>
                    <div class='column is-one-quarter'>
                      $emailInput
                    </div>
                    <div class='column is-one-quarter'>
                      $addressInput
                    </div>
                    <div class='column is-one-eight'>
                       $postalCodeInput
                    </div>
                    <div class='column is-one-eight'>
                      $cityFieldInput
                    </div>
                </div>
                ${billingInformation(org)}
            </div>
            """.trimIndent()
    }

    fun billingInformation(org: Organization?): String {
        val billingNameField =
            formComponents.textInput(
                "organizationDetails.title.billingName",
                "orgBillingName",
                org?.billingName,
                required = false,
            )
        val billingAddressInput =
            formComponents.textInput(
                "organizationDetails.title.billingAddress",
                "orgBillingAddress",
                org?.billingStreetAddress,
                required = true,
            )
        val billingPostalCodeInput =
            formComponents.textInput(
                "organizationDetails.title.postalCode",
                "orgBillingPostalCode",
                org?.billingPostalCode,
                required = true,
            )
        val billingPostOfficeInput =
            formComponents.textInput(
                "organizationDetails.title.postOffice",
                "orgBillingPostOffice",
                org?.billingPostOffice,
                required = true,
            )
        val billingAddressFields =
            // language=HTML
            """
             <div class="field column is-one-quarter">
               $billingNameField
            </div>
             <div class="field column is-one-quarter">
                $billingAddressInput
             </div>
             <div class="field column is-one-quarter">
                $billingPostalCodeInput
             </div>
             <div class="field column is-one-quarter">
                $billingPostOfficeInput
             </div>
            """.trimIndent()

        // language=HTML
        return """
            <div class="form-section-top-line">
                <div class="columns">
                    <div class="column is-narrow">
                        <h3>${t("organizationDetails.title.billingInformation")}</h3>
                    </div>
                </div>
                <div class="columns">
                    $billingAddressFields
                </div>
            </div>
            """.trimIndent()
    }

    fun render(
        organizations: List<Organization>,
        isOrganization: Boolean,
        selectedOrganizationId: UUID?,
        userType: UserType,
        reservationId: Int,
        municipalities: List<Municipality>
    ): String {
        val organizationForm =
            if (isOrganization && selectedOrganizationId == null) {
                newOrganizationForm(municipalities, userType)
            } else if (isOrganization) {
                val org = organizations.find { it.id == selectedOrganizationId }
                if (org == null) throw IllegalArgumentException("Organization not found")
                editOrganizationForm(org, municipalities)
            } else {
                ""
            }

        // language=HTML
        return """
            <div id="shipHolder">
            <h3>${t("boatApplication.title.tenant")}</h3>
                <input type="hidden" name="citizenId" x-bind:value="`${'$'}{citizenId}`"/>
                <div class="field" id="slipHolder">
                    <div class="radio">
                        <input type="radio" 
                            id="reseverTypePrivate" 
                            name="isOrganization"
                            value="false"
                            hx-trigger="change"
                            hx-get="/${userType.path}/venepaikka/varaus/$reservationId"
                            hx-include="#form"
                            hx-target="#reserver-boat-information"
                            hx-select="#reserver-boat-information"
                            hx-swap="outerHTML"
                           ${if (!isOrganization) "checked" else ""}
                        />
                        <label for="reseverTypePrivate" >${t("boatApplication.reserverType.private")}</label>
                    </div>
                    <div class="radio">
                        <input type="radio" 
                            id="reseverTypeOrg" 
                            name="isOrganization"
                            value="true"
                            hx-trigger="change"
                            hx-get="/${userType.path}/venepaikka/varaus/$reservationId"
                            hx-include="#form"
                            hx-target="#reserver-boat-information"
                            hx-select="#reserver-boat-information"
                            hx-swap="outerHTML"
                           ${if (isOrganization) "checked" else ""}
                        />
                        <label for="reseverTypeOrg" >${t("boatApplication.reserverType.organization")}</label>
                    </div>
                    ${if (isOrganization) organizationSelect(userType, reservationId, selectedOrganizationId, organizations) else ""}
                    $organizationForm
                </div>   
            </div>
            """
    }
}
