package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl

class ReservationListPage(
    page: Page
) : BasePage(page) {
    fun navigateTo() {
        page.navigate("$baseUrl/virkailija/venepaikat/varaukset")
    }

    private val filterLocator = { filter: String -> getByDataTestId("filter-$filter") }

    val header = page.getByTestId("reservations-header")
    val boatSpace1 = page.locator("#boat-space-1").first()
    val boatSpace2 = page.locator("#boat-space-2").first()
    val boatSpace8 = page.locator("#boat-space-8").first()
    val createReservation = page.locator("#create-reservation")
    val warningIcon = getByDataTestId("warning-icon", boatSpace1)
    val warningIcon8 = getByDataTestId("warning-icon", boatSpace8)
    val reservationsTableB314Row = page.locator("tr:has-text('B 314')")
    val reservationsTableB314RowEndDate = getByDataTestId("reservation-end-date", reservationsTableB314Row)
    val reservations = page.locator(".reservation-item")
    val searchInput = { inputName: String -> getByDataTestId("search-input-$inputName") }
    val boatSpaceTypeFilter = { type: String -> filterLocator("type-$type") }
    val reservationValidityFilter = { validity: String -> filterLocator("reservation-validity-$validity") }
    val exceptionsFilter = filterLocator("exceptions")
    val expandingSelectionFilter = { selection: String -> filterLocator("selection-$selection") }
    val amenityFilter = { amenity: String -> filterLocator("amenity-$amenity") }
}
