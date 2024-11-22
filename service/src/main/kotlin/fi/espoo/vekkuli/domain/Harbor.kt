package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.domain.BoatSpaceOption

data class Harbor(
    val location: Location,
    val boatSpaces: List<BoatSpaceOption>
)
