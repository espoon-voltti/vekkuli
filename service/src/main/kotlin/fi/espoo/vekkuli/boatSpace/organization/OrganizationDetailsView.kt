package fi.espoo.vekkuli.boatSpace.organization

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.boatSpace.organization.components.OrganizationContactDetails
import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.common.CommonComponents
import fi.espoo.vekkuli.views.employee.SanitizeInput
import fi.espoo.vekkuli.views.employee.components.ReserverDetailsReservationsContainer
import org.springframework.stereotype.Service

@Service
class OrganizationDetailsView(
    var commonComponents: CommonComponents,
    var formComponents: FormComponents,
    private val organizationContactDetails: OrganizationContactDetails,
    private val reserverDetailsReservationsContainer: ReserverDetailsReservationsContainer,
) : BaseView() {
    fun organizationPageForEmployee(
        @SanitizeInput organization: Organization,
        @SanitizeInput organizationMembers: List<CitizenWithDetails>,
        @SanitizeInput organizationReservations: List<BoatSpaceReservationDetails>,
        @SanitizeInput boats: List<CitizenUserController.BoatUpdateForm>,
        @SanitizeInput errors: MutableMap<String, String>? = mutableMapOf(),
    ): String {
        // language=HTML

        val backUrl = "/virkailija/venepaikat/varaukset"

        val result =
            // language=HTML
            """
            <section class="section" id="reserver-details">
                <div class="container block">
                    ${commonComponents.goBackButton(backUrl)} 
                    <h2 class='mb-none'>${organization.name}</h2>
                </div>
                <div class='container block'>
                    <h3>${t("organizationDetails.title.organizationInformation")}</h3>
                    <div id="organization-information">
                      ${organizationContactDetails.render(organization, organizationMembers)}
                    </div>
                </div>
               
                   ${
                reserverDetailsReservationsContainer.render(
                    organization.toReserverWithDetails(),
                    organizationReservations,
                    boats,
                    UserType.EMPLOYEE,
                    ReserverType.Organization,
                )
            }
                
            </section>
            """.trimIndent()

        return result
    }
}
