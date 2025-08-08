package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.utils.ExecuteExpr

class HasWarningExpr :
    ExecuteExpr(
        "rw.key IS NOT NULL"
    )

class HasGeneralWarningExpr :
    ExecuteExpr(
        "rw.key = 'GeneralReservationWarning'"
    )
