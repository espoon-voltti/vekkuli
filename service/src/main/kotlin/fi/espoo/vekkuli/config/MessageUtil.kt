// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageUtil(
    private val messageSource: MessageSource
) {
    private val logger = KotlinLogging.logger {}

    final val localeFI = Locale("fi", "FI")

    // final val localeSV = Locale("sv", "FI")
    // final val localeEN = Locale.ENGLISH
    final val locales = listOf(localeFI) // , localeSV, localeEN)

    fun getMessage(
        code: String,
        args: List<Any> = emptyList(),
        locale: Locale = LocaleContextHolder.getLocale()
    ): String {
        try {
            return messageSource.getMessage(code, args.toTypedArray(), localeFI)
        } catch (e: Exception) {
            logger.error { "Missing message for code: $code for locale $locale: $e" }
            return code
        }
    }

    fun getLocalizedMap(
        key: String,
        code: String,
        args: List<Any> = emptyList()
    ): Map<String, String> =
        locales.associate { locale ->
            "$key${locale.language.replaceFirstChar { it.uppercaseChar() }}" to
                getMessage(code, args, locale)
        }
}
