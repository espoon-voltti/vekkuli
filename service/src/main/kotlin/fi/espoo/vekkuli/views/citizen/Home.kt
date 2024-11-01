package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.EnvType
import fi.espoo.vekkuli.controllers.Utils.Companion.getEnv
import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service

@Service
class Home(
    private val messageUtil: MessageUtil,
    private val markDownService: MarkDownService,
    private val timeProvider: TimeProvider
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun render(): String {
        // language=HTML

        val date =
            if (getEnv() != EnvType.Production) {
                "<div>Päivämäärä: ${timeProvider.getCurrentDate()}</div>"
            } else {
                ""
            }

        return """
            <section class="section">
                <div class="container">
                    <h2>${t("boatSpaces.title")}</h2>
                    ${markDownService.render(t("frontPage.content"))}
                    <div class="block"><a class="button is-primary" href="/kuntalainen/venepaikat">Varaa venepaikka</button></a>
                    $date
                </div>
            </section>
            """.trimIndent()
    }
}
