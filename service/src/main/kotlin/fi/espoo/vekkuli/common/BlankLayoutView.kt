package fi.espoo.vekkuli.common

import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.head
import org.springframework.stereotype.Component

@Component
class BlankLayoutView : BaseView() {
    fun render(bodyContent: String): String {
        // language=HTML
        return """
            <!DOCTYPE html>
            <html>
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
