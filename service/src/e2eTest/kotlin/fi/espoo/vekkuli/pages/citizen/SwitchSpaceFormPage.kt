package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.AriaRole

class SwitchSpaceFormPage(
    page: Page
) : BoatSpaceFormPage(page) {
    val reserveButton = page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Vahvista varaus").setExact(true))

    override fun resolveSubmitButton(): Locator = if (reserveButton.count() > 0) reserveButton else super.resolveSubmitButton()
}
