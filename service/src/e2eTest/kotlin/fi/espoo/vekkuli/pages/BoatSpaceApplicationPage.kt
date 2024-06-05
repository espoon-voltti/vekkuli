// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl

class BoatSpaceApplicationPage(private val page: Page) {
    val navigateTo = { page.navigate("$baseUrl/venepaikkahakemus") }
    val nameField = page.getByTestId("name")
    val emailField = page.getByTestId("email")
    val phoneField = page.getByTestId("phone")
    val boatTypeSelect = page.getByTestId("boatType")
    val boatNameField = page.getByTestId("boatName")
    val registrationCodeField = page.getByTestId("registrationCode")
    val lengthField = page.getByTestId("length")
    val widthField = page.getByTestId("width")
    val weightField = page.getByTestId("weight")
    val boatSpaceTypeSelect = page.getByTestId("boatSpaceType")
    val locationSelect = page.getByTestId("locationId")
    val submitButton = page.getByTestId("submit")
}
