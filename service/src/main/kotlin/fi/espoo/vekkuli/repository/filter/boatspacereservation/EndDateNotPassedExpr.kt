package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.utils.AndExpr
import fi.espoo.vekkuli.utils.OperatorExpr
import fi.espoo.vekkuli.utils.OrExpr
import java.time.LocalDate

private class EndDateNotPassedIfNotCancelledExpr(
    endDate: LocalDate
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
    endDate: LocalDate
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
    endDate: LocalDate
) : OrExpr(
        listOf(
            EndDateNotPassedIfNotCancelledExpr(endDate),
            EndDateNotPassedIfCancelledExpr(endDate)
        )
    )

class EndDatePassedExpr(
    endDate: LocalDate
) : OrExpr(
        listOf(
            OperatorExpr(
                "bsr.end_date",
                "<=",
                endDate
            )
        )
    )
