package fi.espoo.vekkuli.domain

import java.util.*

data class TrailerWithWarnings(
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

data class TrailerWithWarningsRow(
    val id: Int,
    val registrationCode: String?,
    val reserverId: UUID,
    val widthCm: Int,
    val lengthCm: Int,
    val warnings: List<String>,
) {
    fun toTrailerWithWarnings(): TrailerWithWarnings =
        TrailerWithWarnings(
            id = id,
            registrationCode = registrationCode,
            reserverId = reserverId,
            widthCm = widthCm,
            lengthCm = lengthCm,
            warnings = warnings.mapNotNull { runCatching { ReservationWarningType.valueOf(it) }.getOrNull() }.toSet(),
        )
}

data class TrailerRow(
    val id: Int,
    val registrationCode: String?,
    val reserverId: UUID,
    val widthCm: Int,
    val lengthCm: Int,
)
