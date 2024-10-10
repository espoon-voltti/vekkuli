package fi.espoo.vekkuli.views

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.Organization
import fi.espoo.vekkuli.service.MarkDownService
import org.springframework.stereotype.Service

@Service
class Warnings(
    private val messageUtil: MessageUtil,
    private val markDownService: MarkDownService,
    private val icons: Icons
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun businessId(
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

    fun boatTypeWarning() =
        """
        <div class="warning" id="boatType-warning">
            <p class="block">${t("boatSpaceApplication.boatTypeWarning")}</p>
            <button class="icon-text"
                    type="button"
                    id="size-warning-back-button"
                    x-on:click="modalOpen = true">
                <span class="icon">
                    ${icons.chevronLeft}
                </span>
                <span>${t("boatSpaces.goBack")}</span>
            </button>
        </div>
        """.trimIndent()

    fun boatWeightWarning() =
        """
        <div class="warning" id="boatWeight-warning">
            <p class="block">${t("boatSpaceApplication.boatWeightWarning")}</p>
            <button class="icon-text"
                    type="button"
                    id="size-warning-back-button"
                    x-on:click="modalOpen = true">
                <span class="icon">
                    ${icons.chevronLeft}
                </span>
                <span>${t("boatSpaces.goBack")}</span>
            </button>
        </div>
        """.trimIndent()

    fun boatSizeWarning() =
        """
        <div class="warning" id="boatSize-warning">
            <p class="block">${t("boatSpaceApplication.boatSizeWarning")}</p>
            <p class="block">${t("boatSpaceApplication.boatSizeWarningExplanation")}</p>
            <button class="icon-text"
                    type="button"
                    id="size-warning-back-button"
                    x-on:click="modalOpen = true">
                <span class="icon">
                    ${icons.chevronLeft}
                </span>
                <span>${t("boatSpaces.goBack")}</span>
            </button>
        </div>
        """.trimIndent()
}
