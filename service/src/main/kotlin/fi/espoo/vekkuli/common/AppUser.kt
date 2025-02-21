// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.common

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.bindKotlin
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.PropagateNull
import java.util.*
import kotlin.jvm.optionals.getOrNull

data class AdUser(
    val externalId: String,
    val firstName: String,
    val lastName: String,
    val email: String?
)

data class AppUser(
    val id: UUID,
    val externalId: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val fullName: String = "$firstName $lastName"
)

data class UserBasics(
    @PropagateNull val id: UUID,
    val name: String
)

fun Handle.upsertAppUserFromAd(adUser: AdUser): AppUser =
    createQuery(
        // language=SQL
        """
INSERT INTO app_user (external_id, first_name, last_name, email)
VALUES (:externalId, :firstName, :lastName, :email)
ON CONFLICT (external_id) DO UPDATE
SET updated = now(), first_name = :firstName, last_name = :lastName, email = :email
RETURNING id, external_id, first_name, last_name, email
        """.trimIndent()
    ).bindKotlin(adUser)
        .mapTo<AppUser>()
        .one()

fun Handle.getAppUsers(): List<AppUser> =
    createQuery(
        """
    SELECT id, external_id, first_name, last_name, email
    FROM app_user
    WHERE is_system_user IS FALSE
"""
    ).mapTo<AppUser>().list()

fun Handle.getAppUser(id: UUID) =
    createQuery(
        // language=SQL
        """
SELECT id, external_id, first_name, last_name, email
FROM app_user 
WHERE id = :id AND is_system_user IS FALSE
        """.trimIndent()
    ).bind("id", id)
        .mapTo<AppUser>()
        .findOne()
        .getOrNull()
