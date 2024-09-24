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
    val address: LocalizedName,
    val postOffice: LocalizedName,
    val postalCode: String?,
    val municipalityCode: Int,
)

fun Handle.upsertCitizenUserFromAd(adUser: CitizenAdUser): Citizen =
    createQuery(
        // language=SQL
        """
        INSERT INTO citizen (
          national_id, 
          first_name, 
          last_name, 
          phone, 
          email, 
          address, 
          address_sv,
          postal_code, 
          municipality_code,
          post_office,
          post_office_sv
          )
        VALUES (
          :nationalId, 
          :firstName, 
          :lastName, 
          '', 
          '', 
          COALESCE(:address.fi, ''), 
          :address.sv, 
          COALESCE(:postalCode, ''), 
          :municipalityCode,
          :postOffice.fi,
          :postOffice.sv
        )
        ON CONFLICT (national_id) DO UPDATE
        SET 
          updated = now(), 
          first_name = :firstName, 
          last_name = :lastName, 
          municipality_code = :municipalityCode,
          address=COALESCE(:address.fi, ''), 
          address_sv=:address.sv,
          postal_code=COALESCE(:postalCode, ''), 
          post_office=:postOffice.fi,
          post_office_sv=:postOffice.sv
        RETURNING *
        """.trimIndent()
    ).bindKotlin(adUser)
        .mapTo<Citizen>()
        .one()
