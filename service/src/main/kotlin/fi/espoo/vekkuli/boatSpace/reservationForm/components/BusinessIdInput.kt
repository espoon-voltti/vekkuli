package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.domain.Organization
import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class BusinessIdInput(
    private val markDownService: MarkDownService,
) : BaseView() {
    fun render(allowDuplicateIds: Boolean): String {
        val allowedDuplicateInfo =
            """
            hx-post="/info/businessid" 
            hx-trigger="keyup changed" 
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
                        ${if (!allowDuplicateIds) """data-validate-url="/validate/businessid"""" else allowedDuplicateInfo}
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

        val orgList = organizations.joinToString { "<li>${it.name}</li>" }
        // language=HTML
        return """
            <div class="warning">
                <p class="block">$firstParagraph</p>
                <p class="block">
                <ul class="bullets">
                   $orgList 
                </ul></p>
                <p class="block">
                    $secondParagraph
               </p>
            </div>
            """.trimMargin()
    }

    fun warning(
        organizations: List<Organization>,
        businessId: String
    ): String {
        val firstParagraph = messageUtil.getMessage("warning.businessId1", listOf(businessId))
        val secondParagraph = markDownService.render(t("warning.businessId2"))

        val orgList = organizations.joinToString { "<li>${it.name}</li>" }
        // language=HTML
        return """
            <div class="warning">
                <p class="block">$firstParagraph</p>
                <p class="block"><ul>
                   $orgList 
                </ul></p>
                <p class="block">
                    $secondParagraph
               </p>
            </div>
            """.trimMargin()
    }
}
