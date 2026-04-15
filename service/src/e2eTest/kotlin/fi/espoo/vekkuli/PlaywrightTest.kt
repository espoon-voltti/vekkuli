package fi.espoo.vekkuli

import com.microsoft.playwright.*
import com.microsoft.playwright.options.LoadState
import fi.espoo.vekkuli.employee.waitForHtmxSettle
import fi.espoo.vekkuli.service.MessageService
import fi.espoo.vekkuli.service.PaytrailMock
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.createAndSeedDatabase
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.io.path.Path

class PlaywrightTestWatcher : TestWatcher {
    override fun testSuccessful(context: ExtensionContext) {
        val testInstance = context.testInstance.orElse(null)
        if (testInstance is PlaywrightTest) {
            testInstance.closeContext(null)
        }
    }

    override fun testFailed(
        context: ExtensionContext,
        cause: Throwable?
    ) {
        val testInstance = context.testInstance.orElse(null)
        if (testInstance is PlaywrightTest) {
            testInstance.closeContext(cause)
        }
    }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@ExtendWith(PlaywrightTestWatcher::class)
abstract class PlaywrightTest {
    @Autowired
    protected lateinit var jdbi: Jdbi

    @Autowired
    lateinit var messageService: MessageService

    protected lateinit var playwright: Playwright
    protected lateinit var browser: Browser

    // New instance for each test method.
    protected lateinit var context: BrowserContext
    protected lateinit var page: Page

    @MockitoBean
    protected lateinit var timeProvider: TimeProvider

    @BeforeAll
    fun beforeAllSuper() {
        playwright = Playwright.create()
        playwright.selectors().setTestIdAttribute("id")
        browser =
            playwright.chromium().launch(
                BrowserType
                    .LaunchOptions()
                    .setHeadless(true)
                    .setTimeout(10_000.0)
            )
    }

    @AfterAll
    fun afterAllSuper() {
        playwright.close()
    }

    @BeforeEach
    fun createContextAndPage() {
        createAndSeedDatabase(jdbi)
        context = browser.newContext()
        context.tracing().start(
            Tracing
                .StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true)
        )
        page = context.newPage()
        // Mock the behavior to return a specific date-time
        mockTimeProvider(timeProvider)
        SendEmailServiceMock.resetEmails()
        PaytrailMock.paytrailPayments.clear()
    }

    /** Called from PlaywrightTestWatcher after each test
     *
     * This cannot be done in @AfterEach because it doesn't know whether the test was successful or failed. We also must
     * close the page and context here instead of in @AfterEach because TestWatcher is called after @AfterEach.
     */
    fun closeContext(error: Throwable?) {
        if (error != null) {
            val testMethod =
                error
                    .stackTrace
                    .firstOrNull { it.className.contains("Test") || it.methodName.startsWith("test") }
            val testName = testMethod?.methodName ?: "unknown_test"
            val safeTestName = testName.replace(Regex("[^a-zA-Z0-9_-]"), "_")

            val tracePath = Path("build/failure-traces/$safeTestName.zip")
            context.tracing().stop(Tracing.StopOptions().setPath(tracePath))

            val screenshotPath = Path("build/failure-screenshots/$safeTestName.png")
            page.screenshot(
                Page
                    .ScreenshotOptions()
                    .setFullPage(true)
                    .setPath(screenshotPath)
            )
        } else {
            // Don't save trace
            context.tracing().stop()
        }

        page.waitForLoadState(LoadState.NETWORKIDLE)
        page.close()
        context.close()
    }

    fun typeText(
        locator: Locator,
        text: String
    ) {
        text.forEach { character ->
            page.waitForHtmxSettle {
                locator.press("$character")
            }
        }
    }

    /**
     * Fills a search input that uses HTMX with debounced triggers (e.g., hx-trigger="keyup changed delay:500ms")
     * and Alpine.js x-model bindings.
     * Types each character and then ensures Alpine.js state is synced before triggering the search.
     */
    fun fillSearchInput(
        locator: Locator,
        text: String
    ) {
        // Clear any existing value first
        locator.clear()
        // Type each character using press() - this properly triggers Alpine.js x-model bindings
        text.forEach { character ->
            locator.press("$character")
        }
        // Ensure Alpine.js x-model is synced by triggering an input event and updating Alpine data directly
        locator.evaluate(
            """el => {
            // Dispatch input event to trigger any listeners
            el.dispatchEvent(new Event('input', { bubbles: true }));
            // Find the Alpine component and update citizenFullName
            const alpineEl = el.closest('[x-data]');
            if (alpineEl && alpineEl._x_dataStack) {
                alpineEl._x_dataStack[0].citizenFullName = el.value;
            }
        }"""
        )
        // Set up HTMX settle listener and wait for the debounced request to complete
        page.evaluate(
            """
            window.htmxHasSettled = 'false';
            window.addEventListener("htmx:afterSettle", (event) => window.htmxHasSettled = true, { once: true });
            """.trimIndent()
        )
        // Press End key to trigger a final keyup event that satisfies the debounce
        locator.press("End")
        // Wait for HTMX to settle (debounce delay 500ms + request/response time)
        page.waitForFunction("window.htmxHasSettled === true")
        // Give Alpine.js time to process x-show directives on the new content
        page.waitForTimeout(100.0)
    }
}
