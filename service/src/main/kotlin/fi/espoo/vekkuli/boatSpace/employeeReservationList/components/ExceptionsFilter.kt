package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class ExceptionsFilter : BaseView() {
    fun render(active: Boolean): String {
        val checked = if (active) " checked" else ""

        //language=HTML
        return """
            <label class="checkbox">
                <input type="checkbox" name="exceptionsFilter"$checked ${addTestId("filter-exceptions")}>
                <span>${t("boatSpaceReservation.showReservationsWithReserverExceptions")}</span>
            </label>
            """.trimIndent()
    }
}
