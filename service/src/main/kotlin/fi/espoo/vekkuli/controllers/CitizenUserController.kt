package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.domain.BoatSpaceReservation
import fi.espoo.vekkuli.domain.Citizen
import fi.espoo.vekkuli.domain.getBoatsForCitizen
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

@Controller
@RequestMapping("/virkailija")
class CitizenUserController {
    @Autowired
    lateinit var jdbi: Jdbi

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
        return "citizen-details"
    }
}
