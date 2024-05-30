// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.util.Locale

@Configuration
class LocalizationConfig {
    @Bean
    fun messageSource(): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasenames("classpath:locales/messages")
        messageSource.setDefaultLocale(Locale.ENGLISH)
        return messageSource
    }

    @Bean
    fun messageUtil(messageSource: MessageSource): MessageUtil {
        return MessageUtil(messageSource)
    }
}
