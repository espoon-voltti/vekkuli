package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Organization
import java.util.*

data class UpdateOrganizationParams(
    val id: UUID,
    val businessId: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val streetAddress: String? = null,
    val streetAddressSv: String? = null,
    val postalCode: String? = null,
    val postOffice: String? = null,
    val postOfficeSv: String? = null,
    val municipalityCode: Int? = null
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

    fun getOrganizationsByBusinessId(businessId: String): List<Organization>

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
