package fi.espoo.vekkuli.boatSpace.employeeReservationList

import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.ReservationListRow
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class ReservationListRowsPartial(
    private val reservationListRow: ReservationListRow,
) : BaseView() {
    fun render(reservations: PaginatedReservationsResult<BoatSpaceReservationItem>,): String =
        buildString {
            for (result in reservations.items) {
                append(reservationListRow.render(result))
            }
        }
}
