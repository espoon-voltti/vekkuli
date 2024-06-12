// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.common.getAppUser
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.getCitizen
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
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
        val authenticatedUser = request.getAuthenticatedUser()
        val user = authenticatedUser?.let { jdbi.inTransactionUnchecked { tx -> tx.getCitizen(it.id) } }
        val isAuthenticated = user != null
        model.addAttribute("isAuthenticated", isAuthenticated)
        if (user != null) {
            model.addAttribute("userName", "${user.firstName} ${user.lastName}")
        }
        return "citizen-home"
    }

    @GetMapping("/virkailija")
    fun citizenHome(
        request: HttpServletRequest,
        model: Model
    ): String {
        val authenticatedUser = request.getAuthenticatedUser()
        val user = authenticatedUser?.let { jdbi.inTransactionUnchecked { tx -> tx.getAppUser(it.id) } }
        val isAuthenticated = user != null
        model.addAttribute("isAuthenticated", isAuthenticated)
        if (user != null) {
            model.addAttribute("userName", "${user.firstName} ${user.lastName}")
        }
        return "home"
    }
}
