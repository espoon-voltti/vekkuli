package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.utils.InExpr

class LocationExpr(
    private val locations: List<Int>
) : InExpr<Int>(
        "bs.location_id",
        locations
    )
