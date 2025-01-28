package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Page
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.BasePage

class CitizenHomePage(
    page: Page
) : BasePage(page) {
    fun navigateToPage() {
        page.navigate(baseUrl)
    }

    fun loginAsOliviaVirtanen() {
        loginAsCitizen(oliviaVirtanenSsn)
    }

    fun loginAsLeoKorhonen() {
        loginAsCitizen(leoKorhonenSsn)
    }

    fun loginAsMikkoVirtanen() {
        loginAsCitizen(mikkoVirtanenSsn)
    }

    // non-Espoo citizen
    fun loginAsMarkoKuusinen() {
        loginAsCitizen(markoKuusinenSsn)
    }

    fun loginAsCitizen(ssn: String) {
        page.navigate(baseUrl)
        getByDataTestId("loginButton").click()
        getByDataTestId(ssn).click()
        page.getByText("Kirjaudu").click()
    }

    val languageSelector = page.locator("#language-selection")
    val finnishTitle = page.getByRole(AriaRole.HEADING, Page.GetByRoleOptions().setName("venepaikkavaraus"))
    val englishTitle = page.getByRole(AriaRole.HEADING, Page.GetByRoleOptions().setName("boat space reservation"))
    val swedishTitle = page.getByRole(AriaRole.HEADING, Page.GetByRoleOptions().setName("Bokning av båtplats"))

    val boatSearchLink = page.getByRole(AriaRole.LINK, Page.GetByRoleOptions().setName("Venepaikat").setExact(true))
    val openFormButton = page.getByRole(AriaRole.LINK, Page.GetByRoleOptions().setName("Selaile vapaita venepaikkoja"))

    companion object {
        val oliviaVirtanenSsn = "031298-988S"
        val leoKorhonenSsn = "150499-911U"
        val mikkoVirtanenSsn = "010106A957V"
        val markoKuusinenSsn = "290991-993F"
    }
}
