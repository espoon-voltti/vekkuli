// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.layout
import jakarta.servlet.http.HttpServletRequest
import kotlinx.html.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LandingPageController {
    @GetMapping("/", produces = [TEXT_HTML_UTF8])
    fun landingPage(request: HttpServletRequest): String {
        val authenticatedUser = request.getAuthenticatedUser()
        println(authenticatedUser)
        return layout("Vekkuli") {
            if (authenticatedUser == null) {
                div("container") {
                    h1("title") { +"Vekkuli" }
                    a {
                        classes = setOf("button")
                        href = "/auth/saml/login"
                        +"Kirjaudu sisään"
                    }
                }
            } else {
                div("container") {
                    h1("title") { +"Vekkuli" }
                    p {
                        +"Tervetuloa ${authenticatedUser.id}!"
                    }
                    a {
                        classes = setOf("button")
                        href = "/auth/saml/logout"
                        +"Kirjaudu ulos"
                    }
                }
            }
        }
    }
}
