package fi.espoo.vekkuli.domain

enum class BoatSpaceAmenity {
    None,
    Beam,
    WalkBeam,
    RearBuoy,
    Buoy,
    Trailer,
    Buck,
}

val baseAmenities =
    listOf(
        BoatSpaceAmenity.Beam,
        BoatSpaceAmenity.WalkBeam,
        BoatSpaceAmenity.RearBuoy,
        BoatSpaceAmenity.Buoy,
        BoatSpaceAmenity.Trailer,
        BoatSpaceAmenity.Buck
    )

val storageTypeAmenities = listOf(BoatSpaceAmenity.Trailer, BoatSpaceAmenity.Buck)

val actualAmenities = BoatSpaceAmenity.entries.toList().filter { it.name != "None" }
