package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class WarningFilter : BaseView() {
    fun render(
        active: Boolean,
        warningCount: Int
    ): String {
        val checked = if (active) " checked" else ""
        val label = t("boatSpaceReservation.showReservationsWithWarnings", listOf(warningCount.toString()))

        //language=HTML
        return """
            <label class="checkbox">
                <input type="checkbox" name="warningFilter"$checked>
                <span>$label</span>
            </label>
            <span class="ml-s">${icons.warningExclamation(false)}</span>
            """.trimIndent()
    }
}
