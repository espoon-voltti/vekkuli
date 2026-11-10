package fi.espoo.vekkuli.employee

import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.pages.getByDataTestId
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.nio.file.Files
import kotlin.test.assertEquals

@ActiveProfiles("test")
class BillingReportTest : PlaywrightTest() {
    @Test
    fun `employee downloads the billing report for a date range`() {
        EmployeeHomePage(page).employeeLogin()
        page.navigate("$baseUrl/virkailija/admin/reporting")

        page.locator("#startDate").fill("2024-01-01")
        page.locator("#endDate").fill("2024-12-31")
        val download = page.waitForDownload { page.getByDataTestId("billing-report-button").click() }

        assertEquals("vekkuli-laskutusraportti-2024-01-01-2024-12-31.csv", download.suggestedFilename())
        val header = Files.readAllLines(download.path()).first().removePrefix("﻿")
        assertEquals(
            "varaaja;varauksen tyyppi;varauksen kesto;paikan tyyppi;satama;laituri;paikka;maksupäivä;summa;maksutapa;hintaluokka;viite",
            header
        )
    }
}
