package fi.espoo.vekkuli.utils

fun Int.cmToM(): Double = this / 100.0

fun Double.mToCm(): Int = (this * 100F).toInt()

fun Float?.mToCm(): Int? = if (this == null) null else (this * 100F).toInt()
