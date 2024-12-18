package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.common.getAppUser
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.service.ReserverService
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
        private val reserverService: ReserverService
    ) {
        @ModelAttribute
        fun addAttributes(
            model: Model,
            request: HttpServletRequest
        ) {
            val authenticatedUser = request.getAuthenticatedUser()
            model.addAttribute("isAuthenticated", authenticatedUser != null)
            model.addAttribute("isAuthenticatedEmployee", false)

            if (authenticatedUser?.type == "citizen") {
                val user =
                    authenticatedUser.let {
                        reserverService.getCitizen(authenticatedUser.id)
                    }

                if (user != null) {
                    model.addAttribute("userName", "${user.firstName} ${user.lastName}")
                }
            } else if (authenticatedUser?.type == "user") {
                val user =
                    authenticatedUser.let {
                        jdbi.inTransactionUnchecked { tx ->
                            tx.getAppUser(authenticatedUser.id)
                        }
                    }
                model.addAttribute("isAuthenticatedEmployee", true)

                if (user != null) {
                    model.addAttribute("userName", "${user.firstName} ${user.lastName}")
                }
            }
        }

        @ModelAttribute("currentUri")
        fun currentUri(request: HttpServletRequest): String = request.requestURI
    }
