package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.boatSpace.admin.DebugInfoOverlayView
import fi.espoo.vekkuli.config.LocaleUtil
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.common.CommonComponents
import fi.espoo.vekkuli.views.head
import org.springframework.stereotype.Service

@Service
class Layout(
    private val messageUtil: MessageUtil,
    private val icons: Icons,
    private val commonComponents: CommonComponents,
    private val localeUtil: LocaleUtil,
    private val debugOverlay: DebugInfoOverlayView
) {
    fun t(key: String): String = messageUtil.getMessage(key)

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
                                     ${icons.chevronDown}
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
                }} " href="/kuntalainen/omat-tiedot">${t("home.ownInformation")}</a>"""
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
        }}" href="/kuntalainen/venepaikat">${t("boatSpaces.title")}</a>"""
        // language=HTML
        return """
            <!DOCTYPE html>
            <html class="theme-light" lang="${localeUtil.getLocaleLanguageCode()}">
            <head>
                <title>${t("boatSpaceReservation.title.reservations")}</title>
                $head
            </head>
            <body>
            
            <nav role="navigation" aria-label="main navigation">
                <div class="nav-row">
                    <div class='columns'>
                        <img class="logo" src="/static/images/espoo_logo.png" alt="Espoo logo" />
                        <h1>${t("home.title")}</h1>
                    </div>
                    <div class="columns">
                            ${commonComponents.languageSelection()}
                            $menu 
                    </div>
                </div>
                
                <div class="nav-row">
                    <div>
                      $boatLink
                    </div>
                    <!-- <a class="link">Liikuntatilat</a>
                    <a class="link">Ohjatut ryhmäliikunnat</a> -->
                    <div>
                        $citizenProfileLink
                    </div>
                </div>
            </nav>
            
            <div>
                $bodyContent 
            </div>
                <div id="modal-container"></div>
                ${debugOverlay.render(isAuthenticated)}
            </body>
            </html>
            """.trimIndent()
    }
}
