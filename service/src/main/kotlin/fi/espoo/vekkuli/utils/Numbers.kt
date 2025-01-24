package fi.espoo.vekkuli.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun formatDecimal(num: BigDecimal?): String =
    if (num != null) {
        num.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",")
    } else {
        ""
    }

fun formatInt(cents: Int): String = formatDecimal(intToDecimal(cents))

fun formatInt(cents: Int?): String = if (cents != null) formatDecimal(intToDecimal(cents)) else ""

fun intToDecimal(cents: Int): BigDecimal = BigDecimal(cents).divide(BigDecimal(100)).setScale(2)

fun intToDecimal(cents: Int?): BigDecimal? = if (cents != null) intToDecimal(cents) else null

fun decimalToInt(wholes: BigDecimal): Int = wholes.multiply(BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).toInt()

fun decimalToInt(wholes: BigDecimal?): Int? = if (wholes != null) decimalToInt(wholes) else null

fun discountedPriceInCents(
    priceCents: Int,
    discountPercentage: Int?
): Int =
    if (discountPercentage != null && discountPercentage > 0 && priceCents > 0) {
        priceCents - (priceCents * discountPercentage / 100)
    } else {
        priceCents
    }
