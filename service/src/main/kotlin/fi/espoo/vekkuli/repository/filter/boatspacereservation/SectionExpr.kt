package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.utils.InExpr

class SectionExpr(
    private val sections: List<String>
) : InExpr<String>(
        "bs.section",
        sections
    )
