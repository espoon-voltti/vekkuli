package fi.espoo.vekkuli.boatSpace.organization

import fi.espoo.vekkuli.boatSpace.organization.components.OrganizationContactDetails
import fi.espoo.vekkuli.boatSpace.organization.components.OrganizationContactDetailsEdit
import fi.espoo.vekkuli.config.ensureEmployeeId
import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.controllers.CitizenUserController.BoatUpdateForm
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.repository.UpdateOrganizationParams
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.service.OrganizationService
import fi.espoo.vekkuli.service.PermissionService
import fi.espoo.vekkuli.service.ReserverService
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.views.EditBoat
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*

fun toBoatUpdateForm(
    boat: Boat,
    reservations: List<BoatSpaceReservationDetails> = emptyList()
): BoatUpdateForm =
    BoatUpdateForm(
        id = boat.id,
        name = boat.name ?: "",
        type = boat.type,
        width = boat.widthCm.cmToM(),
        length = boat.lengthCm.cmToM(),
        depth = boat.depthCm.cmToM(),
        weight = boat.weightKg,
        registrationNumber = boat.registrationCode ?: "",
        otherIdentifier = boat.otherIdentification ?: "",
        extraInformation = boat.extraInformation ?: "",
        ownership = boat.ownership,
        warnings = boat.warnings,
        reservationId = reservations.find { it.boat?.id == boat.id }?.id
    )

@Controller
class OrganizationUserController(
    private val employeeLayout: EmployeeLayout,
    private val boatService: BoatService,
    private val editBoat: EditBoat,
    private val organizationControllerService: OrganizationControllerService,
    private val citizenUserController: CitizenUserController,
    private val organizationContactDetails: OrganizationContactDetails,
    private val organizationService: OrganizationService,
    private val reserverService: ReserverService,
    private val organizationContactDetailsEdit: OrganizationContactDetailsEdit,
    private val permissionService: PermissionService
) {
    @GetMapping("/virkailija/yhteiso/{organizationId}")
    @ResponseBody
    fun citizenProfile(
        request: HttpServletRequest,
        @PathVariable organizationId: UUID,
    ): String {
        val page = organizationControllerService.buildOrganizationPage(organizationId)
        return employeeLayout.render(
            true,
            request.requestURI,
            page
        )
    }

    @GetMapping("/virkailija/yhteiso/{reserverId}/vene/{boatId}/muokkaa")
    @ResponseBody
    fun boatEditPageForEmployee(
        request: HttpServletRequest,
        @PathVariable reserverId: UUID,
        @PathVariable boatId: Int,
        model: Model
    ): String {
        val boats = boatService.getBoatsForReserver(reserverId)
        val boat = boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")
        return editBoat.editBoatForm(
            toBoatUpdateForm(boat),
            mutableMapOf(),
            reserverId,
            BoatType.entries.map {
                it.toString()
            },
            listOf("Owner", "User", "CoOwner", "FutureOwner"),
            UserType.EMPLOYEE
        )
    }

    @PatchMapping("/virkailija/yhteiso/{reserverId}/vene/{boatId}")
    @ResponseBody
    fun updateBoatForEmployee(
        request: HttpServletRequest,
        @PathVariable reserverId: UUID,
        @PathVariable boatId: Int,
        input: BoatUpdateForm,
        response: HttpServletResponse
    ): String {
        val errors = citizenUserController.validateBoatUpdateInput(input)

        if (errors.isNotEmpty()) {
            return editBoat.editBoatForm(
                input,
                errors,
                reserverId,
                BoatType.entries.map {
                    it.toString()
                },
                listOf("Owner", "User", "CoOwner", "FutureOwner"),
                UserType.EMPLOYEE
            )
        }
        return organizationControllerService.buildOrganizationUpdatedPage(reserverId, boatId, input, errors)
    }

    @GetMapping("/yhteiso/kayttaja/{organizationId}/muokkaa")
    @ResponseBody
    fun editOrganizationInformation(
        @PathVariable organizationId: UUID
    ): String {
        val organization =
            organizationService.getOrganizationById(organizationId)
                ?: throw IllegalArgumentException("Organization not found")

        val municipalities = reserverService.getMunicipalities()
        return organizationContactDetailsEdit.render(organization, municipalities)
    }

    @DeleteMapping("/virkailija/yhteiso/{organizationId}/poista-henkilo/{citizenId}")
    @ResponseBody
    fun removeUserFromOrganization(
        @PathVariable organizationId: UUID,
        @PathVariable citizenId: UUID,
        request: HttpServletRequest
    ) {
        request.ensureEmployeeId()
        organizationService.removeCitizenFromOrganization(organizationId, citizenId)
    }

    @PatchMapping("/virkailija/yhteiso/{organizationId}/muokkaa")
    @ResponseBody
    fun updateOrganizationInformation(
        @PathVariable organizationId: UUID,
        @RequestParam organizationName: String,
        @RequestParam businessId: String,
        @RequestParam municipalityCode: Int,
        @RequestParam phoneNumber: String,
        @RequestParam email: String,
        @RequestParam address: String,
        @RequestParam postOffice: String,
        @RequestParam postalCode: String,
        request: HttpServletRequest
    ): String {
        organizationService.updateOrganization(
            UpdateOrganizationParams(
                id = organizationId,
                name = organizationName,
                businessId = businessId,
                municipalityCode = municipalityCode,
                phone = phoneNumber,
                email = email,
                streetAddress = address,
                postalCode = postalCode,
                postOffice = postOffice
            )
        )
        val page = organizationControllerService.buildOrganizationPage(organizationId)
        return employeeLayout.render(
            true,
            request.requestURI,
            page
        )
    }
}
