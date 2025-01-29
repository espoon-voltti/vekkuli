package fi.espoo.vekkuli.boatSpace.citizenTrailer

import java.math.BigDecimal

data class UpdateTrailerInput(
    val registrationNumber: String,
    val width: BigDecimal,
    val length: BigDecimal,
)
