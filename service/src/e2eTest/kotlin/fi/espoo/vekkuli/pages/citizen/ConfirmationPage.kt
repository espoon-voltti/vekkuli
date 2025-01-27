package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.pages.BasePage
import fi.espoo.vekkuli.pages.citizen.BoatSpaceFormPage.ReservedSpaceSection

class ConfirmationPage(
    page: Page
) : BasePage(page) {
    val reservationSuccessNotification = page.getByText("Venepaikan varaus onnistui")

    fun getReservedSpaceSection() = ReservedSpaceSection(getByDataTestId("reserved-space"))
}
