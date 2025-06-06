package fi.espoo.vekkuli.pages.employee

import com.microsoft.playwright.Page

class PaymentPage(
    private val page: Page
) {
    val nordeaSuccessButton = page.getByTestId("nordea-success")
    val nordeaFailedButton = page.getByTestId("nordea-fail")
    val confirmationPageContainer = page.getByTestId("reservation-confirmation-container")
    val backButtonOnPaymentPage = page.getByTestId("back-to-application")
    val backToHomePageButton = page.getByTestId("back-to-home-page")
    val paymentErrorMessage = page.getByTestId("payment-error-message")
    val paymentErrorLink = page.getByTestId("payment-error-link")
    val paymentPageTitle = page.getByTestId("payment-method-list")
}
