package fi.espoo.vekkuli.domain

enum class BoatSpaceAmenity {
    None,
    Buoy,
    RearBuoy,
    Beam,
    WalkBeam,
    Trailer,
    Buck,
}

val slipAmenities = BoatSpaceAmenity.entries.toList().filter { it.name != "None" && it.name != "Trailer" && it.name != "Buck" }

val storageTypeAmenities = listOf(BoatSpaceAmenity.Trailer, BoatSpaceAmenity.Buck)

val actualAmenities = BoatSpaceAmenity.entries.toList().filter { it.name != "None" }
