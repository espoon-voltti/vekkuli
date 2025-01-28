package fi.espoo.vekkuli.pages.citizen.components

import com.microsoft.playwright.Locator
import fi.espoo.vekkuli.pages.BasePage

interface IGetByTestId<T> where T : BasePage, T : IGetByTestId<T>

@Suppress("UNCHECKED_CAST")
fun <T> IGetByTestId<T>.getByDataTestId(
    testId: String,
    locator: Locator? = null
): Locator where T : BasePage, T : IGetByTestId<T> {
    return (this as T).getByDataTestId(testId, locator)
}
