package fi.espoo.vekkuli

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ActiveProfiles("test")
@EnabledIf(value = "fi.espoo.vekkuli.HeaderTest#isLocalDocker", disabledReason = "Headers are only available when testing with nginx")
class HeaderTest : PlaywrightTest() {
    companion object {
        @JvmStatic
        fun isLocalDocker(): Boolean = "local-docker" == System.getenv("ENVIRONMENT")
    }

    @Test
    fun `should not send duplicate headers for employee`() {
        val response = page.request().get("$baseUrl/virkailija")
        val headers = response.headersArray().filter { it.name.lowercase() != "cache-control" }

        for (header in headers) {
            val occurrences = headers.filter { it.name.lowercase() == header.name.lowercase() }
            assertTrue(occurrences.size == 1, "Header '${header.name}' is duplicated")
        }
    }

    @Test
    fun `should not send duplicate headers for citizen`() {
        val response = page.request().get(baseUrl)
        val headers = response.headersArray().filter { it.name.lowercase() != "cache-control" }

        for (header in headers) {
            val occurrences = headers.filter { it.name.lowercase() == header.name.lowercase() }
            assertTrue(occurrences.size == 1, "Header '${header.name}' is duplicated")
        }
    }

    @Test
    fun `should allow eval and inline scripts for employee`() {
        val response = page.request().get("$baseUrl/virkailija")
        val headers = response.headers()
        val contentSecurityPolicy = headers["content-security-policy"]

        assertNotNull(contentSecurityPolicy)
        assertTrue(contentSecurityPolicy.contains(Regex("script-src[^;]*'unsafe-inline'")))
        assertTrue(contentSecurityPolicy.contains(Regex("script-src[^;]*'unsafe-eval'")))
    }

    @Test
    fun `should not allow eval and inline scripts for citizen`() {
        val response = page.request().get(baseUrl)
        val headers = response.headers()
        val contentSecurityPolicy = headers["content-security-policy"]

        assertNotNull(contentSecurityPolicy)
        assertFalse(contentSecurityPolicy.contains(Regex("script-src[^;]*'unsafe-inline'")))
        assertFalse(contentSecurityPolicy.contains(Regex("script-src[^;]*'unsafe-eval'")))
    }
}
