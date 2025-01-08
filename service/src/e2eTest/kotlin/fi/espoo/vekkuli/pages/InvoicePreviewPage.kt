package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page

class InvoicePreviewPage(
    private val page: Page
) {
    val header = page.getByTestId("invoice-preview-header")
    val sendButton = page.getByTestId("submit")
    val cancelButton = page.getByTestId("cancel")
    val markAsPaid = page.getByTestId("mark-as-paid")
    val confirmModalSubmit = page.getByTestId("confirm-modal-submit")
}
