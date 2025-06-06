package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.OrganizationRepository
import fi.espoo.vekkuli.repository.UpdateOrganizationParams
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrganizationService(
    private val organizationRepository: OrganizationRepository,
    private val seasonalService: SeasonalService
) {
    fun getCitizenOrganizations(citizenId: UUID): List<Organization> = organizationRepository.getCitizenOrganizations(citizenId)

    fun getOrganizationsForReservation(
        citizenId: UUID,
        reservation: BoatSpaceReservation,
        boatSpaceType: BoatSpaceType
    ): List<Organization> {
        val organizations = getCitizenOrganizations(citizenId)

        return when (reservation.creationType) {
            CreationType.New ->
                organizations.filter {
                    seasonalService.canReserveANewSpace(it.id, boatSpaceType).success
                }
            CreationType.Switch ->
                organizations.filter {
                    reservation.reserverId == it.id
                }
            CreationType.Renewal ->
                organizations.filter {
                    reservation.reserverId == it.id
                }
        }
    }

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

    fun getOrganizationById(organizationId: UUID): Organization? = organizationRepository.getOrganizationById(organizationId)

    fun insertOrganization(
        businessId: String,
        billingName: String,
        billingStreetAddress: String,
        billingPostalCode: String,
        billingPostOffice: String,
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
            billingName,
            billingStreetAddress,
            billingPostalCode,
            billingPostOffice,
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
}
