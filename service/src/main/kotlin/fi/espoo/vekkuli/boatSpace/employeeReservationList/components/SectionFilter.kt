package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.components.ExpandingFilter
import org.springframework.stereotype.Component

@Component
class SectionFilter(
    private val expandingFilter: ExpandingFilter,
) : BaseView() {
    fun render(
        selectedSections: List<String>,
        sections: List<String>,
    ): String =
        expandingFilter.filterDropdown(
            selectedSections,
            "selectedSections",
            sections.joinToString("") { sectionCheckbox(it) }
        )

    private fun sectionCheckbox(section: String) =
        //language=HTML
        """
        <label class="checkbox dropdown-item" style="margin-bottom:4px;">
            <input type="checkbox" name="sectionFilter" value="$section" x-model="selectedSections" >
            <span>$section</span>
        </label>
        """.trimIndent()
}
