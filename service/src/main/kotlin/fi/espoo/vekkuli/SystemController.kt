// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli

import fi.espoo.vekkuli.common.*
import fi.espoo.vekkuli.config.AuthenticatedUser
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.domain.CitizenAdUser
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.service.ReserverService
import mu.KotlinLogging
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * Controller for "system" endpoints intended to be only called from api-gateway
 * as the system internal user
 */
@RestController
@RequestMapping("/system")
class SystemController(
    private val jdbi: Jdbi,
    private val reserverService: ReserverService,
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/user-login")
    fun userLogin(
        @RequestBody adUser: AdUser
    ): AppUser =
        jdbi.inTransactionUnchecked { it.upsertAppUserFromAd(adUser) }.also {
            logger.audit(AuthenticatedUser(it.id, "user"), "USER_LOGIN")
        }

    @PostMapping("/citizen-login")
    fun citizenLogin(
        @RequestBody adUser: CitizenAdUser
    ): CitizenWithDetails = reserverService.upsertCitizenUserFromAd(adUser)

    @GetMapping("/users/{id}")
    fun getUser(
        @PathVariable id: UUID
    ): AppUser? = jdbi.inTransactionUnchecked { it.getAppUser(id) }
}
