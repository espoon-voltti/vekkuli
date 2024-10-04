package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Organization
import java.util.*

data class UpdateOrganizationParams(
    val id: UUID,
    val businessId: String?,
    val name: String?,
    val phone: String?,
    val email: String?,
    val streetAddress: String?,
    val streetAddressSv: String?,
    val postalCode: String?,
    val postOffice: String?,
    val postOfficeSv: String?,
    val municipalityCode: Int?
)

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

    fun getOrganizationById(id: UUID): Organization?

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

    fun updateOrganization(params: UpdateOrganizationParams): Unit
}
