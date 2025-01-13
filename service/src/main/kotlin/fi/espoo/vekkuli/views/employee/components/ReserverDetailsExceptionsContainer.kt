package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.SubTab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ReserverDetailsExceptionsContainer : BaseView() {
    @Autowired
    lateinit var reserverDetailsTabs: ReserverDetailsTabs

    private fun espooRulesAppliedContent(reserver: ReserverWithDetails): String {
        // language=HTML
        return """
            <label class="checkbox">            
                <input type="checkbox" id="edit-espoorules-applied-button"
                    ${if (reserver.espooRulesApplied) "checked" else "" }
                    hx-patch="${reserverDetailsTabs.getTabUrl("${reserver.id}/poikkeukset/toggle-espoo-rules-applied")}"
                    hx-trigger="click"
                    hx-target="#tab-content"
                    hx-swap="outerHTML"
                >                
                <span>${t("employee.reserverDetails.exceptions.espooExplanation")}</span>                
            </label>
            """.trimIndent()
    }

    fun tabContent(reserver: ReserverWithDetails): String {
        // language=HTML
        return """
            <div id="tab-content" class="container block">
              ${reserverDetailsTabs.renderTabNavi(reserver, SubTab.Exceptions)}
              <div class="exceptions-container">
                <label class="label">${t("employee.reserverDetails.exceptions.espooTitle")}</label>
                ${espooRulesAppliedContent(reserver)}
              </div>              
            </div>
            """.trimIndent()
    }
}
