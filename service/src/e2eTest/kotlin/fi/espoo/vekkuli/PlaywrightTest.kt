package fi.espoo.vekkuli

import com.microsoft.playwright.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
abstract class PlaywrightTest {
    @Autowired
    protected lateinit var jdbi: Jdbi

    protected lateinit var playwright: Playwright
    protected lateinit var browser: Browser

    // New instance for each test method.
    protected lateinit var context: BrowserContext
    protected lateinit var page: Page

    @BeforeAll
    fun beforeAllSuper() {
        jdbi.withHandleUnchecked { tx ->
            tx.execute(
                """
                CREATE OR REPLACE FUNCTION reset_database() RETURNS void AS ${'$'}${'$'}
                BEGIN
                  EXECUTE (
                    SELECT 'TRUNCATE TABLE ' || string_agg(quote_ident(table_name), ', ') || ' CASCADE'
                    FROM information_schema.tables
                    WHERE table_schema = 'public'
                    AND table_type = 'BASE TABLE'
                    AND table_name <> 'flyway_schema_history'
                  );
                  EXECUTE (
                    SELECT 'SELECT ' || coalesce(string_agg(format('setval(%L, %L, false)', sequence_name, start_value), ', '), '')
                    FROM information_schema.sequences
                    WHERE sequence_schema = 'public'
                  );
                END ${'$'}${'$'} LANGUAGE plpgsql;
                """.trimIndent()
            )

            jdbi.withHandleUnchecked { tx ->
                tx.execute(
                    """
                    SELECT reset_database();
                    """.trimIndent()
                )
            }

            // execute seed.sql
            val file = File("src/e2eTest/resources/seed.sql").readText()
            jdbi.withHandleUnchecked { h ->
                h.createScript(file).execute()
            }

            // add boat spaces from csv
            val csvFilePath = Paths.get("src/e2eTest/resources/boat_space.csv").toAbsolutePath().toString()
            val csvFile = File(csvFilePath)
            val sql =
                buildString {
                    appendLine(
                        "INSERT INTO boat_space (id, type, location_id, price_id, section, place_number, amenity, " +
                            "width_cm, length_cm, description) VALUES"
                    )
                    for (line in csvFile.readLines()) {
                        val values = line.split(",").map { "'$it'" }
                        appendLine("(${values.joinToString(", ")}),")
                    }
                }.trimEnd(',', '\n') + ";"

            jdbi.withHandleUnchecked { transactionHandle ->
                if (csvFile.exists()) {
                    transactionHandle.execute(sql)
                    println("CSV data inserted successfully.")
                }
            }
        }

        playwright = Playwright.create()
        playwright.selectors().setTestIdAttribute("id")
        browser =
            playwright.chromium().launch(
                BrowserType
                    .LaunchOptions()
                    .setHeadless(runningInDocker)
                    .setTimeout(10_000.0)
            )
    }

    @AfterAll
    fun afterAllSuper() {
        playwright.close()
    }

    @BeforeEach
    fun createContextAndPage() {
        context = browser.newContext()
        page = context.newPage()
    }

    @AfterEach
    fun closeContext() {
        context.close()
    }
}
