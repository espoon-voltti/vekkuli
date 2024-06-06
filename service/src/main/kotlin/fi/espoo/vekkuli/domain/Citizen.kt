// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.domain

import java.util.UUID

data class Citizen(
    val id: UUID,
    val nationalId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
)
