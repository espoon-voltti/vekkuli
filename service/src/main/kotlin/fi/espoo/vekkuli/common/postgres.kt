package fi.espoo.vekkuli.common

import java.time.LocalDateTime

fun String.toPostgresTimestamp(): LocalDateTime = LocalDateTime.parse(this.replace(" ", "T"))