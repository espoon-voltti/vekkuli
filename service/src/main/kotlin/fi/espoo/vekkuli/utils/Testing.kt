package fi.espoo.vekkuli.utils

import fi.espoo.vekkuli.controllers.Utils

fun addTestId(testId: String): String {
    if (Utils.isStagingOrProduction()) {
        return ""
    }
    return """data-testid="$testId""""
}
