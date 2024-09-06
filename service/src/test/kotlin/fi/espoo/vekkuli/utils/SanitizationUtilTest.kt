package fi.espoo.vekkuli.utils
import fi.espoo.vekkuli.views.employee.SanitizationUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.web.util.HtmlUtils

private data class CitizenTest(
    val name: String,
    val age: Int,
    val description: String
)

private data class BoatUpdateFormTest(
    val name: String,
    val owner: CitizenTest, // Nested object
    val details: Map<String, String>
)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SanitizationUtilTest {
    private lateinit var sanitizationUtil: SanitizationUtil

    @BeforeEach
    fun setUp() {
        sanitizationUtil = SanitizationUtil()
    }

    @Test
    fun `should sanitize string inputs`() {
        val input = "<script>alert('test');</script>"
        val expected = HtmlUtils.htmlEscape(input)
        val result = sanitizationUtil.sanitize(input)

        assertEquals(expected, result)
    }

    @Test
    fun `should sanitize collections of strings`() {
        val input = listOf("<b>bold</b>", "<i>italic</i>")
        val expected = listOf(HtmlUtils.htmlEscape("<b>bold</b>"), HtmlUtils.htmlEscape("<i>italic</i>"))
        val result = sanitizationUtil.sanitize(input)

        assertEquals(expected, result)
    }

    @Test
    fun `should sanitize maps with string values`() {
        val input = mapOf("key1" to "<u>underline</u>", "key2" to "<a href='#'>link</a>")
        val expected = mapOf("key1" to HtmlUtils.htmlEscape("<u>underline</u>"), "key2" to HtmlUtils.htmlEscape("<a href='#'>link</a>"))
        val result = sanitizationUtil.sanitize(input)

        assertEquals(expected, result)
    }

    @Test
    fun `should sanitize custom data class with strings`() {
        val input = CitizenTest(name = "<strong>Name</strong>", age = 30, description = "<em>Description</em>")
        val expected =
            CitizenTest(
                name = HtmlUtils.htmlEscape("<strong>Name</strong>"),
                age = 30, // Age should remain unchanged
                description = HtmlUtils.htmlEscape("<em>Description</em>")
            )
        val result = sanitizationUtil.sanitize(input) as CitizenTest

        assertEquals(expected, result)
    }

    @Test
    fun `should sanitize nested objects in custom data class`() {
        val citizen = CitizenTest(name = "<strong>Name</strong>", age = 30, description = "<em>Description</em>")
        val input =
            BoatUpdateFormTest(
                name = "<boat>",
                owner = citizen,
                details = mapOf("info" to "<details>")
            )
        val expected =
            BoatUpdateFormTest(
                name = HtmlUtils.htmlEscape("<boat>"),
                owner =
                    CitizenTest(
                        name = HtmlUtils.htmlEscape("<strong>Name</strong>"),
                        age = 30,
                        description = HtmlUtils.htmlEscape("<em>Description</em>")
                    ),
                details = mapOf("info" to HtmlUtils.htmlEscape("<details>"))
            )
        val result =
            sanitizationUtil.sanitize(input) as BoatUpdateFormTest

        assertEquals(expected, result)
    }
}
