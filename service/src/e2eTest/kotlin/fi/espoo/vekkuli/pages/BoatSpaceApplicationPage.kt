// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl

class BoatSpaceApplicationPage(private val page: Page) {
    val navigateTo = { page.navigate("$baseUrl/venepaikkahakemus") }

    val nameField = page.getByTestId("name")
    val nameRequiredError = page.getByTestId("name-error")

    val emailField = page.getByTestId("email")
    val emailRequiredError = page.getByTestId("email-error")

    val phoneField = page.getByTestId("phone")
    val phoneRequiredError = page.getByTestId("phone-error")

    val boatTypeSelect = page.getByTestId("boatType")
    val boatTypeRequiredError = page.getByTestId("boatType-error")

    val boatNameField = page.getByTestId("boatName")
    val boatNameRequiredError = page.getByTestId("boatName-error")

    val registrationCodeField = page.getByTestId("registrationCode")
    val registrationCodeRequiredError = page.getByTestId("registrationCode-error")

    val lengthField = page.getByTestId("length")
    val lengthRequiredError = page.getByTestId("length-error")

    val widthField = page.getByTestId("width")
    val widthRequiredError = page.getByTestId("width-error")

    val weightField = page.getByTestId("weight")
    val weightRequiredError = page.getByTestId("weight-error")

    val boatSpaceTypeSelect = page.getByTestId("boatSpaceType")
    val boatSpaceTypeRequiredError = page.getByTestId("boatSpaceType-error")

    val locationSelect = page.getByTestId("locationId")
    val locationRequiredError = page.getByTestId("locationId-error")

    val submitButton = page.getByTestId("submit")
}
