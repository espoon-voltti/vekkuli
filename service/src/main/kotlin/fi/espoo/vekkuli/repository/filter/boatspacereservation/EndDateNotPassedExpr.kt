package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.boatSpace.reservationStatus.ReservationStatus
import fi.espoo.vekkuli.utils.AndExpr
import fi.espoo.vekkuli.utils.OperatorExpr
import fi.espoo.vekkuli.utils.OrExpr
import java.time.LocalDate

private class EndDateNotPassedIfNotCancelledExpr(
    private val endDate: LocalDate
) : AndExpr(
        listOf(
            StatusExpr(ReservationStatus.Cancelled, true),
            OperatorExpr(
                "bsr.end_date",
                ">=",
                endDate
            )
        )
    )

private class EndDateNotPassedIfCancelledExpr(
    private val endDate: LocalDate
) : AndExpr(
        listOf(
            StatusExpr(ReservationStatus.Cancelled),
            OperatorExpr(
                "bsr.end_date",
                ">",
                endDate
            )
        )
    )

class EndDateNotPassedExpr(
    private val endDate: LocalDate
) : OrExpr(
        listOf(
            EndDateNotPassedIfNotCancelledExpr(endDate),
            EndDateNotPassedIfCancelledExpr(endDate)
        )
    )
