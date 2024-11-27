package fi.espoo.vekkuli.common

import fi.espoo.vekkuli.config.LocaleUtil
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.head
import org.springframework.stereotype.Component

@Component
class BlankLayoutView(
    private val localeUtil: LocaleUtil
) : BaseView() {
    fun render(bodyContent: String): String {
        // language=HTML
        return """
            <!DOCTYPE html>
            <html class="theme-light" lang="${localeUtil.getLocaleLanguageCode()}">
            <head>
                $head
            </head>
            <body>
                $bodyContent
            </body>
            </html>
            """.trimIndent()
    }
}
