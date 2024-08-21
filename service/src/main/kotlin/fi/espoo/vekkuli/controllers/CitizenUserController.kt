package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.BoatSpaceReservation
import fi.espoo.vekkuli.domain.Citizen
import fi.espoo.vekkuli.domain.getBoatsForCitizen
import fi.espoo.vekkuli.domain.updateBoat
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
import org.springframework.web.servlet.ModelAndView
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
        model.addAttribute("errors", mutableMapOf<String, String>())
        return "employee/edit-boat"
    }

    data class BoatUpdateInput(val name: String)

    fun validateBoatUpdateInput(input: BoatUpdateInput): MutableMap<String, String> {
        val errors = mutableMapOf<String, String>()
        if (input.name.isBlank()) {
            errors["name"] = messageUtil.getMessage("validation.required")
        }
        return errors
    }

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
        val errors = validateBoatUpdateInput(input)
        if (errors.isNotEmpty()) {
            model.addAttribute("errors", errors)
            return ModelAndView("employee/edit-boat", model.asMap(), HttpStatus.OK)
        }

        val updatedBoat = boat.copy(name = input.name)
        updateBoat(updatedBoat, jdbi)

        val headers = HttpHeaders()
        headers.add("HX-Redirect", "/virkailija/kayttaja/$citizenId")

        return ResponseEntity<Any>(headers, HttpStatus.OK)
    }
}
