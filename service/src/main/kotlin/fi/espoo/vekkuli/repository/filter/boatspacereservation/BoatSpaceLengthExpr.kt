package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.utils.InExpr

class BoatSpaceLengthExpr(
    lengths: List<Int>
) : InExpr<Int>(
        "bs.length_cm",
        lengths
    )
