// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import kotlin.test.Test

class BoatSpaceApplicationTests : PlaywrightTest() {
    @Test
    fun `submit an application for a boat space`() {
        val page = getPageWithDefaultOptions()
        page.navigate(baseUrl)
        page.getByTestId("loginButton").click()
        page.getByText("Kirjaudu").click()

        page.navigate("$baseUrl/venepaikkahakemus")
        page.getByTestId("name").fill("Testi")
        page.getByTestId("email").fill("test@test.com")
        page.getByTestId("phone").fill("1234567890")
        page.getByTestId("boatType").selectOption("Rowboat")
        page.getByTestId("boatName").fill("Testi")
        page.getByTestId("registrationCode").fill("123456")
        page.getByTestId("length").fill("5")
        page.getByTestId("width").fill("2")
        page.getByTestId("weight").fill("100")
        page.getByTestId("boatSpaceType").selectOption("Harbor space")
        page.getByTestId("locationId").selectOption("Kivenlahti")
        page.getByTestId("submit").click()
        assertThat(page.getByText("Application received")).isVisible()
    }
}
