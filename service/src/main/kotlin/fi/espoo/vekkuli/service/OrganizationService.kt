package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Organization
import fi.espoo.vekkuli.repository.OrganizationRepository
import fi.espoo.vekkuli.repository.UpdateOrganizationParams
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrganizationService(
    private val organizationRepository: OrganizationRepository
) {
    fun getCitizenOrganizations(citizenId: UUID): List<Organization> = organizationRepository.getCitizenOrganizations(citizenId)

    fun getOrganizationMembers(organizationId: UUID): List<CitizenWithDetails> =
        organizationRepository.getOrganizationMembers(organizationId)

    fun addCitizenToOrganization(
        organizationId: UUID,
        citizenId: UUID
    ): Unit = organizationRepository.addCitizenToOrganization(organizationId, citizenId)

    fun removeCitizenFromOrganization(
        organizationId: UUID,
        citizenId: UUID
    ): Unit = organizationRepository.removeCitizenFromOrganization(organizationId, citizenId)

    fun getOrganizationsByBusinessId(businessId: String): List<Organization> =
        organizationRepository.getOrganizationsByBusinessId(businessId)

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
    ): Organization =
        organizationRepository.insertOrganization(
            businessId,
            name,
            phone,
            email,
            streetAddress,
            streetAddressSv,
            postalCode,
            postOffice,
            postOfficeSv,
            municipalityCode
        )

    fun updateOrganization(params: UpdateOrganizationParams): Unit = organizationRepository.updateOrganization(params)

    fun updateOrCreateOrganization(
        organizationId: UUID?,
        businessId: String?,
        name: String?,
        phone: String?,
        email: String?,
        address: String?,
        postalCode: String?,
        city: String?,
        municipalityCode: String?,
        citizenId: UUID,
    ): UUID {
        if (organizationId == null) {
            // add new organization
            val newOrg =
                this.insertOrganization(
                    businessId = businessId ?: "",
                    name = name ?: "",
                    phone = phone ?: "",
                    email = email ?: "",
                    streetAddress = address ?: "",
                    streetAddressSv = address ?: "",
                    postalCode = postalCode ?: "",
                    postOffice = city ?: "",
                    postOfficeSv = city ?: "",
                    municipalityCode = (municipalityCode ?: "1").toInt()
                )
            // add person to anization
            this.addCitizenToOrganization(newOrg.id, citizenId)
            return newOrg.id
        } else {
            // update organization
            this.updateOrganization(
                UpdateOrganizationParams(
                    id = organizationId,
                    businessId = businessId,
                    name = name,
                    phone = phone,
                    email = email,
                    streetAddress = address,
                    streetAddressSv = address,
                    postalCode = postalCode,
                    postOffice = city,
                    postOfficeSv = city,
                    municipalityCode = municipalityCode.let { it?.toInt() }
                )
            )
            return organizationId
        }
    }
}
