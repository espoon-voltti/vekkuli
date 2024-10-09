package fi.espoo.vekkuli.views

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.Organization
import fi.espoo.vekkuli.service.MarkDownService
import org.springframework.stereotype.Service

@Service
class Warnings(
    private val messageUtil: MessageUtil,
    private val markDownService: MarkDownService
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun businessId(
        organizations: List<Organization>,
        businessId: String
    ): String {
        // language=HTML
        val firstParagraph = t("warning.businessId1").replace("""{businessId}""", businessId)

        val secondParagraph = markDownService.render(t("warning.businessId2"))

        val orgList = organizations.joinToString { "<li>${it.name}</li>" }
        return """
            <div class="warning">
                <p class="block">$firstParagraph</p>
                <ul class="block">
                   $orgList 
                </ul>
                <p class="block">
                    $secondParagraph
               </p>
            </div>
            """.trimMargin()
    }
}
