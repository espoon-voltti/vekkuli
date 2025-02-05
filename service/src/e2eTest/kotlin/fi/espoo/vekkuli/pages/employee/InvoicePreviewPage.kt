package fi.espoo.vekkuli.pages.employee

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.pages.BasePage

class InvoicePreviewPage(
    page: Page
) : BasePage(page) {
    val header = page.getByTestId("invoice-preview-header")
    val sendButton = page.getByTestId("submit-button")
    val cancelButton = page.getByTestId("cancel")
    val markAsPaid = page.getByTestId("mark-as-paid")
    val confirmModalSubmit = page.getByTestId("confirm-modal-submit")
    val confirmModalCancel = page.getByTestId("confirm-modal-cancel")
    val priceWithTax = page.getByTestId("priceWithTax")
    val description = page.getByTestId("description")
}
