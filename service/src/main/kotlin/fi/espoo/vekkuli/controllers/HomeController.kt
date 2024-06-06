// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.getAuthenticatedUser
import jakarta.servlet.http.HttpServletRequest
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
    fun users(
        request: HttpServletRequest,
        model: Model
    ): String {
        model.addAttribute("user", request.getAuthenticatedUser())
        return "citizen-home"
    }

    @GetMapping("/virkailija")
    fun citizenHome(
        request: HttpServletRequest,
        model: Model
    ): String {
        model.addAttribute("user", request.getAuthenticatedUser())
        return "home"
    }
}
