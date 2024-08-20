// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.domain

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.jdbi.v3.core.kotlin.mapTo
import java.time.LocalDate
import java.util.*

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
) {
    companion object {
        fun getById(
            id: UUID,
            jdbi: Jdbi
        ): Citizen? {
            return jdbi.inTransactionUnchecked { tx -> tx.getCitizen(id) }
        }
    }
}

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

fun Handle.updateCitizen(
    id: UUID,
    phone: String,
    email: String,
): Citizen {
    val query =
        createQuery(
            """
            UPDATE citizen
            SET phone = :phone, email = :email, updated = :updated
            WHERE id = :id
            RETURNING *
            """.trimIndent()
        )
    query.bind("id", id)
    query.bind("phone", phone)
    query.bind("email", email)
    query.bind("updated", LocalDate.now())

    return query.mapTo<Citizen>().one()
}
