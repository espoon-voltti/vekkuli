// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl

class BoatSpaceApplicationPage(private val page: Page) {
    val navigateTo: () -> Unit = { page.navigate("$baseUrl/kuntalainen/venepaikkahakemus") }

    val amenitySelect = page.getByTestId("amenity")
    val amenityRequiredError = page.getByTestId("amenity-error")

    val nameField = page.getByTestId("name")
    val nameRequiredError = page.getByTestId("name-error")

    val emailField = page.getByTestId("email")
    val emailRequiredError = page.getByTestId("email-error")

    val phoneField = page.getByTestId("phone")
    val phoneRequiredError = page.getByTestId("phone-error")

    val trailerRegistrationCodeField = page.getByTestId("trailerRegistrationCode")
    val trailerRegistrationCodeRequiredError = page.getByTestId("trailerRegistrationCode-error")

    val trailerLengthInMetersField = page.getByTestId("trailerLengthInMeters")
    val trailerLengthInMetersRequiredError = page.getByTestId("trailerLengthInMeters-error")

    val trailerWidthInMetersField = page.getByTestId("trailerWidthInMeters")
    val trailerWidthInMetersRequiredError = page.getByTestId("trailerWidthInMeters-error")

    val boatTypeSelect = page.getByTestId("boatType")
    val boatTypeRequiredError = page.getByTestId("boatType-error")

    val boatNameField = page.getByTestId("boatName")
    val boatNameRequiredError = page.getByTestId("boatName-error")

    val registrationCodeField = page.getByTestId("boatRegistrationCode")
    val registrationCodeRequiredError = page.getByTestId("boatRegistrationCode-error")

    val lengthField = page.getByTestId("boatLengthInMeters")
    val lengthRequiredError = page.getByTestId("boatLengthInMeters-error")

    val widthField = page.getByTestId("boatWidthInMeters")
    val widthRequiredError = page.getByTestId("boatWidthInMeters-error")

    val weightField = page.getByTestId("weight")
    val weightRequiredError = page.getByTestId("weight-error")

    val boatSpaceTypeSelect = page.getByTestId("boatSpaceType")
    val boatSpaceTypeRequiredError = page.getByTestId("boatSpaceType-error")

    val locationSelect = { index: Int -> page.getByTestId("locationId$index") }
    val locationRequiredError = { index: Int -> page.getByTestId("locationId$index-error") }
    val addLocationWishButton = page.getByTestId("addLocationWish")

    val submitButton = page.getByTestId("submit")
}
