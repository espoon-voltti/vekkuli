package fi.espoo.vekkuli.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import java.time.Duration
import java.util.*

@Configuration
class LanguageConfig : WebMvcConfigurer {
    @Bean
    fun localeResolver(): LocaleResolver {
        val cookieLocaleResolver = CookieLocaleResolver("locale")
        cookieLocaleResolver.setDefaultLocale(Locale("fi"))
        cookieLocaleResolver.setCookieMaxAge(Duration.ofDays(30))
        return cookieLocaleResolver
    }

    @Bean
    fun localeChangeInterceptor(): LocaleChangeInterceptor {
        val interceptor = LocaleChangeInterceptor()
        interceptor.paramName = "lang" // parameter to change language
        return interceptor
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(localeChangeInterceptor())
    }
}
