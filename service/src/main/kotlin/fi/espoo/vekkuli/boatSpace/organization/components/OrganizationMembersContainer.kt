package fi.espoo.vekkuli.boatSpace.organization.components

import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrganizationMembersContainer : BaseView() {
    // language=HTML
    fun render(
        organizationId: UUID,
        organizationMembers: List<CitizenWithDetails>
    ): String {
        val organizationMembersRows =
            organizationMembers.joinToString("\n") { member ->
                """
                <tr>
                  <td><a href='/virkailija/kayttaja/${member.id}'>${member.firstName} ${member.lastName}</a></td>
                  <td>${member.phone}</td>
                  <td>${member.email}</td>
                  <td>${removeUserButton(organizationId, member.id)}</td>
                </tr>
                """.trimIndent()
            }

        val editUrl = "/virkailija/yhteiso/$organizationId/jasenet/lisaa"

        val addOrganizationButton =
            """
                <div class='is-flex is-justify-content-right'>
            <a class="is-link is-icon-link" 
                id="add-organization-member"
                hx-get="$editUrl"
                hx-include="[name='citizenId']"
                hx-target="#add-members-container"
                hx-swap="innerHTML">
                <span class="icon">
                    ${icons.plus}
                </span>
                <span>${t("organizationDetails.button.addOrganizationMembers")}</span>
            </a>
            </div>
            """.trimIndent()
        return (
            """
            <div class="form-section id="organization-member-table" x-data='{citizenFullName: "", citizenId:"", updateFullName(event) {
                    const selectElement = event.target;
                    if (selectElement.selectedOptions.length > 0) {
                        const selectedOption = selectElement.selectedOptions[0];
                        this.citizenFullName = selectedOption.dataset.fullname;
                        this.citizenId = selectedOption.value;
                    } else {
                        this.citizenFullName = "";
                        this.citizenId = "";
                    };
                }}'>
                <h4>${t("organizationDetails.title.organizationMembers")}</h4>
                <table class='container'>
                     <thead>
                          <tr>
                               <th>${t("organizationDetails.tableHeaders.name")}</th>
                               <th>${t("organizationDetails.tableHeaders.phone")}</th>
                               <th>${t("organizationDetails.tableHeaders.email")}</th>
                               <th>${t("organizationDetails.tableHeaders.removeUser")}</th>
                        </tr>
                     </thead>
                     <tbody>
                         $organizationMembersRows
                      </tbody>
                </table>
                <div id="add-members-container" class='pt-s' >$addOrganizationButton</div>
            </div>
            """.trimIndent()
        )
    }

    fun removeUserButton(
        organizationId: UUID,
        userId: UUID,
    ): String =
        // language=HTML
        """
                <div x-data="{deleteModal: false}">
                    <a class="is-link has-text-danger" 
                       id="remove-user-$organizationId-$userId"
                       x-on:click="deleteModal = true">
                        <span class="icon">${icons.remove}</span>
                    </a>
                    ${deleteModel(organizationId, userId)}
               </div>
                
                """

    fun deleteModel(
        organizationId: UUID,
        userId: UUID
    ): String =
        // language=HTML
        """
        <div class="modal" x-show="deleteModal" style="display:none;">
            <div class="modal-underlay" @click="deleteModal = false"></div>
            <div class="modal-content">
                <div class="container">
                    <div class="has-text-centered is-1">
                        <p class='mb-m'>${t("organizationDetails.text.deleteUserConfirmation")}</p>
                        <div class="buttons is-centered">
                            <a class="button is-secondary" id="delete-modal-cancel-$organizationId-$userId" x-on:click="deleteModal = false">
                                ${t("cancel")}
                            </a>
                            <a class="button is-danger" 
                               id="delete-modal-confirm-$organizationId-$userId" 
                               hx-delete="/virkailija/yhteiso/$organizationId/poista-henkilo/$userId"
                               hx-swap="none" 
                               hx-on="htmx:afterRequest: window.location.reload()">
                                ${t("organizationDetails.button.confirmDeletion")}
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        """.trimIndent()
}
