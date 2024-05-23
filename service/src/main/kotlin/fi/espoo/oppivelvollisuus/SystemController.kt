// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.oppivelvollisuus

import fi.espoo.oppivelvollisuus.common.AdUser
import fi.espoo.oppivelvollisuus.common.AppUser
import fi.espoo.oppivelvollisuus.common.getAppUser
import fi.espoo.oppivelvollisuus.common.upsertAppUserFromAd
import fi.espoo.oppivelvollisuus.config.AuthenticatedUser
import fi.espoo.oppivelvollisuus.config.audit
import mu.KotlinLogging
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
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
class SystemController {
    @Autowired
    lateinit var jdbi: Jdbi

    private val logger = KotlinLogging.logger {}

    @PostMapping("/user-login")
    fun userLogin(
        @RequestBody adUser: AdUser
    ): AppUser {
        return jdbi.inTransactionUnchecked { it.upsertAppUserFromAd(adUser) }.also {
            logger.audit(AuthenticatedUser(it.id), "USER_LOGIN")
        }
    }

    @GetMapping("/users/{id}")
    fun getUser(
        @PathVariable id: UUID
    ): AppUser? {
        return jdbi.inTransactionUnchecked { it.getAppUser(id) }
    }
}
