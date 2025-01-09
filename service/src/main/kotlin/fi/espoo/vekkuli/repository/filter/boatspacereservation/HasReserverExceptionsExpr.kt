package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.utils.ExecuteExpr

class HasReserverExceptionsExpr :
    ExecuteExpr(
        "r.espoo_rules_applied IS TRUE"
    )
