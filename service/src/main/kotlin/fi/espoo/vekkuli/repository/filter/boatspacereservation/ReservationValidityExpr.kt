package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.utils.InExpr

class ReservationValidityExpr(
    private val validities: List<ReservationValidity>
) : InExpr<ReservationValidity>(
        "bsr.validity",
        validities
    )
