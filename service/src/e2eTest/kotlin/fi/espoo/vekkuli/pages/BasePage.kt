package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page

open class BasePage(
    protected val page: Page
) {
    fun getByDataTestId(
        testId: String,
        locator: Locator? = null
    ): Locator {
        if (locator != null) {
            return locator.getByDataTestId(testId)
        }
        return page.getByDataTestId(testId)
    }
}
