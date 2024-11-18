package fi.espoo.vekkuli.boatSpace.admin

import fi.espoo.vekkuli.views.head
import org.springframework.stereotype.Service

@Service("AdminLayout")
class Layout {
    fun render(bodyContent: String): String {
        // language=HTML
        return """
            <!DOCTYPE html>
            <html class="theme-light">
                <head>
                    <title>Admin</title>
                    $head
                </head>
                <body>
                    $bodyContent     
                </body>
            </html>
            """.trimIndent()
    }
}
