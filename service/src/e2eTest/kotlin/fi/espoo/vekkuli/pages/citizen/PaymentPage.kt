package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.pages.BasePage

class PaymentPage(
    page: Page
) : BasePage(page) {
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
    val reservationFailedNotification = page.getByText("Maksu epäonnistui")
    val reservationSuccessNotification = page.getByText("Venepaikan varaus onnistui")

    fun assertOnPaymentPage() {
        assertThat(getByDataTestId("payment-page")).isVisible()
    }

    fun payReservation() {
        nordeaSuccessButton.click()
    }
}
