// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.util.*

@Component
@Scope("request")
class MessageUtil(private val messageSource: MessageSource) {
    var locale: Locale = Locale("fi")
    fun getMessage(code: String): String {
        return messageSource.getMessage(code, null, locale)
    }
}