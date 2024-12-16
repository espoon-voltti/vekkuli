package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.boatSpace.reservationStatus.ReservationStatus
import fi.espoo.vekkuli.utils.InExpr

class StatusExpr : InExpr<ReservationStatus> {
    constructor(
        statuses: List<ReservationStatus>,
        notIn: Boolean = false
    ) : super(columnName = "bsr.status", data = statuses, isNot = notIn)
    constructor(status: ReservationStatus, notIn: Boolean = false) : this(listOf(status), notIn)
}
