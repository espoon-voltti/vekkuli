package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.domain.BoatSpaceReservationFilterColumn
import fi.espoo.vekkuli.domain.ReservationWarning
import fi.espoo.vekkuli.domain.ReservationWarningType
import fi.espoo.vekkuli.domain.WarningFilterType
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class GeneralWarningFilter : BaseView() {
    fun render(
        active: Boolean,
        warnings: List<ReservationWarning>
    ): String {
        val generalWarningsCount = warnings.filter { it -> it.key == ReservationWarningType.GeneralReservationWarning }.size.toString()
        val label = t("boatSpaceReservation.showReservationsWithManualWarnings", listOf(generalWarningsCount))

        //language=HTML
        return """
            
             <label class="checkbox">
                <input type="checkbox" 
                    name="generalWarningFilter"
                    ${addTestId("filter-warnings")}
                    :checked="$active"
                >
                <span>$label</span>
            </label>
            <span class="ml-s with-asterisk">${icons.warningExclamation(false)}</span>
            """.trimIndent()
    }
}
