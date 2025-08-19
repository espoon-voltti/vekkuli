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
        cause: Throwable
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
}
