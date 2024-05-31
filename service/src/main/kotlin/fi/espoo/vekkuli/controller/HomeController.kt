package fi.espoo.vekkuli.controller

import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController() {
    @Autowired
    lateinit var jdbi: Jdbi

    @GetMapping("/")
    fun users(model: Model): String {
        model.addAttribute("user", null)
        return "home"
    }
}
