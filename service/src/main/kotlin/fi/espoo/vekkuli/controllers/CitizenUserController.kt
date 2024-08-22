package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.mToCm
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

    @GetMapping("/kayttaja/{citizenId}")
    fun boatSpaceSearchPage(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        model: Model
    ): String {
        val citizen = Citizen.getById(citizenId, jdbi)

        model.addAttribute("citizen", citizen)
        val boatSpaceReservations = BoatSpaceReservation.getReservationsForCitizen(citizenId, jdbi)
        model.addAttribute("reservations", boatSpaceReservations)
        val boats = getBoatsForCitizen(citizenId, jdbi)
        model.addAttribute("boats", boats)
        return "employee/citizen-details"
    }

    @GetMapping("/kayttaja/{citizenId}/vene/{boatId}/muokkaa")
    fun boatEditPage(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable boatId: Int,
        model: Model
    ): String {
        val boats = getBoatsForCitizen(citizenId, jdbi)
        val boat = boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")
        model.addAttribute("boat", boat)
        model.addAttribute(
            "boatTypes",
            BoatType.entries.map { it.toString() }
        )

        model.addAttribute("ownershipOptions", listOf("Owner", "User", "CoOwner", "FutureOwner"))

        model.addAttribute("errors", mutableMapOf<String, String>())
        return "employee/edit-boat"
    }

    data class BoatUpdateInput(
        val name: String,
        val boatType: BoatType,
        val width: Double,
        val length: Double,
        val depth: Double,
        val weight: Int,
        val registrationNumber: String,
        val otherIdentifier: String,
        val extraInformation: String,
        val ownership: OwnershipStatus,
    )

    @PatchMapping("/kayttaja/{citizenId}/vene/{boatId}")
    fun updateBoatPatch(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
        @PathVariable boatId: Int,
        input: BoatUpdateInput,
        model: Model
    ): Any {
        val boats = getBoatsForCitizen(citizenId, jdbi)
        val boat = boats.find { it.id == boatId } ?: throw IllegalArgumentException("Boat not found")
        model.addAttribute("boat", boat)

        val updatedBoat =
            boat.copy(
                name = input.name,
                type = input.boatType,
                widthCm = input.width.mToCm(),
                lengthCm = input.length.mToCm(),
                depthCm = input.depth.mToCm(),
                weightKg = input.weight,
                registrationCode = input.registrationNumber,
                otherIdentification = input.otherIdentifier,
                extraInformation = input.extraInformation,
                ownership = input.ownership
            )
        updateBoat(updatedBoat, jdbi)

        val headers = HttpHeaders()
        headers.add("HX-Redirect", "/virkailija/kayttaja/$citizenId")

        return ResponseEntity<Any>(headers, HttpStatus.OK)
    }
}
