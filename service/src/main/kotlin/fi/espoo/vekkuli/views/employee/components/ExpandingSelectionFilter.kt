package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class ExpandingSelectionFilter : BaseView() {
    fun sectionCheckbox(section: String) =
        """
        <label class="checkbox dropdown-item" style="margin-bottom:4px;">
            <input type="checkbox" name="sectionFilter" value="$section" x-model="selectedSections" >
            <span>$section</span>
        </label>
        """.trimIndent()

    fun render(
        filter: List<String>,
        modelName: String,
        content: String
    ) = // language=HTML
        """
        <div x-data="{ open: false, $modelName: [${filter.joinToString(",") { "'$it'" }}] }" @click.outside="open = false">
                    <div class="dropdown $modelName" :class="{ 'is-active': open }" ${addTestId(
            "filter-selection-$modelName"
        )} @click="open = !open">
                        <div class="dropdown-trigger">
                            <a aria-haspopup="true" aria-controls="dropdown-menu-$modelName" >
                                <div class="input search-input has-icons-left has-icons-right">
                                    <span class="icon is-small is-left">${icons.filter}</span>
                                    <span class="filter-tag" x-show="$modelName.length > 0" x-text="$modelName.length" style="margin-left:auto"></span>
                                </div>
                            </a>
                        </div>
                        <div class="dropdown-menu filter-dropdown-menu" id="dropdown-menu-$modelName" role="menu">
                            <div>$content</div>
                        </div>
                    </div>
        </div>
        """.trimIndent()
}
