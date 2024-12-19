package fi.espoo.vekkuli.views

import fi.espoo.vekkuli.config.MessageUtil
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseView {
    @Autowired lateinit var messageUtil: MessageUtil

    @Autowired lateinit var icons: Icons

    fun t(
        key: String,
        params: List<String> = emptyList()
    ): String = messageUtil.getMessage(key, params)
}
