package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.pages.BasePage
import fi.espoo.vekkuli.pages.citizen.components.IHaveReservationTimer

class PaymentPage(
    page: Page
) : BasePage(page),
    IHaveReservationTimer<PaymentPage> {
    val header = page.getByText("Valitse maksutapa")
    val nordeaSuccessButton =
        page.getByText(
            "Nordea success"
        )
    val nordeaFailedButton =
        page.getByText(
            "Nordea failed"
        )
    val paymentProviders = getByDataTestId("payment-providers")
    val paymentFailedNotification = page.getByText("Maksu epäonnistui")
    val reservationFailedNotification = page.getByText("Paikan varaus epäonnistui")
    val reservationSuccessNotification = page.getByText("Paikan varaus onnistui")
    val backButton = page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Palaa takaisin").setExact(true))

    fun assertOnPaymentPage() {
        assertThat(getByDataTestId("payment-page")).isVisible()
    }

    fun payReservation() {
        nordeaSuccessButton.click()
    }
}
