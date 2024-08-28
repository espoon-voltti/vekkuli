package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.common.getAppUser
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.service.CitizenService
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class GlobalControllerAdvice
    @Autowired
    constructor(
        private val jdbi: Jdbi,
        private val messageUtil: MessageUtil,
        private val citizenService: CitizenService
    ) {
        @ModelAttribute
        fun addAttributes(
            model: Model,
            request: HttpServletRequest
        ) {
            val authenticatedUser = request.getAuthenticatedUser()
            model.addAttribute("isAuthenticated", authenticatedUser != null)

            if (authenticatedUser?.type == "citizen") {
                val user =
                    authenticatedUser.let {
                        citizenService.getCitizen(authenticatedUser.id)
                    }

                if (user != null) {
                    model.addAttribute("userName", "${user.firstName} ${user.lastName}")
                }
            } else if (authenticatedUser?.type == "employee") {
                val user =
                    authenticatedUser.let {
                        jdbi.inTransactionUnchecked { tx ->
                            tx.getAppUser(authenticatedUser.id)
                        }
                    }

                if (user != null) {
                    model.addAttribute("userName", "${user.firstName} ${user.lastName}")
                }
            }
        }

        @ModelAttribute("currentUri")
        fun currentUri(request: HttpServletRequest): String = request.requestURI

        @ModelAttribute("lang")
        fun setLanguage(request: HttpServletRequest): String {
            val locale = messageUtil.getLocale()
            // Get the first language from the Accept-Language header
            return locale.split("_")[0]
        }
    }
