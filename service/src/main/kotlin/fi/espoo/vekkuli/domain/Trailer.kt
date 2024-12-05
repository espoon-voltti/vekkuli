package fi.espoo.vekkuli.domain

import java.util.*

data class Trailer(
    val id: Int,
    val registrationCode: String?,
    val reserverId: UUID,
    val widthCm: Int,
    val lengthCm: Int,
)
