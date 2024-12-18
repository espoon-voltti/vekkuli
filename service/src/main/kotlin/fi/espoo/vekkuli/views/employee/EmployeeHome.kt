package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import org.springframework.stereotype.Service

// language=HTML
@Service
class EmployeeHome(
    private val icons: Icons
) : BaseView() {
    fun render(): String =
        """
        <div class="centered is-gap-4">
            <div class="is-centered icon is-extra-large">${icons.boat}</div>
             <div class='is-centered'>
                <h1 class='is-primary-color is-title-secondary m-none'>${t("employeeLoginPage.title")}</h1>
             </div>
             <div class='is-centered'>
             <a class="button is-primary" id="employee-login-button" href="/auth/saml/login">${t("employeeLoginPage.button.login")}</a>
                    </div>
        </div>
        """.trimIndent()
}
