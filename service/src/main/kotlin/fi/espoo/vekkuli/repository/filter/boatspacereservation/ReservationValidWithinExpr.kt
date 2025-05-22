package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.utils.AndExpr
import fi.espoo.vekkuli.utils.OperatorExpr
import java.time.LocalDate

class ReservationValidWithinExpr(
    startDate: LocalDate?,
    endDate: LocalDate?,
) : AndExpr(
        listOfNotNull(
            startDate?.let { startDateExpr(it) },
            endDate?.let { endDateExpr(it) },
        )
    )

fun startDateExpr(startDate: LocalDate) = OperatorExpr("start_date", ">=", startDate)

fun endDateExpr(endDate: LocalDate) = OperatorExpr("end_date", "<=", endDate)
