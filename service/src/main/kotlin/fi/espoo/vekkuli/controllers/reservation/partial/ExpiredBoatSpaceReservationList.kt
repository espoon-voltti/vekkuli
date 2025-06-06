package fi.espoo.vekkuli.controllers.reservation.partial

import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.ReserverService
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
    lateinit var reserverService: ReserverService

    @Autowired
    lateinit var partial: ExpiredReservationList

    @GetMapping("/expired-boat-space-reservation-list/{citizenId}")
    @ResponseBody
    fun expiredBoatSpaceReservationListPartial(
        request: HttpServletRequest,
        @PathVariable citizenId: UUID,
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser()
        val isEmployee = authenticatedUser?.isEmployee() == true
        val isAuthorized = isEmployee || authenticatedUser?.id == citizenId

        if (!isAuthorized) {
            throw UnauthorizedException()
        }

        val reservations =
            reservationService.getExpiredBoatSpaceReservationsForReserver(citizenId)

        if (reservations.isEmpty()) {
            return ResponseEntity.ok("")
        }

        return ResponseEntity.ok(
            partial.render(
                reservations,
                if (isEmployee) UserType.EMPLOYEE else UserType.CITIZEN,
                citizenId
            )
        )
    }
}
