package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.utils.formatInt

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
    val locationId: Int,
    val amenity: BoatSpaceAmenity,
    val formattedSizes: String =
        if (amenity != BoatSpaceAmenity.Buoy) "${formatInt(widthCm)} x ${formatInt(lengthCm)} m" else ""
) {
    val formattedPrice: String
        get() = formatInt(priceCents)
}
