package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.utils.decimalToInt
import fi.espoo.vekkuli.utils.formatInt
import fi.espoo.vekkuli.utils.intToDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NumbersTest {
    @Test
    fun testCmToM() {
        assertEquals(BigDecimal("1.00"), intToDecimal(100))
        assertEquals(BigDecimal("1.50"), intToDecimal(150))
        assertEquals(BigDecimal("2.00"), intToDecimal(200))
        assertEquals(BigDecimal("2.30"), intToDecimal(230))
        assertEquals(BigDecimal("1.90"), intToDecimal(190))
        assertEquals(BigDecimal("0.00"), intToDecimal(0))
        assertEquals(BigDecimal("0.10"), intToDecimal(10))
        assertEquals(BigDecimal("0.90"), intToDecimal(90))
        assertEquals(BigDecimal("0.01"), intToDecimal(1))
        assertEquals(BigDecimal("0.05"), intToDecimal(5))
        assertEquals(BigDecimal("10.00"), intToDecimal(1000))
    }

    @Test
    fun testMToCm() {
        assertEquals(100, decimalToInt(BigDecimal("1.00")))
        assertEquals(150, decimalToInt(BigDecimal("1.50")))
        assertEquals(200, decimalToInt(BigDecimal("2.00")))
        assertEquals(230, decimalToInt(BigDecimal("2.30")))
        assertEquals(190, decimalToInt(BigDecimal("1.90")))
        assertEquals(0, decimalToInt(BigDecimal("0.00")))
        assertEquals(1, decimalToInt(BigDecimal("0.01")))
        assertEquals(10, decimalToInt(BigDecimal("0.10")))
        assertEquals(99, decimalToInt(BigDecimal("0.99")))
        assertEquals(101, decimalToInt(BigDecimal("1.01")))
    }

    @Test
    fun testCentsToEuro() {
        assertEquals("1,00", formatInt(100))
        assertEquals("1,50", formatInt(150))
        assertEquals("2,00", formatInt(200))
        assertEquals("2,30", formatInt(230))
        assertEquals("1,99", formatInt(199))
        assertEquals("0,00", formatInt(0))
        assertEquals("0,01", formatInt(1))
        assertEquals("0,10", formatInt(10))
        assertEquals("0,99", formatInt(99))
        assertEquals("10,00", formatInt(1000))
        assertEquals("10,01", formatInt(1001))
        assertEquals("100,00", formatInt(10000))
    }
}
