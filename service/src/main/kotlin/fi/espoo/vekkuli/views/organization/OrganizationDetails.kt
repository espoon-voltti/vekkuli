package fi.espoo.vekkuli.views.organization

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.common.CommonComponents
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrganizationDetails(
    var commonComponents: CommonComponents,
    var messageUtil: MessageUtil
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun organizationPage(
        @SanitizeInput organizationId: UUID,
    ): String {
        // language=HTML
        return """
            <div class="container block" style='height: 100vh'>
                ${commonComponents.goBackButton("/virkailija/venepaikat/varaukset")}
                <h1>${t("organizationPage.title")}</h1>
            </div>
            """.trimIndent()
    }
}
