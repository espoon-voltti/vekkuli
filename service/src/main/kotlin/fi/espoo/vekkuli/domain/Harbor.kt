package fi.espoo.vekkuli.domain

data class Harbor(
    val location: Location,
    val boatSpaces: List<BoatSpaceOption>
)
