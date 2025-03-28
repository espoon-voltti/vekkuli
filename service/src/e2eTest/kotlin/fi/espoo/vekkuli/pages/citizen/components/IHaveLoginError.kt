package fi.espoo.vekkuli.pages.citizen.components

import com.microsoft.playwright.Locator
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.pages.BasePage

interface IHaveLoginError<T> : IGetByTestId<T> where T : BasePage, T : IHaveLoginError<T> {
    class LoginError(
        val root: Locator
    ) {
        val loginButton = root.getByRole(AriaRole.LINK, Locator.GetByRoleOptions().setName("Kirjaudu sisään"))
    }

    fun getLoginError() = LoginError(getByDataTestId("login-error"))
}
