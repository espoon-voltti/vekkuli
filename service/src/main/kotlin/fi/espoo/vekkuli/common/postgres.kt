// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.common

import java.time.LocalDateTime

fun String.toPostgresTimestamp(): LocalDateTime = LocalDateTime.parse(this.replace(" ", "T"))