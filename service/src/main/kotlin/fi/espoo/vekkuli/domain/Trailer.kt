package fi.espoo.vekkuli.domain

import java.util.*

data class Trailer(
    val id: Int,
    val registrationCode: String?,
    val reserverId: UUID,
    val widthCm: Int,
    val lengthCm: Int,
    val warnings: Set<String> = emptySet(),
) {
    fun hasWarning(warning: String): Boolean = warnings.contains(warning)

    fun hasAnyWarnings(): Boolean = warnings.isNotEmpty()
}
