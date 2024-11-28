package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.employeeHomePage

class EmployeeHomePage(
    private val page: Page
) {
    fun employeeLogin(langCode: String = "en") {
        page.navigate(employeeHomePage + "?lang=$langCode")
        employeeLoginButton.click()
        page.getByText("Kirjaudu").click()
    }

    val employeeLoginButton = page.getByTestId("employee-login-button")
}
