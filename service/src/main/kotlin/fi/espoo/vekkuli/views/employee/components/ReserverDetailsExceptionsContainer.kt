package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.config.DomainConstants
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.SubTab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ReserverDetailsExceptionsContainer : BaseView() {
    @Autowired
    private lateinit var formComponents: FormComponents

    @Autowired
    lateinit var reserverDetailsTabs: ReserverDetailsTabs

    private fun espooRulesAppliedInput(reserver: ReserverWithDetails): String {
        // language=HTML
        return """
<div class="espoo-rules-applied checkbox">            
    <input type="checkbox" name='espooRulesApplied' id="espooRulesApplied"  ${if (reserver.espooRulesApplied) "checked" else "" } ${addTestId(
            "edit-espoorules-applied-checkbox"
        )}>                
    <span>${t(
            "employee.reserverDetails.exceptions.espooExplanation"
        )}</span>                
</div>
            """.trimIndent()
    }

    private fun espooRulesAppliedContent(reserver: ReserverWithDetails): String {
        // language=HTML
        return """
            <div class="espoo-rules-applied checkbox">           
                <input type="checkbox" id="espoorules-applied-checkbox" ${addTestId("espoorules-applied-checkbox")}
                    ${if (reserver.espooRulesApplied) "checked" else "" }
                    disabled
                    />
                <span>${t("employee.reserverDetails.exceptions.espooExplanation")}</span>
            </div>
            """.trimIndent()
    }

    private fun discountContentRow(
        discount: Int,
        reserver: ReserverWithDetails
    ): String {
        // language=HTML
        return """
            <div class="radio">            
                <input type="radio" id="reserver_discount_$discount" 
                ${addTestId("reserver_discount_$discount")} name="discountPercentage" value='$discount'
                    ${if (reserver.discountPercentage == discount) "checked" else ""}>
                    <label for="reserver_discount_0" >${t("employee.reserverDetails.exceptions.discount_$discount")}</label>
            </div>
            """.trimIndent()
    }

    private fun discountInput(reserver: ReserverWithDetails): String {
        // language=HTML
        return """
            <div class="discounts">
                ${DomainConstants.DISCOUNTS.joinToString("\n") { discount -> discountContentRow(discount, reserver) }}
            </div>
            """.trimIndent()
    }

    private fun discountContent(reserver: ReserverWithDetails): String {
        // language=HTML
        return """                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ${formComponents.field(
            labelKey = "employee.reserverDetails.exceptions.discountTitle",
            id = "discount",
            value = t("employee.reserverDetails.exceptions.discount_${reserver.discountPercentage}")
        )}
            """.trimIndent()
    }

    private fun descriptionInput(reserver: ReserverWithDetails): String {
        val expectionNotesInput =
            formComponents.textInput(
                labelKey = "employee.reserverDetails.exceptions.descriptionTitle",
                id = "expectionNotes",
                value = reserver.exceptionNotes ?: "",
            )
        // language=HTML
        return """
            <div class="description">
                $expectionNotesInput               
            </div>
            """.trimIndent()
    }

    private fun expectionNotesContent(reserver: ReserverWithDetails): String =
        formComponents.field(
            labelKey = "employee.reserverDetails.exceptions.descriptionTitle",
            id = "expectionNotes",
            value = reserver.exceptionNotes,
        )

    fun tabContent(reserver: ReserverWithDetails): String {
        // language=HTML
        return """
            <div id="tab-content" class="container block">
              ${reserverDetailsTabs.renderTabNavi(reserver, SubTab.Exceptions)}
              <div class="exceptions-container columns">
                  <div class='column'>
                       <label class="label">${t("employee.reserverDetails.exceptions.espooTitle")}</label>
                       <div class='mb-l'>
                        ${espooRulesAppliedContent(reserver)}
                        </div>
                        <div class='mb-l'>
                        ${discountContent(reserver)}
                        </div>
                        <div class='mb-l'>
                        ${expectionNotesContent(reserver)}
                        </div>
                  </div>
                   <div class="column is-narrow ml-auto">
                        <a class="is-link is-icon-link" 
                            ${addTestId("exceptions-edit")}    
                            id="edit-customer"
                            hx-get="/virkailija/kayttaja/${reserver.id}/poikkeukset/muokkaa"
                            hx-target="#tab-content"
                            hx-swap="outerHTML">
                            <span class="icon">
                                ${icons.edit}
                            </span>
                            <span>${t("reserverDetails.button.editExceptions")}</span>
                        </a>
                    </div>  
              </div>              
            </div>
            """.trimIndent()
    }

    fun tabEdit(reserver: ReserverWithDetails): String {
        // language=HTML
        return """
            <div id="tab-content" class="container block">
              ${reserverDetailsTabs.renderTabNavi(reserver, SubTab.Exceptions)}
              <form id="expection-edit-form" class="exceptions-container">
                <label class="label">${t("employee.reserverDetails.exceptions.espooTitle")}</label>
                <div class='mb-l'>
                ${espooRulesAppliedInput(reserver)}
                </div>
                <label class="label">${t("employee.reserverDetails.exceptions.discountTitle")}</label>
                <div class='mb-l'>
                ${discountInput(reserver)}
                </div>
                <div class='mb-l'>
                ${descriptionInput(reserver)}
                </div>
                <a ${addTestId("exceptions-cancel")}
                            class="button is-secondary" 
                            type="button"
                            hx-get=${reserverDetailsTabs.getTabUrl("${reserver.id}/poikkeukset")}
                            hx-target="#tab-content"
                            hx-select="#tab-content"
                            hx-swap="outerHTML">
                    ${t("cancel")}
                    </a>
                <a class="button is-primary" ${addTestId("exceptions-submit")} 
                    hx-post="${reserverDetailsTabs.getTabUrl("${reserver.id}/poikkeukset/muokkaa")}"
                    hx-include="#expection-edit-form"
                    hx-trigger="click"
                    hx-target="#tab-content"
                    hx-select="#tab-content"
                    hx-swap="outerHTML">
                    ${t("citizenDetails.saveChanges")}
                    </a>
              </form>              
            </div>
            """.trimIndent()
    }
}
