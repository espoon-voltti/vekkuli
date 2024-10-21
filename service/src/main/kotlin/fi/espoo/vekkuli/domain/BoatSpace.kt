package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.utils.*

enum class ReservationValidity {
    Indefinite,
    FixedTerm,
}

data class BoatSpaceOption(
    val id: Int,
    val section: String,
    val placeNumber: Int,
    val widthCm: Int,
    val lengthCm: Int,
    val priceCents: Int,
    val locationName: String,
    val amenity: BoatSpaceAmenity,
    val formattedSizes: String = "${widthCm.cmToM()} x ${lengthCm.cmToM()} m".replace('.', ',')
) {
    val priceInEuro: Double
        get() = priceCents / 100.0
}
