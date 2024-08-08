// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.common

import fi.espoo.vekkuli.domain.Citizen
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.bindKotlin
import org.jdbi.v3.core.kotlin.mapTo

data class LocalizedName(
    val sv: String?,
    val fi: String?
)

data class CitizenAdUser(
    val nationalId: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phone: String?,
    val address: LocalizedName?,
    val postalCode: String?,
    val town: LocalizedName?,
)

fun Handle.upsertCitizenUserFromAd(adUser: CitizenAdUser): Citizen {
    var params =
        adUser.copy(
            // ensures that fields are set to null if the object is null
            address =
                adUser.address?.let { LocalizedName(it.fi.orEmpty(), it.sv.orEmpty()) } ?: LocalizedName(
                    null,
                    null
                ),
            town =
                adUser.town?.let { LocalizedName(it.fi.orEmpty(), it.sv.orEmpty()) } ?: LocalizedName(
                    null,
                    null
                ),
        )

    return createQuery(
        // language=SQL
        """
 INSERT INTO citizen (national_id, first_name, last_name, phone, email, address, postal_code, municipality)
 VALUES (:nationalId, :firstName, :lastName, COALESCE(:phone, ''), COALESCE(:email, ''), COALESCE(:address.fi, ''), COALESCE(:postalCode, ''), COALESCE(:town.fi, ''))
 ON CONFLICT (national_id) DO UPDATE
 SET updated = now(), first_name = :firstName, last_name = :lastName, email = COALESCE(:email, ''), phone = COALESCE(:phone, '')
 RETURNING *
    """
            .trimIndent()
    )
        .bindKotlin(params)
        .mapTo<Citizen>()
        .one()
}
