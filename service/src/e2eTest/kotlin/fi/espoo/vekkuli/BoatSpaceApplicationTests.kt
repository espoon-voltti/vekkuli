// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.pages.BoatSpaceApplicationPage
import kotlin.test.Test

class BoatSpaceApplicationTests : PlaywrightTest() {
    @Test
    fun `submit an application for a boat space`() {
        val page = getPageWithDefaultOptions()
        page.navigate(baseUrl)
        page.getByTestId("loginButton").click()
        page.getByText("Kirjaudu").click()

        val applicationPage = BoatSpaceApplicationPage(page)
        applicationPage.navigateTo()

        applicationPage.submitButton.click()

        assertThat(applicationPage.nameRequiredError).isVisible()
        assertThat(applicationPage.emailRequiredError).isVisible()
        assertThat(applicationPage.phoneRequiredError).isVisible()
        assertThat(applicationPage.boatTypeRequiredError).isVisible()
        assertThat(applicationPage.boatNameRequiredError).isVisible()
        assertThat(applicationPage.registrationCodeRequiredError).isVisible()
        assertThat(applicationPage.lengthRequiredError).isVisible()
        assertThat(applicationPage.widthRequiredError).isVisible()
        assertThat(applicationPage.weightRequiredError).isVisible()
        assertThat(applicationPage.boatSpaceTypeRequiredError).isVisible()
        assertThat(applicationPage.locationRequiredError).isVisible()

        applicationPage.nameField.fill("Testi")
        applicationPage.emailField.fill("test@test.com")
        applicationPage.phoneField.fill("1234567890")
        applicationPage.boatTypeSelect.selectOption("Rowboat")
        applicationPage.boatNameField.fill("Testi")
        applicationPage.registrationCodeField.fill("123456")
        applicationPage.lengthField.fill("5")
        applicationPage.widthField.fill("2")
        applicationPage.weightField.fill("100")
        applicationPage.boatSpaceTypeSelect.selectOption("Harbor space")
        applicationPage.locationSelect.selectOption("Kivenlahti")

        applicationPage.submitButton.click()

        assertThat(page.getByText("Application received")).isVisible()
    }
}
