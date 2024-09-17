// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Scope
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

@Component
@Scope("request")
class MessageUtil(
    private val messageSource: MessageSource
) {
    fun getMessage(
        code: String,
        args: List<Any> = emptyList()
    ): String = messageSource.getMessage(code, args.toTypedArray(), LocaleContextHolder.getLocale())

    fun getLocale(): String = LocaleContextHolder.getLocale().toString()
}
