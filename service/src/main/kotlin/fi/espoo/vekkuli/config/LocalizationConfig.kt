package fi.espoo.vekkuli.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.util.*

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
