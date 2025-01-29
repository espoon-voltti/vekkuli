package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page

fun Page.advanceJSDateTime(seconds: Int) {
    evaluate(
        """
            (seconds) => {
                const originalDate = Date;
                
                globalThis.Date = class extends originalDate {
                    constructor(...args) {
                        if (args.length === 0) {
                            // If no arguments are given, return a shifted time
                            return new originalDate(originalDate.now() + (seconds * 1000));
                        }
                        return new originalDate(...args);
                    }
        
                    static now() {
                        return originalDate.now() + (seconds * 1000);
                    }
                };
            }
        """,
        seconds
    )
}

fun Locator.getByDataTestId(testId: String): Locator {
    val test = "[data-testid=\"$testId\"]"
    return locator(test)
}

fun Page.getByDataTestId(testId: String): Locator {
    val test = "[data-testid=\"$testId\"]"
    return locator(test)
}
