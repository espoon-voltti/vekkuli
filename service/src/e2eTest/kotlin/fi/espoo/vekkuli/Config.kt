// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli

val runningInDocker = System.getenv("E2E_ENV") == "docker"
val baseUrl = if (runningInDocker) "http://api-gateway:3000" else "http://localhost:3000"

const val E2E_DEBUG_LOGGING = false
