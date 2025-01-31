package fi.espoo.vekkuli.views.employee
import fi.espoo.vekkuli.boatSpace.dev.DebugInfoOverlayView
import fi.espoo.vekkuli.config.LocaleUtil
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.head
import org.springframework.stereotype.Service

@Service
class EmployeeLayout(
    private val icons: Icons,
    private val messageUtil: MessageUtil,
    private val localeUtil: LocaleUtil,
    private val debugOver: DebugInfoOverlayView
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun render(
        isAuthenticated: Boolean,
        currentUri: String,
        bodyContent: String,
    ): String {
        val authMenu =
            if (isAuthenticated) {
                """
                <a href="/auth/saml/logout">
                    <span class="login-link">${t("auth.logout")}</span>
                    ${icons.logout}
                </a>
                """.trimIndent()
            } else {
                """
                <a id="employeeLoginButton" href="/auth/saml/login">
                    <span class="login-link">${t("auth.login")}</span>
                </a>
                """.trimIndent()
            }
        val boatSpaceReservationsLink =
            if (isAuthenticated) {
                """
                <a class="${if (currentUri == "/virkailija/venepaikat/varaukset") "active" else ""}"
                   href="/virkailija/venepaikat/varaukset">
                    ${t("menu.boatSpaceReservations")}
                </a>
                <a  class="${if (currentUri == "/virkailija/venepaikat/varaukset") "active" else ""}"
                    href="/virkailija/admin/reporting">
                    ${t("menu.reports")}
                </a>
                """.trimIndent()
            } else {
                """
                <a class="${if (currentUri == "/virkailija/venepaikat/varaukset") "active" else ""}"
                   href="/auth/saml/login">
                    ${t("menu.boatSpaceReservations")}
                </a>
                """.trimIndent()
            }
        // language=HTML
        return """
             <!DOCTYPE html>
            <html class="theme-light" lang="${localeUtil.getLocaleLanguageCode()}">
            <head>
                <title>Varaukset</title>
                $head
            </head>
            <body>
            <div id="boat-space-reservations-employee" class="columns is-gapless">
                <div class="column is-one-fifth menu-container">
                    <div class='menu'>
                        <p class="menu-label">
                            <img src="/virkailija/static/images/espoo_logo.png" alt="Espoo logo" />
                        </p>
                        <p class="menu-label">${t("menu.marineOutdoor")}</p>
                        <ul class="menu-list">
                            <li>
                                $boatSpaceReservationsLink
                            </li>
                        </ul>
                                                   
                        <div class="login-section" >
                            $authMenu
                        </div>
                    </div>
                </div>
                <div class="employee-page column content-column" >
                    $bodyContent 
                </div>
            </div>
            <div id='modal-container'></div>
            ${debugOver.render(isAuthenticated)}
            </body>
            </html>
            """.trimIndent()
    }
}
