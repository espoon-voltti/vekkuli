package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.BasePage

class ReserveBoatSpacePage(
    page: Page
) : BasePage(page) {
    class FilterSection(private val locator: ElementLocator) {
        val slipRadio = locator.getRadio("Laituripaikka")
        fun getSlipFilterSection() = SlipFilterSection(locator)
    }

    class SlipFilterSection(locator: ElementLocator) {
        val boatTypeSelect = locator.getSelect("Venetyyppi")
        val widthInput = locator.getInput("Veneen leveys")
        val lengthInput = locator.getInput("Veneen pituus")
        val amenityBuoyCheckbox = locator.getCheckbox("Poiju")
        val amenityRearBuoyCheckbox = locator.getCheckbox("Peräpoiju")
        val amenityBeamCheckbox = locator.getCheckbox("Aisa")
        val amenityWalkBeamCheckbox = locator.getCheckbox("Kävelyaisa")
        val haukilahtiCheckbox = locator.getCheckbox("Haukilahti")
        val kivenlahtiCheckbox = locator.getCheckbox("Kivenlahti")
    }

    class SearchResultsSection(locator: Locator) {
        val harborHeaders = locator.locator(".harbor-header")
        val firstReserveButton = locator.locator("button:has-text('Varaa')").first()
    }

    class ElementLocator(private val locator: Locator) {
        fun getInput(label: String) =
            locator.locator("label")
                .getByText(label)
                .locator("..")
                .locator("input")

        fun getCheckbox(label: String) =
            locator.locator("label.checkbox span")
                .getByText(label, Locator.GetByTextOptions().setExact(true))
                .locator("..")
                .locator("input[type=checkbox]")

        fun getRadio(label: String) =
            locator.locator("label .body")
                .getByText(label, Locator.GetByTextOptions().setExact(true))
                .locator("..")
                .locator("..")
                .locator("input[type=radio]")

        fun getSelect(label: String) =
            locator.locator("label")
                .getByText(label, Locator.GetByTextOptions().setExact(true))
                .locator("..")
                .locator("select")
    }

    fun navigateToPage() {
        page.navigate("$baseUrl/kuntalainen/venepaikka")
    }

    fun getFilterSection() = FilterSection(ElementLocator(getByDataTestId("boat-space-filter")))

    fun getSearchResultsSection() = SearchResultsSection(getByDataTestId("boat-space-results"))
}
