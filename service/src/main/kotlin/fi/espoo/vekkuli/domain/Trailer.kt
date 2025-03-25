package fi.espoo.vekkuli.domain

import java.util.*

data class Trailer(
    val id: Int,
    val registrationCode: String?,
    val reserverId: UUID,
    val widthCm: Int,
    val lengthCm: Int,
    val warnings: Set<ReservationWarningType> = emptySet(),
) {
    fun hasWarning(warning: ReservationWarningType): Boolean = warnings.contains(warning)

    fun hasAnyWarnings(): Boolean = warnings.isNotEmpty()
}
