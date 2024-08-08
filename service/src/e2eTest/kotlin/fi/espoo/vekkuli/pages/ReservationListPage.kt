package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl

class ReservationListPage(
    private val page: Page
) {
    fun navigateTo() {
        page.navigate("$baseUrl/virkailija/venepaikat/varaukset")
    }

    val harborHeaders = page.locator(".reservation-item")
}
