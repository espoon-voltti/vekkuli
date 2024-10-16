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

    fun render(
        isAuthenticated: Boolean,
        userName: String?,
        currentUri: String,
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

        val citizenProfileLink =
            if (isAuthenticated) {
                """<a class="link ${if (currentUri.startsWith(
                        "/kuntalainen/omat-tiedot"
                    )
                ) {
                    "active"
                } else {
                    ""
                }} " href="/kuntalainen/omat-tiedot" >Omat tiedot</a>"""
            } else {
                ""
            }
        val boatLink = """<a class="link ${if (currentUri.startsWith(
                "/kuntalainen/vene"
            )
        ) {
            "active"
        } else {
            ""
        }}" href="/kuntalainen/venepaikat">Venepaikat</a>"""
        // language=HTML
        return """
            <!DOCTYPE html>
            <html class="theme-light">
            <head>
                <title>Varaukset</title>
                $head
            </head>
            <body>
            
            <nav role="navigation" aria-label="main navigation">
                <div class="nav-row">
                    <img class="logo" src="/static/images/espoo_logo.png" alt="Espoo logo" />
                    <h1>Espoon resurssivaraus</h1>
                    
                    <div class="auth">
                        $menu 
                    </div>
                </div>
                
                <div class="nav-row">
                    <div>
                      $boatLink
                    </div>
                    <!-- <a class="link">Liikuntatilat</a>
                    <a class="link">Ohjatut ryhmäliikunnat</a> -->
                    <div style="margin-left: auto">
                        $citizenProfileLink
                    </div>
                </div>
            </nav>
            
            <div>
                $bodyContent 
            </div>
                <div id="modal-container"></div>
            </body>
            </html>
            """.trimIndent()
    }
}
