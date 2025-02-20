package fi.espoo.vekkuli.employee

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page

private val htmxSettled = "htmxHasSettled"

fun Page.waitForHtmxSettle(action: Page.() -> Unit) {
    evaluate(
        """
        window.$htmxSettled = 'false';
        window.addEventListener("htmx:afterSettle", (event) => window.$htmxSettled = true, { once: true });
        """.trimIndent()
    )
    action()
    waitForFunction("window.$htmxSettled === true")
}

fun Locator.clickAndWaitForHtmxSettle() {
    page().waitForHtmxSettle {
        click()
    }
}
