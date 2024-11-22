package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.utils.*

enum class ReservationValidity {
    Indefinite,
    FixedTerm,
}

data class BoatSpaceOption(
    val id: Int,
    val place: String,
    val widthCm: Int,
    val lengthCm: Int,
    val priceCents: Int,
    val locationName: String,
    val locationAddress: String,
    val amenity: BoatSpaceAmenity,
    val formattedSizes: String =
        if (amenity != BoatSpaceAmenity.Buoy) "${widthCm.cmToM()} x ${lengthCm.cmToM()} m".replace('.', ',') else ""
) {
    val priceInEuro: Double
        get() = priceCents / 100.0
}
