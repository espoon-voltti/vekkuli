package fi.espoo.vekkuli.employee

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.LoadState

fun Page.waitForHtmxSettle() {
    waitForLoadState(LoadState.NETWORKIDLE)
    waitForFunction(
        """() => new Promise(resolve => {
            document.body.addEventListener("htmx:afterSettle", (event) => {
                resolve(true);
            }, { once: true });
            htmx.process(document.body);
        })"""
    )
}

fun Locator.clickAndWaitForHtmxSettle() {
    click()
    page().waitForHtmxSettle()
}
