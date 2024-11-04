
package fi.espoo.vekkuli.config

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

@Component
class LocaleUtil {
    fun getLocaleLanguageCode(): String = LocaleContextHolder.getLocale().language
}
