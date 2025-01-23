package fi.espoo.vekkuli.boatSpace.admin

import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.config.ensureEmployeeId
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
        request.ensureEmployeeId()
        if (getEnv() == EnvType.Production) {
            throw NotFound()
        }
    }
}
