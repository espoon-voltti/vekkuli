package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.employeePageInEnglish
import fi.espoo.vekkuli.pages.CitizenDetailsPage
import fi.espoo.vekkuli.pages.InvoicePreviewPage
import fi.espoo.vekkuli.pages.ReservationListPage
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
class CitizenDetailsAsEmployeeTest : PlaywrightTest() {
    @Test
    fun listingReservations() {
        try {
            page.navigate(employeePageInEnglish)
            page.getByTestId("employeeLoginButton").click()
            page.getByText("Kirjaudu").click()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()
            assertThat(listingPage.boatSpace1).isVisible()
            assertThat(listingPage.boatSpace2).isVisible()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun editCitizen() {
        try {
            page.navigate(employeePageInEnglish)
            page.getByTestId("employeeLoginButton").click()
            page.getByText("Kirjaudu").click()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()
            listingPage.boatSpace1.click()
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
            assertThat(citizenDetails.citizenEditSubmitButton).isDisabled()

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
    fun userMemos() {
        try {
            page.navigate(employeePageInEnglish)
            page.getByTestId("employeeLoginButton").click()
            page.getByText("Kirjaudu").click()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.memoNavi.click()

            // Add memo, added the waitFor which seemed to fix the flakiness
            citizenDetails.addNewMemoBtn.waitFor()
            citizenDetails.addNewMemoBtn.click()
            val text = "This is a new memo"
            val memoId = 2
            citizenDetails.newMemoContent.fill(text)
            citizenDetails.newMemoSaveBtn.click()
            assertThat(citizenDetails.newMemoSaveBtn).isDisabled()
            assertThat(citizenDetails.userMemo(memoId)).containsText(text)

            // Edit memo
            val newText = "Edited memo"
            citizenDetails.userMemo(memoId).getByTestId("edit-memo-button").click()
            citizenDetails.userMemo(memoId).getByTestId("edit-memo-content").fill(newText)
            citizenDetails.userMemo(memoId).getByTestId("save-edit-button").click()
            assertThat(citizenDetails.userMemo(memoId).getByTestId("save-edit-button")).isDisabled()

            assertThat(citizenDetails.userMemo(memoId).locator(".memo-content")).containsText(newText)

            // Delete memo
            page.onDialog { it.accept() }
            citizenDetails.userMemo(memoId).getByTestId("delete-memo-button").click()
            assertThat(citizenDetails.userMemo(memoId)).hasCount(0)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun userMessages() {
        try {
            page.navigate(employeePageInEnglish)
            page.getByTestId("employeeLoginButton").click()
            page.getByText("Kirjaudu").click()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.messagesNavi.click()
            assertThat(citizenDetails.messages).containsText("Käyttöveden katko")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun editBoat() {
        try {
            page.navigate(employeePageInEnglish)
            page.getByTestId("employeeLoginButton").click()
            page.getByText("Kirjaudu").click()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()
            listingPage.boatSpace1.click()
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
            assertThat(citizenDetails.ownershipText(3)).hasText("Owner")
            assertThat(citizenDetails.otherIdentifierText(3)).hasText("ID12345")
            assertThat(citizenDetails.extraInformationText(3)).hasText("Extra info")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun deleteBoat() {
        try {
            page.navigate(employeePageInEnglish)
            page.getByTestId("employeeLoginButton").click()
            page.getByText("Kirjaudu").click()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            citizenDetails.showAllBoatsButton.click()
            assertThat(page.getByTestId("boat-3")).isVisible()
            page.getByTestId("delete-boat-3").click()
            page.getByTestId("delete-modal-confirm-3").click()
            assertThat(page.getByTestId("boat-3")).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `employee can renew a boat space reservation`() {
        try {
            mockTimeProvider(timeProvider, LocalDateTime.of(2025, 1, 7, 0, 0, 0))
            page.navigate(employeePageInEnglish)
            page.getByTestId("employeeLoginButton").click()
            page.getByText("Kirjaudu").click()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()

            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            assertThat(citizenDetails.citizenDetailsSection).isVisible()

            citizenDetails.renewReservationButton(1).click()
            val invoiceDetails = InvoicePreviewPage(page)
            assertThat(invoiceDetails.header).isVisible()
            invoiceDetails.cancelButton.click()
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            citizenDetails.renewReservationButton(1).click()
            invoiceDetails.sendButton.click()
            assertThat(citizenDetails.invoicePaidButton).isVisible()
            assertThat(citizenDetails.renewReservationButton(1)).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
