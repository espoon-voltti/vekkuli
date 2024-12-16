package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.utils.centsToEuro
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.mToCm
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExtensionsTest {
    @Test
    fun testCmToM() {
        fun bd(value: String) = BigDecimal(value).setScale(1, RoundingMode.HALF_UP)

        assertEquals(bd("1.00"), 100.cmToM())
        assertEquals(bd("1.50"), 150.cmToM())
        assertEquals(bd("2.00"), 200.cmToM())
        assertEquals(bd("2.30"), 230.cmToM())
        assertEquals(bd("1.90"), 190.cmToM())
        assertEquals(bd("0.00"), 0.cmToM())
        assertEquals(bd("0.10"), 10.cmToM())
        assertEquals(bd("0.90"), 90.cmToM())
        assertEquals(bd("0.01"), 1.cmToM())
        assertEquals(bd("0.05"), 5.cmToM())
        assertEquals(bd("10.00"), 1000.cmToM())
    }

    @Test
    fun testMToCm() {
        assertEquals(100, BigDecimal("1.0").mToCm())
        assertEquals(150, BigDecimal("1.5").mToCm())
        assertEquals(200, BigDecimal("2.0").mToCm())
        assertEquals(230, BigDecimal("2.3").mToCm())
        assertEquals(190, BigDecimal("1.9").mToCm())
        assertEquals(0, BigDecimal("0").mToCm())
        assertEquals(1, BigDecimal("0.01").mToCm())
        assertEquals(10, BigDecimal("0.1").mToCm())
        assertEquals(99, BigDecimal("0.99").mToCm())
        assertEquals(101, BigDecimal("1.01").mToCm())
    }

    @Test
    fun testCentsToEuro() {
        assertEquals("1,00", 100.centsToEuro())
        assertEquals("1,50", 150.centsToEuro())
        assertEquals("2,00", 200.centsToEuro())
        assertEquals("2,30", 230.centsToEuro())
        assertEquals("1,99", 199.centsToEuro())
        assertEquals("0,00", 0.centsToEuro())
        assertEquals("0,01", 1.centsToEuro())
        assertEquals("0,10", 10.centsToEuro())
        assertEquals("0,99", 99.centsToEuro())
        assertEquals("10,00", 1000.centsToEuro())
        assertEquals("10,01", 1001.centsToEuro())
        assertEquals("100,00", 10000.centsToEuro())
    }
}
