package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.utils.InExpr

class AmenityExpr(
    private val amenities: List<BoatSpaceAmenity>
) : InExpr<BoatSpaceAmenity>(
        "bs.amenity",
        amenities
    )
