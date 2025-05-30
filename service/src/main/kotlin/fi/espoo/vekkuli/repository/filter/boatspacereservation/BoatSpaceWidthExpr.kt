package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.utils.InExpr

class BoatSpaceWidthExpr(
    widths: List<Int>
) : InExpr<Int>(
        "bs.width_cm",
        widths
    )
