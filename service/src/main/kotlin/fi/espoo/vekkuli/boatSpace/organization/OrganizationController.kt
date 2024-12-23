package fi.espoo.vekkuli.boatSpace.organization

import fi.espoo.vekkuli.boatSpace.organization.components.OrganizationContactDetailsEdit
import fi.espoo.vekkuli.boatSpace.organization.components.OrganizationMemberAdd
import fi.espoo.vekkuli.boatSpace.organization.components.OrganizationMembersContainer
import fi.espoo.vekkuli.boatSpace.reservationForm.components.CitizensSearchContent
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.ensureEmployeeId
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.controllers.CitizenUserController.BoatUpdateForm
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.repository.UpdateOrganizationParams
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.service.OrganizationService
import fi.espoo.vekkuli.service.ReserverService
import fi.espoo.vekkuli.utils.intToDecimal
import fi.espoo.vekkuli.views.EditBoat
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
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
        width = intToDecimal(boat.widthCm),
        length = intToDecimal(boat.lengthCm),
        depth = intToDecimal(boat.depthCm),
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
    private val organizationService: OrganizationService,
    private val reserverService: ReserverService,
    private val organizationContactDetailsEdit: OrganizationContactDetailsEdit,
    private val organizationMemberAdd: OrganizationMemberAdd,
    private val citizensSearchContent: CitizensSearchContent,
    private val organizationMembersContainer: OrganizationMembersContainer
) {
    private val logger = KotlinLogging.logger {}

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
        val organizationMembers = organizationService.getOrganizationMembers(organizationId)
        return organizationContactDetailsEdit.render(organization, municipalities, organizationMembers)
    }

    @DeleteMapping("/virkailija/yhteiso/{organizationId}/poista-henkilo/{citizenId}")
    @ResponseBody
    fun removeUserFromOrganization(
        @PathVariable organizationId: UUID,
        @PathVariable citizenId: UUID,
        request: HttpServletRequest
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "UNLINK_CITIZEN_FROM_ORGANIZATION")
        }
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
        @RequestParam billingName: String,
        @RequestParam billingStreetAddress: String,
        @RequestParam billingPostalCode: String,
        @RequestParam billingPostOffice: String,
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
                postOffice = postOffice,
                billingName = billingName,
                billingStreetAddress = billingStreetAddress,
                billingPostalCode = billingPostalCode,
                billingPostOffice = billingPostOffice,
            )
        )
        val page = organizationControllerService.buildOrganizationPage(organizationId)
        return employeeLayout.render(
            true,
            request.requestURI,
            page
        )
    }

    @GetMapping("/virkailija/yhteiso/{organizationId}/jasenet/lisaa")
    @ResponseBody
    fun addMemberToOrganizationView(
        @PathVariable organizationId: UUID,
        @RequestParam citizenId: UUID?,
        request: HttpServletRequest
    ): String {
        var citizen: CitizenWithDetails? = null
        if (citizenId != null) {
            citizen = reserverService.getCitizen(citizenId)
        }

        return organizationMemberAdd.render(citizen, organizationId)
    }

    @PatchMapping("/virkailija/yhteiso/{organizationId}/jasenet/lisaa")
    @ResponseBody
    fun addMemberToOrganization(
        @PathVariable organizationId: UUID,
        @RequestParam citizenId: UUID,
        request: HttpServletRequest
    ): String {
        request.ensureEmployeeId()
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "ADD_ORGANIZATION_MEMBERS")
        }
        organizationService.addCitizenToOrganization(organizationId, citizenId)
        val organizationMembers = organizationService.getOrganizationMembers(organizationId)
        return organizationMembersContainer.render(organizationId, organizationMembers)
    }

    @GetMapping("/virkailija/yhteiso/{organizationId}/jasenet/hae")
    @ResponseBody
    fun searchCitizens(
        @PathVariable organizationId: UUID,
        request: HttpServletRequest,
        @RequestParam nameParameter: String,
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "SEARCH_ORGANIZATION_MEMBERS")
        }
        reserverService.getCitizens(nameParameter).let {
            return organizationMemberAdd.organizationAddMemberSearchContent(it, organizationId)
        }
    }
}
