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