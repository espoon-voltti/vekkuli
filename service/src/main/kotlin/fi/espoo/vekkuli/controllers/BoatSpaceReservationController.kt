package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.domain.*
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/virkailija/venepaikat")
class BoatSpaceReservationController {
    @Autowired
    lateinit var jdbi: Jdbi

    @GetMapping("/varaukset")
    fun reservationSearchPage(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter,
        model: Model
    ): String {
        val reservations =
            jdbi.inTransactionUnchecked {
                it.getBoatSpaceReservations(params)
            }
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

    @GetMapping("/varaukset/luo")
    fun reservationCreatePage(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter,
        model: Model
    ): String = "boat-space-reservation-create"

    @GetMapping("/varaukset/{reservationId}")
    fun boatSpaceSearchPage(
        request: HttpServletRequest,
        @PathVariable reservationId: Int,
        model: Model
    ): String {
        val reservation =
            jdbi.inTransactionUnchecked {
                it.getBoatSpaceReservation(reservationId)
            }
        model.addAttribute("reservation", reservation)
        return "boat-space-single-reservation"
    }
}
