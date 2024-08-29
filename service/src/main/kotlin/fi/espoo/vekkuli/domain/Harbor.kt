package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.repository.BoatSpaceOption

data class Harbor(
    val location: Location,
    val boatSpaces: List<BoatSpaceOption>
)
