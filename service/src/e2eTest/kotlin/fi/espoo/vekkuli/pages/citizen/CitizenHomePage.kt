package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.BasePage

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
