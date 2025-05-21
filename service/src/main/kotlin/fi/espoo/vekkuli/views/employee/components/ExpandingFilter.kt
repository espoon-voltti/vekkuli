package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component
import org.springframework.web.util.HtmlUtils.htmlEscape

data class FilterOption(
    val value: String,
    val label: String
)

@Component
class ExpandingFilter : BaseView() {
    fun optionCheckbox(
        value: String,
        label: String,
        name: String,
        modelName: String
    ) = """
        <label class="checkbox dropdown-item" style="margin-bottom:4px;">
            <input type="checkbox" name="$name" value="$value" x-model="$modelName" >
            <span>$label</span>
        </label>
        """.trimIndent()

    fun filterDropdown(
        filter: List<Any>,
        modelName: String,
        content: String
    ) = // language=HTML
        """
        <div x-data="{ open: false, $modelName: [${filter.joinToString(",") { "'${htmlEscape(it.toString())}'" }}] }" @click.outside="open = false">
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

    fun render(
        options: List<FilterOption>,
        filter: List<Any>,
        filterName: String,
        modelName: String,
    ): String = filterDropdown(filter, modelName, options.joinToString("\n") { optionCheckbox(it.value, it.label, filterName, modelName) })
}
