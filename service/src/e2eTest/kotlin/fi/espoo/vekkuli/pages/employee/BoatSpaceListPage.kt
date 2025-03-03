package fi.espoo.vekkuli.pages.employee

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.BasePage

class BoatSpaceListPage(
    page: Page
) : BasePage(page) {
    fun navigateTo() {
        navigateToWithParams()
    }

    fun navigateToWithParams(params: Map<String, String>? = null) {
        // if params, map params to query string
        val paramsString =
            params
                ?.takeIf { it.isNotEmpty() }
                ?.map { (key, value) -> "$key=$value" }
                ?.joinToString("&", prefix = "?") ?: ""

        page.navigate("$baseUrl/virkailija/venepaikat/selaa$paramsString")
    }

    private val filterLocator = { filter: String -> getByDataTestId("filter-$filter") }

    val listItems = page.locator(".boat-space-item")
    val boatSpaceTypeFilter = { type: String -> filterLocator("type-$type") }
    val boatStateFilter = { state: String -> filterLocator("boatSpaceState-$state") }
    val searchInput = { inputName: String -> getByDataTestId("search-input-$inputName") }
    val expandingSelectionFilter = { selection: String -> filterLocator("selection-$selection") }
    val amenityFilter = { amenity: String -> filterLocator("amenity-$amenity") }
}
