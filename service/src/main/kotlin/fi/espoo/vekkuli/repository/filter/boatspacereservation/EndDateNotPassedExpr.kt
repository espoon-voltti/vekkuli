package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.utils.OperatorExpr
import java.time.LocalDate

class EndDateNotPassedExpr(
    private val endDate: LocalDate
) : OperatorExpr(
        "bsr.end_date",
        ">=",
        endDate
    )
