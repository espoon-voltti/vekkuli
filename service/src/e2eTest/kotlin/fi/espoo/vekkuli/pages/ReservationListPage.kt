package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl

class ReservationListPage(
    private val page: Page
) {
    fun navigateTo() {
        page.navigate("$baseUrl/virkailija/venepaikat/varaukset")
    }

    val header = page.getByTestId("reservations-header")
    val boatSpace1 = page.locator("#boat-space-1").first()
    val boatSpace2 = page.locator("#boat-space-2").first()
    val createReservation = page.locator("#create-reservation")
}
