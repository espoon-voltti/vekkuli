package fi.espoo.vekkuli.boatSpace.citizenHome

data class HomeViewParameters(
    val typeSections: List<HomeViewSection>,
)

data class HomeViewSection(
    val title: String,
    val season: String,
    val periods: List<String>
)
