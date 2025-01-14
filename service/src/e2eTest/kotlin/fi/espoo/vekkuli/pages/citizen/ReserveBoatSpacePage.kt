package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.BasePage

class ReserveBoatSpacePage(
    page: Page
) : BasePage(page) {
    class FilterSection(private val root: Locator) {
        private val fields = FieldLocator(root)
        val slipRadio = fields.getRadio("Laituripaikka")

        fun getSlipFilterSection() = SlipFilterSection(root)
    }

    class SlipFilterSection(root: Locator) {
        private val fields = FieldLocator(root)
        val boatTypeSelect = fields.getSelect("Venetyyppi")
        val widthInput = fields.getInput("Veneen leveys")
        val lengthInput = fields.getInput("Veneen pituus")
        val amenityBuoyCheckbox = fields.getCheckbox("Poiju", true)
        val amenityRearBuoyCheckbox = fields.getCheckbox("Peräpoiju")
        val amenityBeamCheckbox = fields.getCheckbox("Aisa", true)
        val amenityWalkBeamCheckbox = fields.getCheckbox("Kävelyaisa")
        val haukilahtiCheckbox = fields.getCheckbox("Haukilahti")
        val kivenlahtiCheckbox = fields.getCheckbox("Kivenlahti")
    }

    class SearchResultsSection(root: Locator) {
        val harborHeaders = root.locator(".harbor-header")
        val firstReserveButton = root.locator("button:has-text('Varaa')").first()
    }

    fun navigateToPage() {
        page.navigate("$baseUrl/kuntalainen/venepaikka")
    }

    fun getFilterSection() = FilterSection(getByDataTestId("boat-space-filter"))

    fun getSearchResultsSection() = SearchResultsSection(getByDataTestId("boat-space-results"))

    fun filterForBoatSpaceB314() {
        val filterSection = getFilterSection()
        filterSection.slipRadio.click()

        val slipFilterSection = filterSection.getSlipFilterSection()
        slipFilterSection.boatTypeSelect.selectOption("Sailboat")
        slipFilterSection.widthInput.fill("3")
        slipFilterSection.lengthInput.fill("6")
    }

    fun startReservingBoatSpaceB314() {
        filterForBoatSpaceB314()
        page.locator("tr:has-text('B 314')").locator("button:has-text('Varaa')").click()
    }
}
