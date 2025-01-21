package fi.espoo.vekkuli.employee

import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.citizen.CitizenHomePage
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@ActiveProfiles("test")
class AuthenticationTest : PlaywrightTest() {
    @Test
    fun `authenticated citizen should not be able to access employee tools`() {
        CitizenHomePage(page).loginAsLeoKorhonen()
        page.navigate("$baseUrl/admin/settings")
        assertEquals("$baseUrl/", page.url())
    }
}
