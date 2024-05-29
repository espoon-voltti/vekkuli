// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.domain

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

data class Location(
    val id: String,
    val name: String,
    val address: String
)

fun Handle.getLocations(): List<Location> = createQuery("SELECT * FROM location").mapTo<Location>().toList()
