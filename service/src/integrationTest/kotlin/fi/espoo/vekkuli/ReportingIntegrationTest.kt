package fi.espoo.vekkuli

import fi.espoo.vekkuli.service.getRawReport
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportingIntegrationTest : IntegrationTestBase() {
    @Test
    fun `raw report`() {
        val rawReportRows = getRawReport(jdbi)
        assertEquals(5, rawReportRows.size)
    }
}
