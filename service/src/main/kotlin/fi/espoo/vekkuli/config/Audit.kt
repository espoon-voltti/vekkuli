// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.config

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KMarkerFactory
import io.github.oshai.kotlinlogging.Marker
import net.logstash.logback.argument.StructuredArguments

val AUDIT_MARKER: Marker = KMarkerFactory.getMarker("AUDIT_EVENT")

fun KLogger.audit(
    user: AuthenticatedUser,
    eventCode: String,
    meta: Map<String, String> = emptyMap()
) {
    val data =
        mapOf<String, Any?>(
            "userId" to user.id,
            "userType" to user.type,
            "meta" to meta,
            "eventCode" to eventCode
        )

    atWarn(AUDIT_MARKER) {
        message = eventCode
        arguments = arrayOf(StructuredArguments.entries(data))
    }
}
