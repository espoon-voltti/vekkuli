package fi.espoo.vekkuli.boatSpace.dev

import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.EnvType
import fi.espoo.vekkuli.controllers.Utils.Companion.getEnv
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice(basePackages = ["fi.espoo.vekkuli.boatSpace.dev"])
class DevAuthControllerAdvice : ResponseEntityExceptionHandler() {
    @ModelAttribute
    fun handleAuth(request: HttpServletRequest) {
        // This should *never* be enabled in production
        if (getEnv() == EnvType.Production) {
            throw NotFound()
        }

        // Allow access from both citizen & employee side for ease of testing
        request.getAuthenticatedUser() ?: throw Unauthorized()
    }
}
