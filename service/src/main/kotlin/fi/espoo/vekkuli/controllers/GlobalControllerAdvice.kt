package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.getCitizen
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
    constructor(private val jdbi: Jdbi) {
        @ModelAttribute
        fun addAttributes(
            model: Model,
            request: HttpServletRequest
        ) {
            val authenticatedUser = request.getAuthenticatedUser()
            model.addAttribute("isAuthenticated", authenticatedUser != null)
            val user = authenticatedUser?.let { jdbi.inTransactionUnchecked { tx -> tx.getCitizen(it.id) } }
            if (user != null) {
                model.addAttribute("userName", "${user.firstName} ${user.lastName}")
            }
        }
    }
