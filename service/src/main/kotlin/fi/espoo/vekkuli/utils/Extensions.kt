package fi.espoo.vekkuli.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun Int.cmToM(): BigDecimal =
    BigDecimal(this / 100.0)
        .setScale(1, RoundingMode.HALF_UP)

fun BigDecimal.mToCm(): Int =
    (this * BigDecimal(100))
        .setScale(0, RoundingMode.HALF_UP)
        .toInt()

fun Int.centsToEuro(): String =
    this
        .centToEuro()
        .toString()
        .replace(".", ",")

fun Int.centToEuro(): BigDecimal =
    BigDecimal(this)
        .divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
