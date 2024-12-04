package fi.espoo.vekkuli.controllers.reservation.partial

import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.views.citizen.details.reservation.ExpiredReservationList
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
    lateinit var partial: ExpiredReservationList

    @GetMapping("/expired-boat-space-reservation-list/{citizenId}")
    @ResponseBody
    fun expiredBoatSpaceReservationListPartial(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser()
        val isAuthorized = authenticatedUser?.isEmployee() == true || authenticatedUser?.id == citizenId

        if (!isAuthorized) {
            throw UnauthorizedException()
        }

        val reservations =
            reservationService.getExpiredBoatSpaceReservationsForCitizen(citizenId)

        if (reservations.isEmpty()) {
            return ResponseEntity.ok("")
        }

        return ResponseEntity.ok(
            partial.render(
                reservations
            )
        )
    }
}
