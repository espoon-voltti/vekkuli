package fi.espoo.vekkuli.boatSpace.organization

import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.service.OrganizationService
import fi.espoo.vekkuli.utils.mToCm
import fi.espoo.vekkuli.views.organization.OrganizationDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrganizationControllerService(
    private val organizationService: OrganizationService,
    private val boatReservationService: BoatReservationService,
    private val boatService: BoatService,
    private val organizationDetails: OrganizationDetails,
) {
    fun buildOrganizationPage(organizationId: UUID): String {
        val organization = organizationService.getOrganizationById(organizationId) ?: return "Organization not found"
        val organizationMembers = organizationService.getOrganizationMembers(organizationId)
        val organizationReservations = boatReservationService.getBoatSpaceReservationsForCitizen(organizationId)
        val boats =
            boatService.getBoatsForReserver(organizationId).map { toBoatUpdateForm(it, organizationReservations) }
        return organizationDetails.organizationPageForEmployee(
            organization,
            organizationMembers,
            organizationReservations,
            boats
        )
    }

    fun buildOrganizationUpdatedPage(
        reserverId: UUID,
        boatId: Int,
        input: CitizenUserController.BoatUpdateForm,
        errors: MutableMap<String, String>
    ): String {
        val boats = boatService.getBoatsForReserver(reserverId)
        val boat = boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")

        val organization = organizationService.getOrganizationById(reserverId) ?: throw IllegalArgumentException("Organization not found")
        val organizationMembers = organizationService.getOrganizationMembers(reserverId)
        val updatedBoat =
            boat.copy(
                name = input.name,
                type = input.type,
                widthCm = input.width!!.mToCm(),
                lengthCm = input.length!!.mToCm(),
                depthCm = input.depth!!.mToCm(),
                weightKg = input.weight!!,
                registrationCode = input.registrationNumber,
                otherIdentification = input.otherIdentifier,
                extraInformation = input.extraInformation,
                ownership = input.ownership,
            )
        boatService.updateBoat(updatedBoat)

        val organizationReservations = boatReservationService.getBoatSpaceReservationsForCitizen(reserverId)
        val updatedBoats = boatService.getBoatsForReserver(reserverId).map { toBoatUpdateForm(it, organizationReservations) }

        return organizationDetails.organizationPageForEmployee(
            organization,
            organizationMembers,
            organizationReservations,
            updatedBoats,
            errors,
        )
    }
}
