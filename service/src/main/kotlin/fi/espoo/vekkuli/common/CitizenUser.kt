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
    val name: String,
    val email: String?,
    val phone: String?
)

// data class UserBasics(
//    @PropagateNull val id: UUID,
//    val name: String
// )
//
fun Handle.upsertCitizenUserFromAd(adUser: CitizenAdUser): Citizen =
    createQuery(
        // language=SQL
        """
 INSERT INTO citizen (national_id, name, phone, email)
 VALUES (:nationalId, :name, COALESCE(:phone, ''), COALESCE(:email, ''))
 ON CONFLICT (national_id) DO UPDATE
 SET updated = now(), name = :name, email = COALESCE(:email, ''), phone = COALESCE(:phone, '')
 RETURNING *
    """
            .trimIndent()
    )
        .bindKotlin(adUser)
        .mapTo<Citizen>()
        .one()

// fun Handle.getAppUsers(): List<AppUser> =
//    createQuery(
//        """
//    SELECT id, external_id, first_name, last_name, email
//    FROM app_user
//    WHERE NOT system_user
// """
//    ).mapTo<AppUser>().list()
//
// fun Handle.getAppUser(id: UUID) =
//    createQuery(
//        // language=SQL
//        """
// SELECT id, external_id, first_name, last_name, email
// FROM app_user
// WHERE id = :id AND NOT system_user
//    """
//            .trimIndent()
//    )
//        .bind("id", id)
//        .mapTo<AppUser>()
//        .findOne()
//        .getOrNull()
