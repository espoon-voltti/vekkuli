package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceReservationFilter
import fi.espoo.vekkuli.domain.getLocations
import fi.espoo.vekkuli.service.BoatReservationService
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/virkailija/venepaikat")
class BoatSpaceReservationController {
    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var reservationService: BoatReservationService

    @GetMapping("/varaukset")
    fun reservationSearchPage(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter,
        model: Model
    ): String {
        val reservations =
            reservationService.getBoatSpaceReservations(params)

        val harbors =
            jdbi.inTransactionUnchecked {
                it.getLocations()
            }

        model.addAttribute("reservations", reservations)
        model.addAttribute("params", params)
        model.addAttribute("harbors", harbors)
        model.addAttribute("amenities", BoatSpaceAmenity.entries.toList())
        return "boat-space-reservation-list"
    }

    @PostMapping("/varaukset/kuittaa-varoitus")
    fun ackWarning(
        @RequestParam("reservationId") reservationId: Int,
        @RequestParam("boatId") boatId: Int,
        @RequestParam("key") key: String
    ): ResponseEntity<Void> {
        reservationService.acknowledgeWarning(reservationId, boatId, key)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/varaukset/luo")
    fun reservationCreatePage(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter,
        model: Model
    ): String = "boat-space-reservation-create"
}
