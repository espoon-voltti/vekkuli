// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.dataQa
import java.util.regex.Pattern

class StudentPage(private val page: Page) {
    val studentName = page.locator(dataQa("student-name"))

    fun assertUrl() {
        assertThat(page).hasURL(Pattern.compile("$baseUrl/oppivelvolliset/[a-f0-9\\-]+"))
    }
}
