package fi.espoo.vekkuli.controllers.reservation.partial

import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.views.citizen.details.reservation.ExpiredReservationList
import fi.espoo.vekkuli.views.citizen.details.reservation.ReservationList
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
@RequestMapping("/reservation/partial")
class ExpiredBoatSpaceReservationList {
    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var citizenService: CitizenService

    @Autowired
    lateinit var reservationList: ExpiredReservationList

    @Autowired
    lateinit var partial: ReservationList

    @GetMapping("/expired-boat-space-reservation-list/{citizenId}")
    @ResponseBody
    fun expiredBoatSpaceReservationListPartial(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
    ): ResponseEntity<String> {
        /*
        val authenticatedUser = request.getAuthenticatedUser()
        if (authenticatedUser == null) {
            return throw Unauthorized()
        }
         */

        val reservations =
            reservationService.getExpiredBoatSpaceReservationsForCitizen(citizenId)

        val citizen = citizenService.getCitizen(citizenId) ?: throw IllegalArgumentException("Citizen not found")

        return ResponseEntity.ok(
            reservationList.render(
                citizen,
                reservations
            )
        )
    }
}
