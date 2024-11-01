package fi.espoo.vekkuli.views.employee
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.common.CommonComponents
import fi.espoo.vekkuli.views.head
import org.springframework.stereotype.Service

@Service
class EmployeeLayout(
    private val icons: Icons,
    private val messageUtil: MessageUtil,
    private val commonComponents: CommonComponents
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
            <html class="theme-light" lang="${messageUtil.getLocaleLanguageCode()}>
            <head>
                <title>Varaukset</title>
                $head
            </head>
            <body>
            <div class="columns">
                <div class="menu">
                    <p class="menu-label">
                        <img src="/static/images/espoo_logo.png" alt="Espoo logo" />
                    </p>
                        <div class='pb-m'>${commonComponents.languageSelection()}</div>
                        <p class="menu-label">${t("menu.marineOutdoor")}</p>
                        <ul class="menu-list">
                            <li>
                                $boatSpaceReservationsLink
                            </li>
                        </ul>
                        
                    <div class="auth-menu" >
                        $authMenu
                    </div>
                </div>
                <div class="column content-column" >
                    $bodyContent 
                </div>
            </div>
            <div id='modal-container'></div>
            </body>
            </html>
            """.trimIndent()
    }
}
