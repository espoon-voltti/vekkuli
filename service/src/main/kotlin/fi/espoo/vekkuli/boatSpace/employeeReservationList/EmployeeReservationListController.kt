package fi.espoo.vekkuli.boatSpace.employeeReservationList

import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.BoatSpaceReservationFilter
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.actualAmenities
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.service.BoatSpaceService
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/virkailija/venepaikat")
class EmployeeReservationListController(
    private var boatSpaceService: BoatSpaceService,
    private var reservationListService: EmployeeReservationListService,
    private var boatSpaceReservationRepository: BoatSpaceReservationRepository,
    private var reservationListView: EmployeeReservationListView,
    private var reservationListRowsPartial: ReservationListRowsPartial,
    private var layout: EmployeeLayout,
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/varaukset")
    @ResponseBody
    fun reservationListView(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "EMPLOYEE_RESERVATION_SEARCH")
        }
        val initialPageSize = 50
        val loadMorePageSize = 25

        val reservations =
            reservationListService.getBoatSpaceReservations(params, 0, initialPageSize)

        val harbors =
            boatSpaceReservationRepository.getHarbors()

        val boatSpaceTypes = BoatSpaceType.entries.toList()

        val sections = boatSpaceService.getSections()

        return ResponseEntity.ok(
            layout.render(
                true,
                request.requestURI,
                reservationListView.render(harbors, boatSpaceTypes, actualAmenities, reservations, params, sections, loadMorePageSize)
            )
        )
    }

    @GetMapping("/varaukset/rivit")
    @ResponseBody
    fun reservationListRows(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "EMPLOYEE_RESERVATION_SEARCH_ROWS")
        }

        val reservations =
            reservationListService.getBoatSpaceReservations(params)

        return ResponseEntity.ok(
            reservationListRowsPartial.render(reservations)
        )
    }
}
