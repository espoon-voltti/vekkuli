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
            <div class="columns is-centered icon is-extra-large">${icons.boat}</div>
             <p class='columns is-centered is-primary-color subtitle-main'>${t("home.login")}</p>
             <div class='columns is-centered'>
             <button class="button is-primary" hx-get="/auth/saml/login"
                    hx-target="body">${t("auth.login")}</button>
                    </div>
        </div>
        """.trimIndent()
}
