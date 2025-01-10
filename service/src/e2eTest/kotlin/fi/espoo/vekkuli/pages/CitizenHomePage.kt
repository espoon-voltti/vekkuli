package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl

class CitizenHomePage(
    page: Page
) : BasePage(page) {
    fun loginAsOliviaVirtanen() {
        loginAsCitizen("031298-988S")
    }

    fun loginAsLeoKorhonen() {
        loginAsCitizen("150499-911U")
    }

    fun loginAsMikkoVirtanen() {
        loginAsCitizen("010106A957V")
    }

    fun loginAsCitizen(ssn: String) {
        page.navigate(baseUrl)
        getByDataTestId("loginButton").click()
        getByDataTestId(ssn).click()
        page.getByText("Kirjaudu").click()
    }
}
