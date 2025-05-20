package fi.espoo.vekkuli.pages.employee

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.employeeHomePage
import fi.espoo.vekkuli.pages.getByDataTestId

class BoatSpaceDetailsPage(
    private val page: Page
) {
    fun navigateTo(boatSpaceId: Int) {
        page.navigate("$employeeHomePage/virkailija/venepaikat/$boatSpaceId")
    }

    val reservationHistoryListContainer = page.getByDataTestId("boat-space-details-container")
    val reservationRows = page.getByDataTestId("reservation-history-table").locator("tbody").locator("tr")
    val reservationRow = { index: Int -> reservationRows.nth(index) }

    val reserverColumn = { index: Int -> reservationRow(index).getByDataTestId("reserver-column-$index") }
    val goBack = page.getByDataTestId("go-back")
}
