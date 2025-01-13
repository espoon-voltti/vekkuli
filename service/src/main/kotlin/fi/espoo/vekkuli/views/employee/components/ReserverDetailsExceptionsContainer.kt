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
            <div class="espoo-rules-applied checkbox">            
                <input type="checkbox" id="edit-espoorules-applied-checkbox"
                    ${if (reserver.espooRulesApplied) "checked" else "" }
                    hx-patch="${reserverDetailsTabs.getTabUrl("${reserver.id}/poikkeukset/toggle-espoo-rules-applied")}"
                    hx-trigger="click"
                    hx-target="#tab-content"
                    hx-swap="outerHTML"
                >                
                <span>${t("employee.reserverDetails.exceptions.espooExplanation")}</span>                
            </div>
            """.trimIndent()
    }

    private fun discountContent(reserver: ReserverWithDetails): String {
        // language=HTML
        return """
            <div class="discounts">
                <div class="radio">            
                    <input type="radio" id="reserver_discount_0" name="reserverDiscount" value='0'
                        ${if (reserver.discountPercentage == 0) "checked" else ""}
                        hx-patch="${reserverDetailsTabs.getTabUrl("${reserver.id}/poikkeukset/discount")}"
                        hx-trigger="click"
                        hx-target="#tab-content"
                        hx-swap="outerHTML">
                        <label for="reserver_discount_0" >${t("employee.reserverDetails.exceptions.discount_0")}</label>
                </div>
                <div class="radio">
                    <input type="radio" id="reserver_discount_50" name="reserverDiscount" value='50'
                        ${if (reserver.discountPercentage == 50) "checked" else ""}
                        hx-patch="${reserverDetailsTabs.getTabUrl("${reserver.id}/poikkeukset/discount")}"
                        hx-trigger="click"
                        hx-target="#tab-content"
                        hx-swap="outerHTML">
                        <label for="reserver_discount_50" >${t("employee.reserverDetails.exceptions.discount_50")}</label>
                </div>
                <div class="radio">
                    <input type="radio" id="reserver_discount_100" name="reserverDiscount" value='100'
                        ${if (reserver.discountPercentage == 100) "checked" else ""}
                        hx-patch="${reserverDetailsTabs.getTabUrl("${reserver.id}/poikkeukset/discount")}"
                        hx-trigger="click"
                        hx-target="#tab-content"
                        hx-swap="outerHTML">
                        <label for="reserver_discount_100" >${t("employee.reserverDetails.exceptions.discount_100")}</label>
                </div>
                </div>
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
                <label class="label">${t("employee.reserverDetails.exceptions.discountTitle")}</label>
                ${discountContent(reserver)}
              </div>              
            </div>
            """.trimIndent()
    }
}
