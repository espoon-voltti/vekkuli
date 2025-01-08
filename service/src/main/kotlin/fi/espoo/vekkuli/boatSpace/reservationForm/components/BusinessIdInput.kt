package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.domain.Organization
import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.WarningBox
import org.springframework.stereotype.Component

@Component
class BusinessIdInput(
    private val markDownService: MarkDownService,
    private val warningBox: WarningBox
) : BaseView() {
    fun render(allowDuplicateIds: Boolean): String {
        val allowedDuplicateInfo =
            """
            hx-post="/info/businessid" 
            hx-trigger="keyup changed delay:500ms" 
            hx-target="#orgBusinessId-server-info" 
            hx-params="orgBusinessId"
            """.trimIndent()

        // language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label required" for="orgBusinessId" >${t("boatApplication.organizationId")}</label>
                    <input
                        class="input"
                        data-required
                        ${if (allowDuplicateIds) allowedDuplicateInfo else """data-validate-url="/validate/businessid"""" }
                        type="text"
                        id="orgBusinessId"
                        name="orgBusinessId" />
                    <div id="orgBusinessId-info-container">
                    </div>

                    <div id="orgBusinessId-error-container">
                        <span id="orgBusinessId-error" class="help is-danger" style="display: none">
                            ${t("validation.required")}
                        </span>
                    </div>
                </div>
            </div>
            """.trimIndent()
    }

    fun infoBox(
        organizations: List<Organization>,
        businessId: String
    ): String {
        val firstParagraph = messageUtil.getMessage("warning.businessId1", listOf(businessId))
        val secondParagraph = markDownService.render(t("info.businessId2"))

        return warningBox.render(duplicateOrgIdContent(firstParagraph, secondParagraph, organizations))
    }

    fun warning(
        organizations: List<Organization>,
        businessId: String
    ): String {
        val firstParagraph = messageUtil.getMessage("warning.businessId1", listOf(businessId))
        val secondParagraph = markDownService.render(t("warning.businessId2"))

        return warningBox.render(duplicateOrgIdContent(firstParagraph, secondParagraph, organizations))
    }

    private fun duplicateOrgIdContent(
        firstParagraph: String,
        secondParagraph: String,
        organizations: List<Organization>
    ): String {
        val orgList = organizations.joinToString("") { "<li>${it.name}</li>" }
        // language=HTML
        return """
            <p>$firstParagraph</p>
            <ul class="bullets" style="margin: 12px">
               $orgList 
            </ul>
            $secondParagraph
            """.trimIndent()
    }
}
