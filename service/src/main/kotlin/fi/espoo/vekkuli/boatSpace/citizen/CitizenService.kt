package fi.espoo.vekkuli.boatSpace.citizen

import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.service.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
open class CitizenService(
    private val citizenAccessControl: CitizenAccessControl,
    private val reserverService: ReserverService,
    private val organizationService: OrganizationService,
    private val permissionService: PermissionService
) {
    @Transactional
    open fun updateCitizen(citizen: UpdateCitizenInformationInput) {
        val (citizenId) = citizenAccessControl.requireCitizen()
        reserverService.updateCitizen(citizen.toCitizenUpdateInput(citizenId))
    }

    fun getCitizenOrganizationContactPersons(organizationId: UUID): List<CitizenWithDetails> {
        val (citizenId) = citizenAccessControl.requireCitizen()
        if(!permissionService.hasAccessToOrganization(citizenId, organizationId)) {
            throw Forbidden("No access to organization")
        }
        return organizationService.getOrganizationMembers(organizationId)
    }
}
