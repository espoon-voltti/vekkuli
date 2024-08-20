package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page

class CitizenDetailsPage(
    private val page: Page
) {
    val citizenDetailsSection = page.getByTestId("citizen-details")
}
