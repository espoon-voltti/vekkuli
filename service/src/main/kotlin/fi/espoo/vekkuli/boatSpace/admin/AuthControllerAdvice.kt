package fi.espoo.vekkuli.boatSpace.admin

import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.EnvType
import fi.espoo.vekkuli.controllers.Utils.Companion.getEnv
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice(basePackages = ["fi.espoo.vekkuli.boatSpace.admin"])
class AuthControllerAdvice : ResponseEntityExceptionHandler() {
    @ModelAttribute
    fun handleAuth(request: HttpServletRequest) {
        val authenticatedUser = request.getAuthenticatedUser() ?: throw Unauthorized()

        if (getEnv() !in setOf(EnvType.Staging, EnvType.Local) && !authenticatedUser.isEmployee()) {
            throw NotFound()
        }
    }
}
