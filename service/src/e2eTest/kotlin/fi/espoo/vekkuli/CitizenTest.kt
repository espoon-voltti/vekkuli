package fi.espoo.vekkuli

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.pages.CitizenDetailsPage
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class CitizenTest : PlaywrightTest() {
    @Test
    fun `citizen can edit their own information`() {
        try {
            page.navigate(baseUrl)
            page.getByTestId("loginButton").click()
            page.getByText("Kirjaudu").click()

            page.navigate(baseUrl + "/kuntalainen/omat-tiedot")

            val citizenDetails = CitizenDetailsPage(page)
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            citizenDetails.editButton.click()

            assertThat(page.getByTestId("edit-citizen-form")).isVisible()
            val citizenFirstName = "New First Name"
            val citizenLastName = "New Last Name"
            val citizenPhone = "0405839281"
            val citizenEmail = "test2@email.com"
            val citizenAddress = "New Address"
            val citizenNationalId = "031195-950Y"
            val citizenPostalCode = "12345"
            val citizenMunicipalityCode = "49"

            citizenDetails.citizenFirstNameInput.fill(citizenFirstName)
            citizenDetails.citizenLastNameInput.fill(citizenLastName)
            citizenDetails.citizenAddressInput.fill(citizenAddress)
            citizenDetails.citizenEmailInput.fill("")
            citizenDetails.citizenPhoneInput.fill("")
            citizenDetails.citizenNationalIdInput.fill(citizenNationalId)
            citizenDetails.citizenPostalCodeInput.fill(citizenPostalCode)
            citizenDetails.citizenMunicipalityInput.selectOption(citizenMunicipalityCode)
            citizenDetails.citizenEditSubmitButton.click()

            // assert that email and phone can not be empty
            assertThat(citizenDetails.citizenEmailError).isVisible()
            assertThat(citizenDetails.citizenPhoneError).isVisible()
            citizenDetails.citizenEmailInput.fill("asd")
            citizenDetails.citizenPhoneInput.fill("asd")
            citizenDetails.citizenEditSubmitButton.click()

            // assert that email and phone have to be valid
            assertThat(citizenDetails.citizenEmailPatternError).isVisible()
            assertThat(citizenDetails.citizenPhonePatternError).isVisible()
            citizenDetails.citizenEmailInput.fill(citizenEmail)
            citizenDetails.citizenPhoneInput.fill(citizenPhone)
            citizenDetails.citizenEditSubmitButton.click()

            // assert that the values are updated
            assertThat(citizenDetails.citizenFirstNameField).hasText(citizenFirstName)
            assertThat(citizenDetails.citizenLastNameField).hasText(citizenLastName)
            assertThat(citizenDetails.citizenPhoneField).hasText(citizenPhone)
            assertThat(citizenDetails.citizenEmailField).hasText(citizenEmail)
            assertThat(citizenDetails.citizenAddressField).hasText(citizenAddress)
            assertThat(citizenDetails.citizenNationalIdField).hasText(citizenNationalId)
            assertThat(citizenDetails.citizenPostalCodeField).hasText(citizenPostalCode)
            assertThat(citizenDetails.citizenMunicipalityField).hasText("Espoo")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `citizen can edit their own boat`() {
        try {
            page.navigate(baseUrl)
            page.getByTestId("loginButton").click()
            page.getByText("Kirjaudu").click()

            page.navigate(baseUrl + "/kuntalainen/omat-tiedot")

            val citizenDetails = CitizenDetailsPage(page)
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            citizenDetails.showAllBoatsButton.click()
            page.getByTestId("edit-boat-3").click()
            assertThat(page.getByTestId("form")).isVisible()

            citizenDetails.nameInput.fill("New Boat Name")
            citizenDetails.weightInput.fill("2000")
            citizenDetails.typeSelect.selectOption("Sailboat")
            citizenDetails.depthInput.fill("1.5")
            citizenDetails.widthInput.fill("3")
            citizenDetails.registrationNumberInput.fill("ABC123")
            citizenDetails.length.fill("6")
            citizenDetails.ownership.selectOption("Owner")
            citizenDetails.otherIdentifier.fill("ID12345")
            citizenDetails.extraInformation.fill("Extra info")

            citizenDetails.submitButton.click()
            assertThat(citizenDetails.nameText(3)).hasText("New Boat Name")
            assertThat(citizenDetails.weightText(3)).hasText("2000")
            assertThat(citizenDetails.typeText(3)).hasText("Sailboat")
            assertThat(citizenDetails.depthText(3)).hasText("1.5")
            assertThat(citizenDetails.widthText(3)).hasText("3.0")
            assertThat(citizenDetails.registrationNumberText(3)).hasText("ABC123")

            assertThat(citizenDetails.lengthText(3)).hasText("6.0")
            assertThat(citizenDetails.ownershipText(3)).hasText("I own the boat")
            assertThat(citizenDetails.otherIdentifierText(3)).hasText("ID12345")
            assertThat(citizenDetails.extraInformationText(3)).hasText("Extra info")

            // delete the boat
            page.getByTestId("delete-boat-3").click()
            page.getByTestId("delete-modal-confirm-3").click()
            assertThat(page.getByTestId("boat-3")).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
