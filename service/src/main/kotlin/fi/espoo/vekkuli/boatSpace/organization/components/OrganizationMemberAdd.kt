package fi.espoo.vekkuli.boatSpace.organization.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.boatSpace.reservationForm.components.CitizenSearch
import fi.espoo.vekkuli.boatSpace.reservationForm.components.CitizensSearchContent
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.common.CommonComponents
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrganizationMemberAdd(
    private val citizensSearchContent: CitizensSearchContent,
    private val citizenSearch: CitizenSearch,
    private val formComponents: FormComponents,
    private val commonComponents: CommonComponents
) : BaseView() {
    fun organizationAddMemberSearchContent(
        citizens: List<CitizenWithDetails>,
        organizationId: UUID
    ): String {
        val listSize = if (citizens.size >= 3) 3 else citizens.size
        // language=HTML
        return (
            """
            <select 
                x-show="citizenFullName != ''" 
                multiple 
                size="$listSize" 
                name='citizenIdOption' 
                hx-get="/virkailija/yhteiso/$organizationId/jasenet/lisaa"
                 hx-include="[name='citizenId']"
                hx-target="#add-members-container"
                hx-swap="innerHTML"
                @change="updateFullName">
                ${citizensSearchContent.searchContentList(citizens)}
            </select>

            """.trimIndent()
        )
    }

    fun render(
        chosenCitizen: CitizenWithDetails?,
        organizationId: UUID
    ): String {
        val cancelUrl = "/virkailija/yhteiso/$organizationId"

        // language=HTML
        val citizenInformation =
            if (chosenCitizen != null) {
                """
                <div class='columns'>
                    <div class="field column is-one-quarter">
                        ${formComponents.field("organizationDetails.title.fullName", "citizenFullNameField", chosenCitizen.fullName)}
                    </div>
                     <div class="field column is-one-quarter">
                        ${formComponents.field("organizationDetails.title.phoneNumber", "citizenPhoneNumberField", chosenCitizen.phone)}
                    </div>
                     <div class="field column is-one-quarter">
                        ${formComponents.field("organizationDetails.title.email", "citizenEmailField", chosenCitizen.email)}
                    </div>
                
                </div>
                """.trimIndent()
            } else {
                ""
            }

        // language=HTML
        return """
            <div id='add-member-search-container'>
                <div class="field">
                  ${citizenSearch.render("/virkailija/yhteiso/$organizationId/jasenet/hae")}    
                </div>
                <div class='field'>
                  $citizenInformation 
                </div>
               <div class="buttons">
                    <button
                            id="cancel-organization-member-add"
                            class="button"
                            type="button"
                            hx-get=$cancelUrl
                            hx-target="#add-members-container"
                            hx-select="#add-members-container"
                            hx-swap="outerHTML"
                    >${t("cancel")}</button>
                    <button
                            id="submit-organization-member-add"
                            hx-patch='/virkailija/yhteiso/$organizationId/jasenet/lisaa'
                            hx-target="#organization-member-table"
                            hx-include='[name="citizenId"]'
                            hx-swap='outerHTML'
                            class="button is-primary"
                            type="submit"
                    >${t("citizenDetails.saveChanges")}</button>
                </div>
            </div>
            """.trimIndent()
    }
}
