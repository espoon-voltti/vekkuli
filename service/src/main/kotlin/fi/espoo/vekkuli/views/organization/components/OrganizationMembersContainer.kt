package fi.espoo.vekkuli.views.organization.components

import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class OrganizationMembersContainer : BaseView() {
    // language=HTML
    fun render(organizationMembers: List<CitizenWithDetails>): String {
        val organizationMembersRows =
            organizationMembers.joinToString("\n") { member ->
                """
                <tr>
                  <td><a href='/virkailija/kayttaja/${member.id}'>${member.firstName} ${member.lastName}</a></td>
                  <td>${member.phone}</td>
                  <td>${member.email}</td>
                </tr>
                """.trimIndent()
            }
        return (
            """
            <h4>${t("organizationDetails.title.organizationMembers")}</h4>
            <table>
                 <thead>
                      <tr>
                           <th>${t("organizationDetails.tableHeaders.name")}</th>
                           <th>${t("organizationDetails.tableHeaders.phone")}</th>
                           <th>${t("organizationDetails.tableHeaders.email")}</th>
                      </tr>
                 </thead>
                 <tbody>
                     $organizationMembersRows
            </table>
            """.trimIndent()
        )
    }
}
