package fi.espoo.vekkuli.pages.employee

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.employeeHomePage

class EmployeeHomePage(
    private val page: Page
) {
    fun employeeLogin(langCode: String = "en") {
        page.navigate("$employeeHomePage?lang=$langCode")
        page.waitForCondition { employeeLoginButton.isVisible }
        employeeLoginButton.click()
        page.getByText("Kirjaudu").click()
    }

    val employeeLoginButton = page.getByTestId("employee-login-button")
    val languageSelector = page.locator("#language-selection")
}
