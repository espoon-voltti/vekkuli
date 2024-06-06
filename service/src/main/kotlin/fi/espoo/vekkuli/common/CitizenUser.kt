// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.common

import fi.espoo.vekkuli.domain.Citizen
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.bindKotlin
import org.jdbi.v3.core.kotlin.mapTo

data class CitizenAdUser(
    val nationalId: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phone: String?
)

fun Handle.upsertCitizenUserFromAd(adUser: CitizenAdUser): Citizen =
    createQuery(
        // language=SQL
        """
 INSERT INTO citizen (national_id, first_name, last_name, phone, email)
 VALUES (:nationalId, :firstName, :lastName, COALESCE(:phone, ''), COALESCE(:email, ''))
 ON CONFLICT (national_id) DO UPDATE
 SET updated = now(), first_name = :firstName, last_name = :lastName, email = COALESCE(:email, ''), phone = COALESCE(:phone, '')
 RETURNING *
    """
            .trimIndent()
    )
        .bindKotlin(adUser)
        .mapTo<Citizen>()
        .one()
