package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.utils.ExecuteExpr

class ShowOnlyFreeSpacesExpr :
    ExecuteExpr(
        "(r.id IS NULL)"
    )
