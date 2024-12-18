package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl

class ReservationListPage(
    page: Page
) : BasePage(page) {
    fun navigateTo() {
        page.navigate("$baseUrl/virkailija/venepaikat/varaukset")
    }

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
    val boatSpaceTypeFilter = { typeFilter: String -> getByDataTestId("filter-type-$typeFilter") }
    val searchInput = { inputName: String -> getByDataTestId("search-input-$inputName") }
}
