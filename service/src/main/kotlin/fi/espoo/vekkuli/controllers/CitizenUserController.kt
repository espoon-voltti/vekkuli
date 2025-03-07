package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.boatSpace.organization.OrganizationDetailsView
import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.ensureEmployeeId
import fi.espoo.vekkuli.config.getAuthenticatedEmployee
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Routes.Companion.USERTYPE
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.JdbiReserverRepository
import fi.espoo.vekkuli.repository.UpdateCitizenParams
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.decimalToInt
import fi.espoo.vekkuli.utils.intToDecimal
import fi.espoo.vekkuli.utils.reservationStatusToText
import fi.espoo.vekkuli.views.EditBoat
import fi.espoo.vekkuli.views.citizen.Layout
import fi.espoo.vekkuli.views.citizen.details.reservation.TrailerCard
import fi.espoo.vekkuli.views.employee.CitizenDetails
import fi.espoo.vekkuli.views.employee.EditCitizen
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import fi.espoo.vekkuli.views.employee.components.*
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Controller
class CitizenUserController(
    private val organizationDetailsView: OrganizationDetailsView,
    private val organizationService: OrganizationService,
    private val reserverDetailsReservationsContainer: ReserverDetailsReservationsContainer,
    private val reserverDetailsMessagesContainer: ReserverDetailsMessagesContainer,
    private val reserverDetailsMemoContainer: ReserverDetailsMemoContainer,
    private val reserverDetailsExceptionsContainer: ReserverDetailsExceptionsContainer,
    private val editBoat: EditBoat,
    private val messageUtil: MessageUtil,
    private val reserverService: ReserverService,
    private val memoService: MemoService,
    private val reservationService: BoatReservationService,
    private val boatService: BoatService,
    private val citizenDetails: CitizenDetails,
    private val employeeLayout: EmployeeLayout,
    private val citizenLayout: Layout,
    private val reserverRepository: JdbiReserverRepository,
    private val trailerCard: TrailerCard,
    private val editCitizen: EditCitizen,
    private val paymentService: PaymentService,
    private val sentMessageModalView: SentMessageModalView
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/virkailija/kayttaja/{citizenId}")
    @ResponseBody
    fun citizenProfile(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE", mapOf("targetId" to citizenId.toString()))
        }
        val citizen = reserverService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")

        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(citizenId)
        val boats = boatService.getBoatsForReserver(citizenId).map { toBoatUpdateForm(it, boatSpaceReservations) }
        val organizations = organizationService.getCitizenOrganizations(citizenId)

        return employeeLayout.render(
            true,
            request.requestURI,
            citizenDetails.citizenPage(
                citizen,
                boatSpaceReservations,
                boats,
                organizations,
                UserType.EMPLOYEE,
                ReserverType.Citizen,
            )
        )
    }

    @GetMapping("/kuntalainen/omat-tiedot")
    @ResponseBody
    fun ownProfile(request: HttpServletRequest): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE")
        }
        val citizen = getAuthenticatedCitizen(request)
        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(citizen.id)
        val boats = boatService.getBoatsForReserver(citizen.id).map { toBoatUpdateForm(it, boatSpaceReservations) }
        val organizations = organizationService.getCitizenOrganizations(citizen.id)

        return citizenLayout.render(
            true,
            citizen.fullName,
            request.requestURI,
            citizenDetails.citizenPage(
                citizen,
                boatSpaceReservations,
                boats,
                organizations,
                UserType.CITIZEN,
                ReserverType.Citizen,
            )
        )
    }

    fun getAuthenticatedCitizen(request: HttpServletRequest): CitizenWithDetails {
        val authenticatedUser = request.getAuthenticatedUser()
        val citizen = authenticatedUser?.let { reserverService.getCitizen(it.id) }
        if (citizen == null) {
            throw UnauthorizedException()
        }
        return citizen
    }

    @GetMapping("/virkailija/kayttaja/{citizenId}/varaukset")
    @ResponseBody
    fun boatSpaceReservationContent(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_RESERVATIONS")
        }
        val reserver = reserverService.getReserverById(citizenId) ?: throw IllegalArgumentException("Citizen not found")
        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(citizenId)
        val boats = boatService.getBoatsForReserver(citizenId).map { toBoatUpdateForm(it, boatSpaceReservations) }
        return reserverDetailsReservationsContainer.render(
            reserver,
            boatSpaceReservations,
            boats,
            UserType.EMPLOYEE,
            reserver.type
        )
    }

    @GetMapping("/virkailija/kayttaja/{citizenId}/viestit")
    @ResponseBody
    fun boatSpaceMessageContent(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_MESSAGES", mapOf("targetId" to citizenId.toString()))
        }
        val reserver =
            reserverRepository.getReserverById(citizenId) ?: throw IllegalArgumentException("Reserver not found")
        val messages = reserverService.getMessages(citizenId)
        return reserverDetailsMessagesContainer.messageTabContent(reserver, messages)
    }

    @GetMapping("/virkailija/kayttaja/{citizenId}/viestit/{messageId}")
    @ResponseBody
    fun citizenMessageContent(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable messageId: UUID
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_MESSAGES", mapOf("targetId" to citizenId.toString()))
        }
        val message = reserverService.getMessage(messageId)
        return sentMessageModalView.render(message)
    }

    @GetMapping("/virkailija/kayttaja/{citizenId}/muistiinpanot")
    @ResponseBody
    fun boatSpaceMemoContent(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_MEMOS", mapOf("targetId" to citizenId.toString()))
        }
        val reserver =
            reserverRepository.getReserverById(citizenId) ?: throw IllegalArgumentException("Reserver not found")
        val memos = memoService.getMemos(citizenId)
        return reserverDetailsMemoContainer.tabContent(reserver, memos)
    }

    @GetMapping("/virkailija/kayttaja/{citizenId}/muistiinpanot/muokkaa/{memoId}")
    @ResponseBody
    fun boatSpaceMemoEditForm(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable memoId: Int,
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_MEMOS_EDIT", mapOf("targetId" to citizenId.toString(), "memoId" to memoId.toString()))
        }
        val memo = memoService.getMemo(memoId) ?: throw IllegalArgumentException("Memo not found")
        return reserverDetailsMemoContainer.memoContent(memo, true)
    }

    @GetMapping("/virkailija/kayttaja/{citizenId}/muistiinpanot/lisaa")
    @ResponseBody
    fun boatSpaceMemoNewForm(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_MEMOS_ADD_NEW_FORM", mapOf("targetId" to citizenId.toString()))
        }
        return reserverDetailsMemoContainer.newMemoContent(citizenId, true)
    }

    @GetMapping("/virkailija/kayttaja/{citizenId}/muistiinpanot/lisaa_peruuta")
    @ResponseBody
    fun boatSpaceMemoNewCancel(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_MEMOS_CANCEL", mapOf("targetId" to citizenId.toString()))
        }
        return reserverDetailsMemoContainer.newMemoContent(citizenId, false)
    }

    @PostMapping("/virkailija/kayttaja/{citizenId}/muistiinpanot")
    @ResponseBody
    fun boatSpaceNewMemo(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @RequestParam content: String,
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_MEMOS_ADD_NEW", mapOf("targetId" to citizenId.toString()))
        }
        val userId = request.getAuthenticatedUser()?.id ?: throw IllegalArgumentException("User not found")
        val reserver =
            reserverRepository.getReserverById(citizenId) ?: throw IllegalArgumentException("Reserver not found")
        memoService.insertMemo(citizenId, userId, content)
        val memos = memoService.getMemos(citizenId)
        return reserverDetailsMemoContainer.tabContent(reserver, memos)
    }

    @DeleteMapping("/virkailija/kayttaja/{citizenId}/muistiinpanot/{memoId}")
    @ResponseBody
    fun boatSpaceDeleteMemo(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable memoId: Int,
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_MEMOS_DELETE", mapOf("targetId" to citizenId.toString(), "memoId" to memoId.toString()))
        }
        val reserver =
            reserverRepository.getReserverById(citizenId) ?: throw IllegalArgumentException("Reserver not found")
        memoService.removeMemo(memoId)
        val memos = memoService.getMemos(citizenId)
        return reserverDetailsMemoContainer.tabContent(reserver, memos)
    }

    @PatchMapping("/virkailija/kayttaja/{citizenId}/muistiinpanot/{memoId}")
    @ResponseBody
    fun boatSpaceMemoPatch(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable memoId: Int,
        @RequestParam content: String,
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "CITIZEN_PROFILE_MEMOS_UPDATE",
                mapOf(
                    "targetId" to citizenId.toString(),
                    "memoId" to memoId.toString(),
                    "content" to content
                )
            )
        }
        val userId = request.getAuthenticatedUser()?.id ?: throw IllegalArgumentException("User not found")
        val memo = memoService.updateMemo(memoId, userId, content) ?: throw IllegalArgumentException("Memo not found")
        return reserverDetailsMemoContainer.memoContent(memo, false)
    }

    @GetMapping("/virkailija/kayttaja/{citizenId}/muistiinpanot/{memoId}")
    @ResponseBody
    fun boatSpaceMemoItem(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable memoId: Int,
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_MEMOS_ITEM", mapOf("targetId" to citizenId.toString(), "memoId" to memoId.toString()))
        }
        val memo = memoService.getMemo(memoId) ?: throw IllegalArgumentException("Memo not found")
        return reserverDetailsMemoContainer.memoContent(memo, false)
    }

    @GetMapping("/virkailija/kayttaja/{citizenId}/maksut")
    @ResponseBody
    fun boatSpacePaymentContent(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_PAYMENTS", mapOf("targetId" to citizenId.toString()))
        }
        val reserver = reserverRepository.getReserverById(citizenId) ?: throw IllegalArgumentException("Reserver not found")
        val history = paymentService.getReserverPaymentHistory(reserver.id)

        return reserverDetailsReservationsContainer.paymentTabContent(reserver, history)
    }

    @GetMapping("/virkailija/kayttaja/{reserverId}/poikkeukset")
    @ResponseBody
    fun boatSpaceExceptionContent(
        request: HttpServletRequest,
        @PathVariable reserverId: UUID
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "RESERVER_PROFILE_EXEPTIONS", mapOf("targetId" to reserverId.toString()))
        }
        val reserver =
            reserverRepository.getReserverById(reserverId) ?: throw IllegalArgumentException("Reserver not found")
        return reserverDetailsExceptionsContainer.tabContent(reserver)
    }

    @PatchMapping("/virkailija/kayttaja/{reserverId}/poikkeukset/toggle-espoo-rules-applied")
    @ResponseBody
    fun boatSpaceEspooRulesAppliedPatch(
        request: HttpServletRequest,
        @PathVariable reserverId: UUID,
    ): String {
        val reserver = reserverService.toggleEspooRulesApplied(reserverId) ?: throw IllegalArgumentException("Reserver not found")
        val espooRulesAppliedChangedTo = !reserver.espooRulesApplied
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "RESERVER_PROFILE_ESPOO_RULES_APPLIED__UPDATE",
                mapOf(
                    "targetId" to reserverId.toString(),
                    "espoo_rules_applied" to espooRulesAppliedChangedTo.toString()
                )
            )
        }
        return reserverDetailsExceptionsContainer.tabContent(reserver)
    }

    @PatchMapping("/virkailija/kayttaja/{reserverId}/poikkeukset/discount")
    @ResponseBody
    fun boatSpaceDiscountPatch(
        request: HttpServletRequest,
        @PathVariable reserverId: UUID,
        @RequestParam discountPercentage: Int,
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "RESERVER_PROFILE_ESPOO_RULES_APPLIED__UPDATE",
                mapOf("discount_percentage" to discountPercentage.toString())
            )
        }
        val reserver =
            reserverRepository.updateDiscount(
                reserverId,
                discountPercentage
            ) ?: throw IllegalArgumentException("Reserver not found")
        return reserverDetailsExceptionsContainer.tabContent(reserver)
    }

    @GetMapping("/virkailija/kayttaja/{citizenId}/vene/{boatId}/muokkaa")
    @ResponseBody
    fun boatEditPageForEmployee(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable boatId: Int,
        model: Model
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_EDIT_BOAT_FORM", mapOf("targetId" to citizenId.toString(), "boatId" to boatId.toString()))
        }
        val boats = boatService.getBoatsForReserver(citizenId)
        val boat = boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")
        return editBoat.editBoatForm(
            toBoatUpdateForm(boat),
            mutableMapOf(),
            citizenId,
            BoatType.entries.map {
                it.toString()
            },
            listOf("Owner", "User", "CoOwner", "FutureOwner"),
            UserType.EMPLOYEE
        )
    }

    @GetMapping("/kuntalainen/vene/{boatId}/muokkaa")
    @ResponseBody
    fun boatEditPage(
        request: HttpServletRequest,
        @PathVariable boatId: Int,
        model: Model
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_EDIT_BOAT_FORM", mapOf("targetId" to boatId.toString()))
        }
        val citizen = getAuthenticatedCitizen(request)
        val citizenId = citizen.id
        val boats = boatService.getBoatsForReserver(citizenId)
        val boat = boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")
        return editBoat.editBoatForm(
            toBoatUpdateForm(boat),
            mutableMapOf(),
            citizenId,
            BoatType.entries.map {
                it.toString()
            },
            listOf("Owner", "User", "CoOwner", "FutureOwner"),
            UserType.CITIZEN
        )
    }

    @GetMapping("/$USERTYPE/{citizenId}/traileri/{trailerId}/muokkaa")
    @ResponseBody
    fun trailerEditPage(
        @PathVariable usertype: String,
        @PathVariable citizenId: UUID,
        @PathVariable trailerId: Int,
        request: HttpServletRequest
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "CITIZEN_PROFILE_EDIT_TRAILER_FORM",
                mapOf(
                    "targetId" to citizenId.toString(),
                    "trailerId" to trailerId.toString()
                )
            )
        }
        val userType = UserType.fromPath(usertype)
        val trailer = reservationService.getTrailer(trailerId)
        if (trailer == null) {
            throw IllegalArgumentException("Trailer not found")
        }
        return trailerCard.renderEdit(trailer, userType, citizenId)
    }

    @PatchMapping("/$USERTYPE/{citizenId}/traileri/{trailerId}/tallenna")
    @ResponseBody
    fun trailerSavePage(
        @PathVariable usertype: String,
        @PathVariable citizenId: UUID,
        @PathVariable trailerId: Int,
        @RequestParam trailerRegistrationCode: String,
        @RequestParam trailerWidth: BigDecimal,
        @RequestParam trailerLength: BigDecimal,
        request: HttpServletRequest
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "CITIZEN_PROFILE_EDIT_TRAILER_SAVE",
                mapOf(
                    "targetId" to citizenId.toString(),
                    "trailerId" to trailerId.toString()
                )
            )
        }
        val userType = UserType.fromPath(usertype)
        val user = request.getAuthenticatedUser() ?: throw Unauthorized()
        val trailer =
            reservationService.updateTrailer(
                user.id,
                trailerId,
                trailerRegistrationCode,
                trailerWidth,
                trailerLength
            )
        return trailerCard.render(trailer, userType, citizenId)
    }

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

    data class CitizenUpdate(
        val phoneNumber: String? = null,
        val email: String? = null,
        val address: String? = null,
        val postalCode: String? = null,
        val municipalityCode: Int? = null,
        val nationalId: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val city: String? = null,
    )

    data class BoatUpdateForm(
        val id: Int,
        val name: String,
        val type: BoatType,
        val width: BigDecimal?,
        val length: BigDecimal?,
        val depth: BigDecimal?,
        val weight: Int?,
        val registrationNumber: String,
        val otherIdentifier: String,
        val extraInformation: String,
        val ownership: OwnershipStatus,
        val warnings: Set<String> = emptySet(),
        val reservationId: Int? = null,
    ) {
        fun hasWarning(warning: String): Boolean = warnings.contains(warning)

        fun hasAnyWarnings(): Boolean = warnings.isNotEmpty()
    }

    fun validateBoatUpdateInput(input: BoatUpdateForm): MutableMap<String, String> {
        val errors = mutableMapOf<String, String>()
        if (input.width == null) {
            errors["width"] = messageUtil.getMessage("validation.required")
        }
        if (input.length == null) {
            errors["length"] = messageUtil.getMessage("validation.required")
        }
        if (input.depth == null) {
            errors["depth"] = messageUtil.getMessage("validation.required")
        }
        if (input.weight == null) {
            errors["weight"] = messageUtil.getMessage("validation.required")
        }
        return errors
    }

    @PatchMapping("/virkailija/kayttaja/{citizenId}/vene/{boatId}")
    @ResponseBody
    fun updateBoatForEmployee(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable boatId: Int,
        input: BoatUpdateForm,
        response: HttpServletResponse
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_EDIT_BOAT_SAVE", mapOf("targetId" to citizenId.toString(), "boatId" to boatId.toString()))
        }
        val boats = boatService.getBoatsForReserver(citizenId)
        val boat = boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")

        val citizen = reserverService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")

        val organizations = organizationService.getCitizenOrganizations(citizenId)

        val errors = validateBoatUpdateInput(input)

        if (errors.isNotEmpty()) {
            return editBoat.editBoatForm(
                input,
                errors,
                citizenId,
                BoatType.entries.map {
                    it.toString()
                },
                listOf("Owner", "User", "CoOwner", "FutureOwner"),
                UserType.EMPLOYEE
            )
        }

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

        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(citizenId)

        val updatedBoats =
            boatService.getBoatsForReserver(citizenId).map { toBoatUpdateForm(it, boatSpaceReservations) }

        return citizenDetails.citizenPage(
            citizen,
            boatSpaceReservations,
            updatedBoats,
            organizations,
            UserType.EMPLOYEE,
            ReserverType.Citizen,
            errors,
        )
    }

    @PatchMapping("/kuntalainen/vene/{boatId}")
    @ResponseBody
    fun updateBoatPatch(
        request: HttpServletRequest,
        @PathVariable boatId: Int,
        input: BoatUpdateForm,
        response: HttpServletResponse
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_EDIT_BOAT_SAVE", mapOf("targetId" to boatId.toString()))
        }
        val citizen = getAuthenticatedCitizen(request)
        val citizenId = citizen.id
        val boats = boatService.getBoatsForReserver(citizenId)
        val boat = boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")

        val organizations = organizationService.getCitizenOrganizations(citizenId)

        val errors = validateBoatUpdateInput(input)

        if (errors.isNotEmpty()) {
            return editBoat.editBoatForm(
                input,
                errors,
                citizenId,
                BoatType.entries.map {
                    it.toString()
                },
                listOf("Owner", "User", "CoOwner", "FutureOwner"),
                UserType.CITIZEN
            )
        }

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
        boatService.updateBoatAsCitizen(updatedBoat)

        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(citizenId)

        val updatedBoats =
            boatService.getBoatsForReserver(citizenId).map { toBoatUpdateForm(it, boatSpaceReservations) }

        return citizenDetails.citizenPage(
            citizen,
            boatSpaceReservations,
            updatedBoats,
            organizations,
            UserType.CITIZEN,
            ReserverType.Citizen,
            errors,
        )
    }

    @DeleteMapping("/virkailija/kayttaja/{citizenId}/vene/{boatId}/poista")
    @ResponseBody
    fun deleteBoatFromCitizenAsEmployee(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable boatId: Int,
        response: HttpServletResponse
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_DELETE_BOAT", mapOf("targetId" to citizenId.toString(), "boatId" to boatId.toString()))
        }
        val boats = boatService.getBoatsForReserver(citizenId)
        boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")

        val citizen = reserverService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")

        val organizations = organizationService.getCitizenOrganizations(citizenId)

        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(citizenId)

        val boatDeletionSuccessful = boatService.deleteBoat(boatId)
        // Update the boat list to remove the deleted boat
        val updatedBoats =
            boats
                .map { toBoatUpdateForm(it, boatSpaceReservations) }
                .filter { !boatDeletionSuccessful || it.id != boatId }

        return citizenDetails.citizenPage(
            citizen,
            boatSpaceReservations,
            updatedBoats,
            organizations,
            UserType.EMPLOYEE,
            ReserverType.Citizen,
        )
    }

    @DeleteMapping("/virkailija/yhteiso/{organizationId}/vene/{boatId}/poista")
    @ResponseBody
    fun deleteBoatFromOrganizationAsEmployee(
        request: HttpServletRequest,
        @PathVariable organizationId: UUID,
        @PathVariable boatId: Int,
        response: HttpServletResponse
    ): String {
        val boats = boatService.getBoatsForReserver(organizationId)
        boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")

        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(organizationId)

        val boatDeletionSuccessful = boatService.deleteBoat(boatId)
        // Update the boat list to remove the deleted boat
        val updatedBoats =
            boats
                .map { toBoatUpdateForm(it, boatSpaceReservations) }
                .filter { !boatDeletionSuccessful || it.id != boatId }

        return reserverPage(boatSpaceReservations, updatedBoats, organizationId)
    }

    @DeleteMapping("/kuntalainen/vene/{boatId}/poista")
    @ResponseBody
    fun deleteBoatAsCitizen(
        request: HttpServletRequest,
        @PathVariable boatId: Int,
        response: HttpServletResponse
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_EDIT_TRAILER_SAVE", mapOf("targetId" to boatId.toString()))
        }
        val citizen = getAuthenticatedCitizen(request)
        val citizenId = citizen.id

        val boats = boatService.getBoatsForReserver(citizenId)
        boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")

        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(citizenId)

        val organizations = organizationService.getCitizenOrganizations(citizenId)

        val boatDeletionSuccessful = boatService.deleteBoat(boatId)
        // Update the boat list to remove the deleted boat
        val updatedBoats =
            boats
                .map { toBoatUpdateForm(it, boatSpaceReservations) }
                .filter { !boatDeletionSuccessful || it.id != boatId }

        return citizenDetails.citizenPage(
            citizen,
            boatSpaceReservations,
            updatedBoats,
            organizations,
            UserType.CITIZEN,
            ReserverType.Citizen,
        )
    }

    @GetMapping("/virkailija/kayttaja/{citizenId}/muokkaa")
    @ResponseBody
    fun citizenEditPageForEmployee(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        model: Model
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_EDIT_CITIZEN_FORM", mapOf("targetId" to citizenId.toString()))
        }
        val citizen = reserverService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")
        val municipalities = reserverService.getMunicipalities()
        return editCitizen.editCitizenForm(citizen, municipalities, emptyMap(), UserType.EMPLOYEE)
    }

    @GetMapping("/kuntalainen/kayttaja/muokkaa")
    @ResponseBody
    fun citizenEditPage(
        request: HttpServletRequest,
        model: Model
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_EDIT_CITIZEN_FORM")
        }
        val citizen = getAuthenticatedCitizen(request)
        val municipalities = reserverService.getMunicipalities()
        return editCitizen.editCitizenForm(citizen, municipalities, emptyMap(), UserType.CITIZEN)
    }

    @PatchMapping("/virkailija/kayttaja/{citizenId}")
    @ResponseBody
    fun citizenEdit(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        input: CitizenUpdate,
        model: Model
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_EDIT_CITIZEN_SAVE", mapOf("targetId" to citizenId.toString()))
        }
        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(citizenId)
        val organizations = organizationService.getCitizenOrganizations(citizenId)

        val boats = boatService.getBoatsForReserver(citizenId).map { toBoatUpdateForm(it, boatSpaceReservations) }
        val updatedCitizen = updateCitizen(input, citizenId)
        return citizenDetails.citizenPage(
            updatedCitizen,
            boatSpaceReservations,
            boats,
            organizations,
            UserType.EMPLOYEE,
            ReserverType.Citizen
        )
    }

    data class UpdateInput(
        val phoneNumber: String,
        val email: String,
    )

    @PatchMapping("/kuntalainen/omat-tiedot")
    @ResponseBody
    fun editOwnProfile(
        request: HttpServletRequest,
        input: UpdateInput,
        model: Model
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PROFILE_EDIT_CITIZEN_SAVE", mapOf("targetId" to it.id.toString()))
        }
        val citizen = getAuthenticatedCitizen(request)
        val citizenId = citizen.id
        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(citizenId)
        val organizations = organizationService.getCitizenOrganizations(citizenId)

        val boats = boatService.getBoatsForReserver(citizenId).map { toBoatUpdateForm(it, boatSpaceReservations) }
        val citizenUpdate =
            CitizenUpdate(
                phoneNumber = input.phoneNumber,
                email = input.email,
            )
        val updatedCitizen = updateCitizen(citizenUpdate, citizenId)
        return citizenDetails.citizenPage(
            updatedCitizen,
            boatSpaceReservations,
            boats,
            organizations,
            UserType.CITIZEN,
            ReserverType.Citizen
        )
    }

    fun updateCitizen(
        input: CitizenUpdate,
        citizenId: UUID
    ) = reserverService.updateCitizen(
        UpdateCitizenParams(
            id = citizenId,
            firstName = input.firstName,
            lastName = input.lastName,
            phone = input.phoneNumber,
            email = input.email,
            streetAddress = input.address,
            streetAddressSv = input.address,
            postalCode = input.postalCode,
            municipalityCode = input.municipalityCode,
            nationalId = input.nationalId,
            postOffice = input.city,
            postOfficeSv = input.city
        )
    )!!

    @PostMapping("/virkailija/venepaikat/varaukset/status")
    fun updateReservationStatus(
        @RequestParam reservationId: Int,
        @RequestParam reservationStatus: ReservationStatus,
        @RequestParam paymentDate: LocalDate,
        @RequestParam paymentStatusText: String,
        @RequestParam reserverId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val user = request.getAuthenticatedEmployee()
        logger.audit(
            user,
            "CITIZEN_PROFILE_UPDATE_PAYMENT_STATUS",
            mapOf(
                "targetId" to reservationId.toString(),
                "reservationStatus" to reservationStatus.toString(),
                "paymentDate" to paymentDate.toString(),
                "paymentStatusText" to paymentStatusText,
                "reserverId" to reserverId.toString()
            )
        )

        reservationService.updateReservationStatus(
            reservationId,
            reservationStatus,
            paymentDate.atStartOfDay(),
            paymentStatusText
        )

        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(reserverId)
        val boats = boatService.getBoatsForReserver(reserverId).map { toBoatUpdateForm(it, boatSpaceReservations) }

        val memoContent = "Varauksen tila: ${reservationStatusToText(reservationStatus)}  $paymentDate: $paymentStatusText"

        memoService.insertMemo(reserverId, user.id, memoContent)

        return ResponseEntity.ok(reserverPage(boatSpaceReservations, boats, reserverId))
    }

    @PostMapping("/virkailija/venepaikat/varaukset/kuittaa-varoitus")
    fun ackWarning(
        @RequestParam("boatId") boatId: Int,
        @RequestParam("key") key: List<String>,
        @RequestParam("infoText") infoText: String,
        @RequestParam("reserverId") reserverId: UUID,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "CITIZEN_PROFILE_ACK_WARNING",
                mapOf(
                    "targetId" to reserverId.toString(),
                    "boatId" to boatId.toString(),
                    "key" to key.toString(),
                    "reserverId" to reserverId.toString()
                )
            )
        }
        val userId = request.ensureEmployeeId()
        reservationService.acknowledgeWarningForBoat(boatId, userId, key, infoText)
        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(reserverId)
        val boats = boatService.getBoatsForReserver(reserverId).map { toBoatUpdateForm(it, boatSpaceReservations) }
        return ResponseEntity.ok(reserverPage(boatSpaceReservations, boats, reserverId))
    }

    @PostMapping("/virkailija/kayttaja/{citizenId}/maksut/{paymentId}/hyvita")
    @ResponseBody
    fun markPaymentAsRefunded(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable paymentId: UUID
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_PAYMENT_MARK_AS_REFUNDED", mapOf("targetId" to paymentId.toString()))
        }

        paymentService.getPayment(paymentId)?.let {
            paymentService.updatePayment(it.copy(status = PaymentStatus.Refunded))
        } ?: throw RuntimeException("Payment not found")

        return redirectUrl("/virkailija/kayttaja/$citizenId")
    }

    @PostMapping("/virkailija/venepaikat/varaukset/kuittaa-traileri-varoitus")
    fun ackTrailerWarning(
        @RequestParam("reserverId") reserverId: UUID,
        @RequestParam("trailerId") trailerId: Int,
        @RequestParam("key") key: List<String>,
        @RequestParam("infoText") infoText: String,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "CITIZEN_PROFILE_ACK_WARNING",
                mapOf(
                    "targetId" to reserverId.toString(),
                    "trailerId" to trailerId.toString(),
                    "key" to key.toString(),
                    "infoText" to infoText
                )
            )
        }
        val userId = request.ensureEmployeeId()

        reservationService.acknowledgeWarningForTrailer(trailerId, userId, key, infoText)
        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForReserver(reserverId)
        val boats = boatService.getBoatsForReserver(reserverId).map { toBoatUpdateForm(it, boatSpaceReservations) }
        return ResponseEntity.ok(reserverPage(boatSpaceReservations, boats, reserverId))
    }

    fun reserverPage(
        boatSpaceReservations: List<BoatSpaceReservationDetails>,
        boats: List<BoatUpdateForm>,
        reserverId: UUID,
    ): String {
        val citizen = reserverService.getCitizen(reserverId)
        if (citizen != null) {
            val organizations = organizationService.getCitizenOrganizations(reserverId)
            return citizenDetails.citizenPage(
                citizen,
                boatSpaceReservations,
                boats,
                organizations,
                UserType.EMPLOYEE,
                ReserverType.Citizen,
            )
        } else {
            val organization =
                organizationService.getOrganizationById(reserverId)
                    ?: throw IllegalArgumentException("Reserver not found")
            return organizationDetailsView.organizationPageForEmployee(
                organization,
                organizationService.getOrganizationMembers(reserverId),
                boatSpaceReservations,
                boats
            )
        }
    }
}
