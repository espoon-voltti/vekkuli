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
               hx-target="#form-inputs"
               hx-select="#form-inputs"
               hx-swap="outerHTML"
               ${if (selectedOrganizationId == org.id) "checked" else ""}
            />
            <label for="${org.id}">${org.name}</label>
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
                        hx-target="#form-inputs"
                        hx-select="#form-inputs"
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

    fun newOrganizationForm(municipalities: List<Municipality>): String {
        val nameInput =
            formComponents.textInput(
                "boatApplication.organizationName",
                "orgName",
                null,
                required = true
            )

        //language=HTML
        val businessIdInput =
            """
            <div class="field">
                <div class="control">
                    <label class="label required" for="orgBusinessId" >${t("boatApplication.organizationId")}</label>
                    <input
                        class="input"
                        data-required
                        data-validate-url="/validate/businessid"
                        type="text"
                        id="orgBusinessId"
                        name="orgBusinessId" />
                   
                    <div id="orgBusinessId-error-container">
                        <span id="orgBusinessId-error" class="help is-danger" style="display: none">
                            ${t("validation.required")}
                        </span>
                    </div>
                </div>
            </div>
            """.trimIndent()

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
            <div>
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
                newOrganizationForm(municipalities)
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
                            hx-target="#form-inputs"
                            hx-select="#form-inputs"
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
                            hx-target="#form-inputs"
                            hx-select="#form-inputs"
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
