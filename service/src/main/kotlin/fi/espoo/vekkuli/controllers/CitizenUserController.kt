package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.mToCm
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

@Controller
@RequestMapping("/virkailija")
class CitizenUserController {
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

    @GetMapping("/kayttaja/{citizenId}")
    fun boatSpaceSearchPage(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        model: Model
    ): String {
        val citizen = citizenService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")

        model.addAttribute("citizen", citizen)
        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForCitizen(citizenId)
        model.addAttribute("reservations", boatSpaceReservations)
        val boats = boatService.getBoatsForCitizen(citizenId)
        model.addAttribute("boats", boats.map { toUpdateForm(it) })
        return "employee/citizen-details"
    }

    @GetMapping("/kayttaja/{citizenId}/vene/{boatId}/muokkaa")
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
        return "employee/edit-boat"
    }

    fun toUpdateForm(boat: Boat): BoatUpdateForm =
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
            ownership = boat.ownership
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
    )

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
    fun updateBoatPatch(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable boatId: Int,
        input: BoatUpdateForm,
        model: Model,
        response: HttpServletResponse
    ): String {
        val boats = boatService.getBoatsForCitizen(citizenId)
        val boat = boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")

        val citizen = citizenService.getCitizen(citizenId)
        model.addAttribute("citizen", citizen)

        val errors = validateBoatUpdateInput(input)
        model.addAttribute("errors", errors)

        if (errors.isNotEmpty()) {
            model.addAttribute(
                "boatTypes",
                BoatType.entries.map { it.toString() }
            )
            model.addAttribute("ownershipOptions", listOf("Owner", "User", "CoOwner", "FutureOwner"))
            model.addAttribute("boat", input)

            return "employee/edit-boat"
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
                ownership = input.ownership
            )
        boatService.updateBoat(updatedBoat)

        val boatSpaceReservations = reservationService.getBoatSpaceReservationsForCitizen(citizenId)
        model.addAttribute("reservations", boatSpaceReservations)

        val updatedBoats = boatService.getBoatsForCitizen(citizenId)
        model.addAttribute("boats", updatedBoats.map { toUpdateForm(it) })
        response.addHeader("HX-Retarget", "#citizen-details")
        response.addHeader("HX-Reselect", "#citizen-details")

        return "/employee/citizen-details"
    }
}
