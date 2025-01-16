package fi.espoo.vekkuli.boatSpace.organization

import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.service.OrganizationService
import fi.espoo.vekkuli.utils.decimalToInt
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrganizationControllerService(
    private val organizationService: OrganizationService,
    private val boatReservationService: BoatReservationService,
    private val boatService: BoatService,
    private val organizationDetailsView: OrganizationDetailsView,
) {
    fun buildOrganizationPage(organizationId: UUID): String {
        val organization =
            organizationService.getOrganizationById(organizationId) ?: throw IllegalArgumentException("Organization not found")
        val organizationMembers = organizationService.getOrganizationMembers(organizationId)
        val organizationReservations = boatReservationService.getBoatSpaceReservationsForReserver(organizationId)
        val boats =
            boatService.getBoatsForReserver(organizationId).map { toBoatUpdateForm(it, organizationReservations) }
        return organizationDetailsView.organizationPageForEmployee(
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
                widthCm = decimalToInt(input.width!!),
                lengthCm = decimalToInt(input.length!!),
                depthCm = decimalToInt(input.depth!!),
                weightKg = input.weight!!,
                registrationCode = input.registrationNumber,
                otherIdentification = input.otherIdentifier,
                extraInformation = input.extraInformation,
                ownership = input.ownership,
            )
        boatService.updateBoatAsEmployee(updatedBoat)

        val organizationReservations = boatReservationService.getBoatSpaceReservationsForReserver(reserverId)
        val updatedBoats = boatService.getBoatsForReserver(reserverId).map { toBoatUpdateForm(it, organizationReservations) }

        return organizationDetailsView.organizationPageForEmployee(
            organization,
            organizationMembers,
            organizationReservations,
            updatedBoats,
            errors,
        )
    }
}
