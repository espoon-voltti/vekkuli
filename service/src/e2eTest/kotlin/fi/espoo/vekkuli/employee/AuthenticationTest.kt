package fi.espoo.vekkuli.employee

import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.citizen.CitizenHomePage
import fi.espoo.vekkuli.pages.employee.ReservationListPage
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@ActiveProfiles("test")
class AuthenticationTest : PlaywrightTest() {
    @Test
    fun `unauthenticated users should be redirected to employee login`() {
        ReservationListPage(page).navigateTo()
        assertEquals("$baseUrl/virkailija", page.url())
    }

    @Test
    fun `authenticated citizen should not be able to access employee tools`() {
        CitizenHomePage(page).loginAsLeoKorhonen()
        page.navigate("$baseUrl/admin/settings")
        assertEquals("$baseUrl/virkailija", page.url())
    }
}
