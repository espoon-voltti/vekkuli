package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page

class ErrorPage(
    private val page: Page
) {
    val errorPageContainer = page.getByTestId("error-page-container")
}
