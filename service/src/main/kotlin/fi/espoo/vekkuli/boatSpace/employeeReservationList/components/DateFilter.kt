package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.DateInputOptions
import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.common.CommonComponents
import org.springframework.stereotype.Component

@Component
class DateFilter(
    private val commonComponents: CommonComponents,
    private val formComponents: FormComponents
) : BaseView() {
    fun render(active: Boolean): String {
        val checked = if (active) " checked" else ""
        val dateInput =
            formComponents.dateInput(
                DateInputOptions(id = "reservationValidFrom", value = "", autoWidth = true)
            )
        val dateInputEnd =
            formComponents.dateInput(
                DateInputOptions(id = "reservationValidUntil", value = "", autoWidth = true)
            )
        //language=HTML
        return """
            <label class="checkbox">
                <input type="checkbox" name="dateFilter" $checked ${addTestId("filter-exceptions")}>
                <span>${t("boatSpaceReservation.showDatesWithin")}</span>
               $dateInput - $dateInputEnd
            </label>
            """.trimIndent()
    }
}
