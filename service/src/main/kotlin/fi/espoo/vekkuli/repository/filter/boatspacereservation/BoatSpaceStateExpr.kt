package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.domain.BoatSpaceState
import fi.espoo.vekkuli.utils.InExpr

class BoatSpaceStateExpr(
    boatSpaceState: List<BoatSpaceState>
) : InExpr<Boolean>("bs.is_active", boatSpaceState.map { it == BoatSpaceState.Active })
