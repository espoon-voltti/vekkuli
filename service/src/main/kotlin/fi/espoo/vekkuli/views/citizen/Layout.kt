package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.views.head
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Layout {
    @Autowired
    lateinit var messageUtil: MessageUtil

    fun t(key: String): String {
        return messageUtil.getMessage(key)
    }

    fun generateLayout(
        isAuthenticated: Boolean,
        userName: String?,
        bodyContent: String
    ): String {
        // language=HTML
        val menu =
            if (!isAuthenticated) {
                """
                <a id="loginButton"
                   class="link"
                   href="/auth/saml-suomifi/login">${t("auth.login")}</a>
                """.trimIndent()
            } else {
                """
                <div class="container" x-data="{ open: false }">
                    <div class="dropdown" :class="{ 'is-active': open }">
                        <div class="dropdown-trigger">
                            <a aria-haspopup="true" aria-controls="dropdown-menu" @click="open = !open">
                                <span>${userName ?: "Dropdown"}</span>
                                <span class="icon is-small">
                                    <i class="fas fa-angle-down" aria-hidden="true"></i>
                                </span>
                            </a>
                        </div>
                        <div class="dropdown-menu" id="dropdown-menu" role="menu">
                            <div class="dropdown-content">
                                <a href="/auth/saml-suomifi/logout" class="dropdown-item">
                                    ${t("auth.logout")}
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
                """.trimIndent()
            }

        // language=HTML
        return """
            <!DOCTYPE html>
            <html class="theme-light">
            <head>
                <title>Varaukset</title>
                $head
            </head>
            <body>

            <nav class="navbar mb-s" role="navigation" aria-label="main navigation">
                <div class="navbar-brand">
                    <a class="navbar-item" href="/">
                        <img src="/static/images/espoo_logo.png" alt="Espoo logo" />
                    </a>
                </div>
                <div class="navbar-end" style="margin-right: 132px">
                    <div class="navbar-item">
                        <div class="buttons">
                            $menu
                        </div>
                    </div>
                </div>
            </nav>
            
            <div>
                $bodyContent 
            </div>

            </body>
            </html>
            """.trimIndent()
    }
}
