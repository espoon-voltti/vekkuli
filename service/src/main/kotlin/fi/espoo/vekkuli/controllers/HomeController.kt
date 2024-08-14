package fi.espoo.vekkuli.controllers

import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    @Autowired
    lateinit var jdbi: Jdbi

    @GetMapping("/")
    fun users(
        request: HttpServletRequest,
        model: Model
    ): String {
        return "citizen-home"
    }

    @GetMapping("/virkailija")
    fun citizenHome(
        request: HttpServletRequest,
        model: Model
    ): String {
        return "home"
    }
}
