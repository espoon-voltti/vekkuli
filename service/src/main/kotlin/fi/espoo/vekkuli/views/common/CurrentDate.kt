package fi.espoo.vekkuli.views.common

import fi.espoo.vekkuli.controllers.EnvType
import fi.espoo.vekkuli.controllers.Utils.Companion.getEnv
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service

@Service
class CurrentDate(
    val timeProvider: TimeProvider
) {
    fun render(): String =
        if (getEnv() != EnvType.Production) {
            "<div>Päivämäärä: ${timeProvider.getCurrentDate()}</div>"
        } else {
            ""
        }
}
