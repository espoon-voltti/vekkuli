package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class TextSearchFilter : BaseView() {
    fun render(
        name: String,
        inputVal: String?
    ): String {
        //language=HTML
        return """
            <p class="control has-icons-left">
                 <input 
                    class="input search-input"
                    type="text"
                    name="$name"
                    aria-label="${t("boatSpaces.searchButton")}"
                    value="${inputVal ?: ""}" 
                    ${addTestId("search-input-$name")} />                
                 <span class="icon is-small is-left">${icons.search}</span>                
             </p>
            """.trimIndent()
    }
}
