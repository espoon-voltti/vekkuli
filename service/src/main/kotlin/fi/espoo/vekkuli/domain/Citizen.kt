// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.domain

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.util.UUID

data class Citizen(
    val id: UUID,
    val nationalId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: String?,
    val postalCode: String?,
    val municipality: String?,
)

fun Handle.getCitizen(id: UUID): Citizen? {
    val query =
        createQuery(
            """
            SELECT * FROM citizen WHERE id = :id
            """.trimIndent()
        )
    query.bind("id", id)
    val citizens = query.mapTo<Citizen>().toList()
    return if (citizens.isEmpty()) null else citizens[0]
}
