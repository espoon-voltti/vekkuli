package fi.espoo.vekkuli.boatSpace.citizen

import java.math.BigDecimal

data class UpdateTrailerInformationInput(
    val id: Int,
    val registrationNumber: String,
    val width: BigDecimal,
    val length: BigDecimal,
)
