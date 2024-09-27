package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Organization
import java.util.*

interface OrganizationRepository {
    fun getCitizenOrganizations(citizenId: UUID): List<Organization>

    fun getOrganizationMembers(organizationId: UUID): List<CitizenWithDetails>

    fun addCitizenToOrganization(
        organizationId: UUID,
        citizenId: UUID
    ): Unit

    fun removeCitizenFromOrganization(
        organizationId: UUID,
        citizenId: UUID
    ): Unit

    fun getOrganizationByBusinessId(businessId: String): Organization?

    fun insertOrganization(
        businessId: String,
        name: String,
        phone: String,
        email: String,
        streetAddress: String,
        streetAddressSv: String,
        postalCode: String,
        postOffice: String,
        postOfficeSv: String,
        municipalityCode: Int
    ): Organization
}
