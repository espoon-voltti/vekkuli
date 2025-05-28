package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.domain.BoatSpaceReservationFilterColumn
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.AlertLevel
import org.springframework.stereotype.Component

@Component
class WarningFilter : BaseView() {
    fun render(
        active: Boolean,
        warningCount: Int
    ): String {
        val label = t("boatSpaceReservation.showReservationsWithWarnings", listOf(warningCount.toString()))

        //language=HTML
        return """
            <label class="checkbox">
                <input type="checkbox" 
                    name="warningFilter"
                    ${addTestId("filter-warnings")}
                    :checked="$active"
                    @change="
                        if (event.target.checked) {
                          sortColumn = '${BoatSpaceReservationFilterColumn.WARNING_CREATED}';
                          sortDirection = 'false';
                          document.getElementById('sortColumn').value = sortColumn;
                          document.getElementById('sortDirection').value = sortDirection;
                          nextTick(() => {
                            document.getElementById('reservation-table-header')
                              .dispatchEvent(new Event('change', { bubbles: true }));
                          });
                        }
                    ">
                <span>$label</span>
            </label>
            <span class="ml-s">${icons.warningExclamation(AlertLevel.SystemWarning)}</span>
            """.trimIndent()
    }
}
