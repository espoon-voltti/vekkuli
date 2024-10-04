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
                <div x-data="{ open: false }">
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

            
            
            <div style="background: white; display:flex; padding-left: 28px; padding-right:28px">
                <img style="width: 185px; height: 107px; margin-right: 16px" src="/static/images/espoo_logo.png" alt="Espoo logo" />
                <h1 style="margin-top:43px">Espoon resurssivaraus</h1>
                
                <div style="padding: 50px 20px 20px;margin-left: auto;">
                    $menu 
                </div>
            </div>
            
            <div style="background: white; display:flex; padding-left: 28px; padding-right:28px; border-top: 8px solid #F7F7F7">
                <span style="padding: 20px"><a class="link" style="margin-right: 16px">Venepaikat</a></span>
                <span style="padding: 20px"><a class="link" style="margin-right: 16px">Liikuntatilat</a></span>
                <span style="padding: 20px"><a class="link" style="margin-right: 16px">Ohjatut ryhmäliikunnat</a></span>
                <span style="padding: 20px; margin-left: auto"><a class="link" style="margin-right: 16px">Omat tiedot</a></span>
            </div>
                
            <div>
                $bodyContent 
            </div>

            </body>
            </html>
            """.trimIndent()
    }
}
