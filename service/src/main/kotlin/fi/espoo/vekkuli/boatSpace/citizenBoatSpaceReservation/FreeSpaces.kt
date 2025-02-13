package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

data class SpaceSize(
    val width: Int,
    val length: Int,
)

data class FreeSpace(
    val id: Int,
    val size: SpaceSize,
    val amenity: String,
    val price: Int,
    val identifier: String
)

data class Place(
    val id: Int,
    val name: String,
    val address: String,
)

data class PlacesWithFreeSpaces(
    val place: Place,
    val spaces: List<FreeSpace>
)
