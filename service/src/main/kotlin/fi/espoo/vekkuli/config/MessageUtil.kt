// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.config

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageUtil(
    private val messageSource: MessageSource
) {
    fun getMessage(
        code: String,
        args: List<Any> = emptyList(),
        locale: Locale = LocaleContextHolder.getLocale()
    ): String = messageSource.getMessage(code, args.toTypedArray(), locale)

    fun getLocaleLanguageCode(): String = LocaleContextHolder.getLocale().language
}
