package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.utils.InExpr

class BoatSpaceTypeExpr(
    private val spaceTypes: List<BoatSpaceType>
) : InExpr<BoatSpaceType>(
        "bs.type",
        spaceTypes
    )
