package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Page
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.pages.BasePage

class PaymentPage(
    page: Page
) : BasePage(page) {
    val nordeaSuccessButton = page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Nordea success"))
    val nordeaFailedButton = page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Nordea failed"))
    val paymentProviders = getByDataTestId("payment-providers")
    val reservationSuccessNotification = page.getByText("Venepaikan varaus onnistui")

    fun payReservation() {
        nordeaSuccessButton.click()
    }
}
