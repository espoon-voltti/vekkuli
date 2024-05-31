// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.config.AuthenticatedUser
import kotlinx.html.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LandingPageController {
    @GetMapping("/", produces = [TEXT_HTML_UTF8])
    fun landingPage(user: AuthenticatedUser?): String {
        return layout("Vekkuli") {
            div("container") {
                h1("title") { +"Vekkuli" }
                if (user != null) {
                    p {
                        +"Tervetuloa ${user.id}!"
                    }
                    a {
                        classes = setOf("button")
                        href = "/auth/saml/logout"
                        +"Kirjaudu ulos"
                    }
                } else {
                    a {
                        classes = setOf("button")
                        href = "/auth/saml/login"
                        +"Kirjaudu sisään"
                    }
                }
            }
        }
    }
}
