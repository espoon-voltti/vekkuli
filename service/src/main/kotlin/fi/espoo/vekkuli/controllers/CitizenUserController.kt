package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.mToCm
import fi.espoo.vekkuli.views.EditBoat
import fi.espoo.vekkuli.views.employee.CitizenDetails
import fi.espoo.vekkuli.views.employee.EditCitizen
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
@RequestMapping("/virkailija")
class CitizenUserController {
    @Autowired
    private lateinit var editBoat: EditBoat

    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var citizenService: CitizenService

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var boatService: BoatService

    @Autowired
    lateinit var citizenDetails: CitizenDetails

    @Autowired
    lateinit var layout: EmployeeLayout

    @Autowired
    lateinit var editCitizen: EditCitizen

    @GetMapping("/kayttaja/{citizenId}")
    @ResponseBody
    fun boatSpaceSearchPage(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
    ): String {
        val citizen = citizenService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")
        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForCitizen(citizenId)
        val boats = boatService.getBoatsForCitizen(citizenId).map { toUpdateForm(it, boatSpaceReservations) }

        return layout.render(
            true,
            request.requestURI,
            citizenDetails.citizenPage(
                citizen,
                boatSpaceReservations,
                boats,
            )
        )
    }

    @GetMapping("/kayttaja/{citizenId}/varaukset")
    @ResponseBody
    fun boatSpaceReservationContent(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID
    ): String {
        val citizen = citizenService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")
        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForCitizen(citizenId)
        val boats = boatService.getBoatsForCitizen(citizenId).map { toUpdateForm(it, boatSpaceReservations) }
        return citizenDetails.reservationTabContent(citizen, boatSpaceReservations, boats)
    }

    @GetMapping("/kayttaja/{citizenId}/viestit")
    @ResponseBody
    fun boatSpaceMessageContent(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID
    ): String {
        val citizen = citizenService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")
        val messages = citizenService.getMessages(citizenId)
        return citizenDetails.messageTabContent(citizen, messages)
    }

    @GetMapping("/kayttaja/{citizenId}/muistiinpanot")
    @ResponseBody
    fun boatSpaceMemoContent(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID
    ): String {
        val memos = citizenService.getMemos(citizenId, MemoCategory.Marine)
        return citizenDetails.memoTabContent(citizenId, memos)
    }

    @GetMapping("/kayttaja/{citizenId}/muistiinpanot/muokkaa/{memoId}")
    @ResponseBody
    fun boatSpaceMemoEditForm(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable memoId: Int,
    ): String {
        val memo = citizenService.getMemo(memoId) ?: throw IllegalArgumentException("Memo not found")
        return citizenDetails.memoContent(memo, true)
    }

    @GetMapping("/kayttaja/{citizenId}/muistiinpanot/lisaa")
    @ResponseBody
    fun boatSpaceMemoNewForm(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
    ): String = citizenDetails.newMemoContent(citizenId, true)

    @GetMapping("/kayttaja/{citizenId}/muistiinpanot/lisaa_peruuta")
    @ResponseBody
    fun boatSpaceMemoNewCancel(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
    ): String = citizenDetails.newMemoContent(citizenId, false)

    @PostMapping("/kayttaja/{citizenId}/muistiinpanot")
    @ResponseBody
    fun boatSpaceNewMemo(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @RequestParam content: String,
    ): String {
        val userId = request.getAuthenticatedUser()?.id ?: throw IllegalArgumentException("User not found")
        citizenService.insertMemo(citizenId, userId, MemoCategory.Marine, content)
        val memos = citizenService.getMemos(citizenId, MemoCategory.Marine)
        return citizenDetails.memoTabContent(citizenId, memos)
    }

    @DeleteMapping("/kayttaja/{citizenId}/muistiinpanot/{memoId}")
    @ResponseBody
    fun boatSpaceDeleteMemo(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable memoId: Int,
    ): String {
        citizenService.removeMemo(memoId)
        val memos = citizenService.getMemos(citizenId, MemoCategory.Marine)
        return citizenDetails.memoTabContent(citizenId, memos)
    }

    @PatchMapping("/kayttaja/{citizenId}/muistiinpanot/{memoId}")
    @ResponseBody
    fun boatSpaceMemoPatch(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable memoId: Int,
        @RequestParam content: String,
    ): String {
        val userId = request.getAuthenticatedUser()?.id ?: throw IllegalArgumentException("User not found")
        val memo = citizenService.updateMemo(memoId, userId, content) ?: throw IllegalArgumentException("Memo not found")
        return citizenDetails.memoContent(memo, false)
    }

    @GetMapping("/kayttaja/{citizenId}/muistiinpanot/{memoId}")
    @ResponseBody
    fun boatSpaceMemoItem(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable memoId: Int,
    ): String {
        val memo = citizenService.getMemo(memoId) ?: throw IllegalArgumentException("Memo not found")
        return citizenDetails.memoContent(memo, false)
    }

    @GetMapping("/kayttaja/{citizenId}/maksut")
    @ResponseBody
    fun boatSpacePaymentContent(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID
    ): String {
        val citizen = citizenService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")
        return citizenDetails.paymentTabContent(citizen)
    }

    @GetMapping("/kayttaja/{citizenId}/vene/{boatId}/muokkaa")
    @ResponseBody
    fun boatEditPage(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable boatId: Int,
        model: Model
    ): String {
        val boats = boatService.getBoatsForCitizen(citizenId)
        val boat = boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")
        model.addAttribute("boat", toUpdateForm(boat))
        model.addAttribute(
            "boatTypes",
            BoatType.entries.map { it.toString() }
        )

        model.addAttribute("ownershipOptions", listOf("Owner", "User", "CoOwner", "FutureOwner"))

        model.addAttribute("errors", mutableMapOf<String, String>())
        return editBoat.editBoatForm(
            toUpdateForm(boat),
            mutableMapOf(),
            citizenId,
            BoatType.entries.map {
                it.toString()
            },
            listOf("Owner", "User", "CoOwner", "FutureOwner")
        )
    }

    fun toUpdateForm(
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
            reservationId = reservations.find { it.boatId == boat.id }?.id
        )

    data class CitizenUpdate(
        val phoneNumber: String,
        val email: String,
        val address: String?,
        val postalCode: String?,
        val municipalityCode: Int?,
        val nationalId: String?,
        val firstName: String,
        val lastName: String,
    )

    data class BoatUpdateForm(
        val id: Int,
        val name: String,
        val type: BoatType,
        val width: Double?,
        val length: Double?,
        val depth: Double?,
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

    @PatchMapping("/kayttaja/{citizenId}/vene/{boatId}")
    @ResponseBody
    fun updateBoatPatch(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable boatId: Int,
        input: BoatUpdateForm,
        response: HttpServletResponse
    ): String {
        val boats = boatService.getBoatsForCitizen(citizenId)
        val boat = boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")

        val citizen = citizenService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")

        val errors = validateBoatUpdateInput(input)

        if (errors.isNotEmpty()) {
            return editBoat.editBoatForm(
                input,
                errors,
                citizenId,
                BoatType.entries.map {
                    it.toString()
                },
                listOf("Owner", "User", "CoOwner", "FutureOwner")
            )
        }

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

        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForCitizen(citizenId)

        val updatedBoats = boatService.getBoatsForCitizen(citizenId).map { toUpdateForm(it, boatSpaceReservations) }
        response.addHeader("HX-Retarget", "#citizen-details")
        response.addHeader("HX-Reselect", "#citizen-details")

        return layout.render(
            true,
            request.requestURI,
            citizenDetails.citizenPage(
                citizen,
                boatSpaceReservations,
                updatedBoats,
                errors
            )
        )
    }

    @DeleteMapping("/kayttaja/{citizenId}/vene/{boatId}/poista")
    @ResponseBody
    fun deleteBoat(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable boatId: Int,
        response: HttpServletResponse
    ): String {
        val boats = boatService.getBoatsForCitizen(citizenId)
        boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")

        val citizen = citizenService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")

        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForCitizen(citizenId)

        val boatDeletionSuccessful = boatService.deleteBoat(boatId)
        // Update the boat list to remove the deleted boat
        val updatedBoats =
            boats
                .map { toUpdateForm(it, boatSpaceReservations) }
                .filter { !boatDeletionSuccessful || it.id != boatId }

        response.addHeader("HX-Retarget", "#citizen-details")
        response.addHeader("HX-Reselect", "#citizen-details")

        return layout.render(
            true,
            request.requestURI,
            citizenDetails.citizenPage(
                citizen,
                boatSpaceReservations,
                updatedBoats,
            )
        )
    }

    @GetMapping("/kayttaja/{citizenId}/muokkaa")
    @ResponseBody
    fun citizenEditPage(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        model: Model
    ): String {
        val citizen = citizenService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")
        val municipalities = citizenService.getMunicipalities()
        return editCitizen.editCitizenForm(citizen, municipalities, emptyMap())
    }

    @PatchMapping("/kayttaja/{citizenId}")
    @ResponseBody
    fun citizenEdit(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        input: CitizenUpdate,
        model: Model
    ): String {
        val citizen = citizenService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")
        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForCitizen(citizenId)

        val boats = boatService.getBoatsForCitizen(citizenId).map { toUpdateForm(it, boatSpaceReservations) }
        val updatedCitizen =
            citizenService.updateCitizen(
                citizenId,
                input.firstName,
                input.lastName,
                input.phoneNumber,
                input.email,
                input.address,
                input.postalCode,
                input.municipalityCode,
                input.nationalId,
                // TODO when sending these from the form
                // TODO use `input` instead of `citizen`
                citizen.addressSv,
                citizen.postOffice,
                citizen.postOfficeSv,
            )
        return layout.render(
            true,
            request.requestURI,
            citizenDetails.citizenPage(updatedCitizen, boatSpaceReservations, boats)
        )
    }
}
