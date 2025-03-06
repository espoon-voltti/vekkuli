package fi.espoo.vekkuli.pages.citizen.components

import com.microsoft.playwright.Locator
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.pages.BasePage

interface IHaveErrorModal<T> : IGetByTestId<T> where T : BasePage, T : IHaveErrorModal<T> {
    class ErrorModal(
        val root: Locator
    ) {
        val title = root.getByRole(AriaRole.HEADING, Locator.GetByRoleOptions().setName("Varaaminen ei onnistunut").setExact(true))
        val okButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Ok").setExact(true))
    }

    fun getErrorModal() = ErrorModal(getByDataTestId("error-modal"))
}
