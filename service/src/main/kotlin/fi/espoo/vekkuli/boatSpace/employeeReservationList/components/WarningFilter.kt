package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.domain.BoatSpaceReservationFilterColumn
import fi.espoo.vekkuli.domain.ReservationWarning
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class WarningFilter : BaseView() {
    fun render(
        active: Boolean,
        warnings: List<ReservationWarning>
    ): String {
        val warningCount = warnings.distinctBy { it.reservationId }.size.toString()
        val label = t("boatSpaceReservation.showReservationsWithWarnings", listOf(warningCount))

        //language=HTML
        val onChange = """if (event.target.checked) {
                          sortColumn = '${BoatSpaceReservationFilterColumn.WARNING_CREATED}';
                          sortDirection = 'false';
                          document.getElementById('sortColumn').value = sortColumn;
                          document.getElementById('sortDirection').value = sortDirection;
                        }"""

        //language=HTML
        return """
            <label class="checkbox mr-s">
                <input type="checkbox" 
                    name="warningFilter"
                    ${addTestId("filter-warnings")}
                    :checked="$active"
                    @change="$onChange"
                >
                <span>$label</span>
            </label>
            <span class="ml-s">${icons.warningExclamation(false)}</span>
            """.trimIndent()
    }
}
