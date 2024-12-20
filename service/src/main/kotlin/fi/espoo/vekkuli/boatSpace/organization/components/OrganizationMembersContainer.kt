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
        return (
            """
            <div class="form-section">
                <h4>${t("organizationDetails.title.organizationMembers")}</h4>
                <table>
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
