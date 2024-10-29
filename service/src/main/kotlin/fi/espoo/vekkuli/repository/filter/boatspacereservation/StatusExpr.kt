package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.utils.InExpr

class StatusExpr(
    private val statuses: List<ReservationStatus>
) : InExpr<ReservationStatus>(
        "bsr.status",
        statuses
    )
