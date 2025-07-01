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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.io.path.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
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
        page = context.newPage()
        // Mock the behavior to return a specific date-time
        mockTimeProvider(timeProvider)
        SendEmailServiceMock.resetEmails()
        PaytrailMock.paytrailPayments.clear()
    }

    @AfterEach
    fun closeContext() {
        page.waitForLoadState(LoadState.NETWORKIDLE)
        page.close()
        context.close()
    }

    fun handleError(e: AssertionError) {
        val testMethod =
            e
                .stackTrace
                .firstOrNull { it.className.contains("Test") || it.methodName.startsWith("test") }
        val testName = testMethod?.methodName ?: "unknown_test"
        val safeTestName = testName.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val screenshotPath = Path("build/failure-screenshots/$safeTestName.png")
        val playwrightOptions =
            Page
                .ScreenshotOptions()
                .setFullPage(true)
                .setPath(screenshotPath)
        page.screenshot(playwrightOptions)

        throw e
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
