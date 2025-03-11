package fi.espoo.vekkuli.boatSpace.boatSpaceList.partials

import fi.espoo.vekkuli.boatSpace.boatSpaceList.BoatSpaceListRow
import fi.espoo.vekkuli.boatSpace.boatSpaceList.components.BoatSpaceRow
import fi.espoo.vekkuli.repository.PaginatedResult
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class BoatSpaceListRowsPartial(
    private val boatSpaceListRow: BoatSpaceRow,
) : BaseView() {
    fun render(boatSpaceRow: PaginatedResult<BoatSpaceListRow>): String =
        buildString {
            for (result in boatSpaceRow.items) {
                append(boatSpaceListRow.render(result))
            }
        }
}
