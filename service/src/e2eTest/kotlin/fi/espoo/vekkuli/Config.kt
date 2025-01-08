// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli

val runningInDocker = System.getenv("ENVIRONMENT") == "local-docker"

val baseUrl = if (runningInDocker) "http://frontend" else "http://localhost:9000"
val baseUrlWithEnglishLangParam = "$baseUrl?lang=en"
val citizenPageInEnglish = "$baseUrl/kuntalainen/omat-tiedot?lang=en"
val employeeHomePage = "$baseUrl/virkailija"

const val E2E_DEBUG_LOGGING = false
