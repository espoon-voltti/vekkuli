package fi.espoo.vekkuli.views

import fi.espoo.vekkuli.config.MessageUtil
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseView {
    @Autowired lateinit var messageUtil: MessageUtil

    fun t(key: String): String = messageUtil.getMessage(key)
}
