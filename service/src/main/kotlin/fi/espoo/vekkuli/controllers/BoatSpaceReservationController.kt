package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.domain.BoatSpaceFilterColumn
import fi.espoo.vekkuli.domain.BoatSpaceSort
import fi.espoo.vekkuli.domain.getBoatSpaceReservations
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/virkailija/venepaikat")
class BoatSpaceReservationController {
    @Autowired
    lateinit var jdbi: Jdbi

    @GetMapping("/varaukset")
    fun boatSpaceSearchPage(
        request: HttpServletRequest,
        @RequestParam sortBy: BoatSpaceFilterColumn?,
        @RequestParam asc: Boolean?,
        model: Model
    ): String {
        val sort = BoatSpaceSort(sortBy ?: BoatSpaceFilterColumn.START_DATE, asc ?: false)
        val reservations =
            jdbi.inTransactionUnchecked {
                it.getBoatSpaceReservations(sort)
            }
        model.addAttribute("reservations", reservations)
        model.addAttribute("sort", sort)
        return "boat-space-reservation-list"
    }
}
