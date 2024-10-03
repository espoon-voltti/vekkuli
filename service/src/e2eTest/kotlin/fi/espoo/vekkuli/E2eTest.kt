package fi.espoo.vekkuli

import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.pages.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import kotlin.io.path.Path

@ActiveProfiles("test")
class E2eTest : PlaywrightTest() {
    fun handleError(e: AssertionError) {
        page.screenshot(Page.ScreenshotOptions().setPath(Path("build/failure-screenshot.png")))
        throw e
    }

    @Test
    fun listingReservations() {
        try {
            page.navigate(baseUrl + "/virkailija")
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
            page.navigate(baseUrl + "/virkailija")
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
            page.navigate(baseUrl + "/virkailija")
            page.getByTestId("employeeLoginButton").click()
            page.getByText("Kirjaudu").click()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.memoNavi.click()

            // Add memo
            citizenDetails.addNewMemoBtn.click()
            val text = "This is a new memo"
            val memoId = 2
            citizenDetails.newMemoContent.fill(text)
            citizenDetails.newMemoSaveBtn.click()
            assertThat(citizenDetails.userMemo(memoId)).containsText(text)

            // Edit memo
            val newText = "Edited memo"
            citizenDetails.userMemo(memoId).getByTestId("edit-memo-button").click()
            citizenDetails.userMemo(memoId).getByTestId("edit-memo-content").fill(newText)
            citizenDetails.userMemo(memoId).getByTestId("save-edit-button").click()
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
            page.navigate(baseUrl + "/virkailija")
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
            page.navigate(baseUrl + "/virkailija")
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
            page.navigate(baseUrl + "/virkailija")
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
    fun reservingABoatSpace() {
        try {
            page.navigate(baseUrl)
            page.getByTestId("loginButton").click()
            page.getByText("Kirjaudu").click()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateTo()
            assertThat(reservationPage.emptyDimensionsWarning).isVisible()
            reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
            reservationPage.widthFilterInput.fill("3")
            reservationPage.lenghtFilterInput.fill("6")
            reservationPage.lenghtFilterInput.blur()
            reservationPage.boatSpaceTypeSlipRadio.click()
            reservationPage.amenityBuoyCheckbox.check()
            reservationPage.amenityRearBuoyCheckbox.check()
            reservationPage.amenityBeamCheckbox.check()
            reservationPage.amenityWalkBeamCheckbox.check()

            assertThat(reservationPage.harborHeaders).hasCount(4)
            reservationPage.haukilahtiCheckbox.check()
            reservationPage.kivenlahtiCheckbox.check()
            assertThat(reservationPage.harborHeaders).hasCount(2)

            reservationPage.firstReserveButton.click()

            // click send to trigger validation
            val formPage = BoatSpaceFormPage(page)
            formPage.submitButton.click()

            assertThat(formPage.widthError).isHidden()
            assertThat(formPage.lengthError).isHidden()
            assertThat(formPage.depthError).isVisible()
            assertThat(formPage.weightError).isVisible()
            assertThat(formPage.boatRegistrationNumberError).isVisible()
            assertThat(formPage.certifyInfoError).isVisible()
            assertThat(formPage.agreementError).isVisible()

            // Fill in the boat information
            formPage.boatTypeSelect.selectOption("Sailboat")

            formPage.widthInput.clear()
            formPage.widthInput.blur()
            assertThat(formPage.widthError).isVisible()

            formPage.lengthInput.clear()
            formPage.lengthInput.blur()
            assertThat(formPage.lengthError).isVisible()

            // warning for boat size
            formPage.widthInput.fill("10")
            formPage.widthInput.blur()
            assertThat(formPage.boatSizeWarning).isVisible()

            formPage.widthInput.fill("3")
            formPage.widthInput.blur()
            assertThat(formPage.boatSizeWarning).isHidden()

            formPage.lengthInput.fill("20")
            formPage.lengthInput.blur()
            assertThat(formPage.boatSizeWarning).isVisible()

            formPage.lengthInput.fill("5")
            formPage.lengthInput.blur()
            assertThat(formPage.boatSizeWarning).isHidden()

            formPage.lengthInput.fill("25")
            formPage.lengthInput.blur()
            assertThat(formPage.boatSizeWarning).isVisible()
            formPage.lengthInput.fill("60")
            formPage.lengthInput.blur()

            formPage.depthInput.fill("1.5")
            formPage.depthInput.blur()
            assertThat(formPage.depthError).isHidden()

            formPage.weightInput.fill("2000")
            formPage.weightInput.blur()
            assertThat(formPage.weightError).isHidden()

            formPage.boatNameInput.fill("My Boat")
            formPage.otherIdentification.fill("ID12345")
            formPage.noRegistrationCheckbox.check()
            assertThat(formPage.boatRegistrationNumberError).isHidden()

            formPage.ownerRadioButton.check()

            formPage.emailInput.fill("test@example.com")
            formPage.emailInput.blur()
            assertThat(formPage.emailError).isHidden()

            formPage.phoneInput.fill("123456789")
            formPage.phoneInput.blur()
            assertThat(formPage.phoneError).isHidden()

            formPage.certifyInfoCheckbox.check()
            formPage.agreementCheckbox.check()
            formPage.submitButton.click()

            // assert that payment title is shown
            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.paymentPageTitle).hasCount(1)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun cancelReservationFromForm() {
        // login and pick first free space
        page.navigate(baseUrl)
        page.getByTestId("loginButton").click()
        page.getByText("Kirjaudu").click()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateTo()
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lenghtFilterInput.fill("6")
        reservationPage.lenghtFilterInput.blur()
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        assertThat(formPage.header).isVisible()
        // Cancel, then cancel in modal
        formPage.cancelButton.click()
        assertThat(formPage.confirmCancelModal).isVisible()
        formPage.confirmCancelModalCancel.click()
        assertThat(formPage.confirmCancelModal).isHidden()

        // Cancel, then confirm in modal
        formPage.cancelButton.click()
        assertThat(formPage.confirmCancelModal).isVisible()
        formPage.confirmCancelModalConfirm.click()
        assertThat(reservationPage.header).isVisible()
    }

    @Test
    fun authenticationOnReservation() {
        // go directly to reservation page
        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateTo()

        reservationPage.widthFilterInput.fill("3")
        reservationPage.lenghtFilterInput.fill("6")
        reservationPage.lenghtFilterInput.blur()
        reservationPage.firstReserveButton.click()
        assertThat(reservationPage.authModal).isVisible()

        reservationPage.authModalCancel.click()
        assertThat(reservationPage.authModal).isHidden()
        reservationPage.firstReserveButton.click()
        reservationPage.authModalContinue.click()
        page.getByText("Kirjaudu").click()
        val formPage = BoatSpaceFormPage(page)
        assertThat(formPage.header).isVisible()
        formPage.fillFormAndSubmit()
    }

    @Test
    fun formValuesArePreservedAfterPaymentPageBackButton() {
        page.navigate(baseUrl)
        page.getByTestId("loginButton").click()
        page.getByText("Kirjaudu").click()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateTo()
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lenghtFilterInput.fill("6")
        reservationPage.lenghtFilterInput.blur()
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        assertThat(formPage.header).isVisible()
        formPage.fillFormAndSubmit()

        val paymentPage = PaymentPage(page)
        assertThat(paymentPage.paymentPageTitle).hasCount(1)
        paymentPage.backButtonOnPaymentPage.click()

        // assert that form is filled with the previous values
        assertThat(formPage.header).isVisible()
        assertThat(formPage.widthInput).hasValue("3.0")
        assertThat(formPage.lengthInput).hasValue("6.0")
        assertThat(formPage.depthInput).hasValue("1.5")
        assertThat(formPage.weightInput).hasValue("2000")
        assertThat(formPage.boatNameInput).hasValue("My Boat")
        assertThat(formPage.otherIdentification).hasValue("ID12345")
        assertThat(formPage.emailInput).hasValue("test@example.com")
        assertThat(formPage.phoneInput).hasValue("123456789")
    }

    @Test
    fun paymentSuccess() {
        // login and pick first free space
        page.navigate(baseUrl)
        page.getByTestId("loginButton").click()
        page.getByText("Kirjaudu").click()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateTo()
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lenghtFilterInput.fill("6")
        reservationPage.lenghtFilterInput.blur()
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        formPage.fillFormAndSubmit()
        val paymentPage = PaymentPage(page)
        assertThat(paymentPage.paymentPageTitle).isVisible()
        paymentPage.nordeaSuccessButton.click()
        assertThat(paymentPage.confirmationPageContainer).isVisible()
    }

    @Test
    fun paymentFailed() {
        // login and pick first free space
        page.navigate(baseUrl)
        page.getByTestId("loginButton").click()
        page.getByText("Kirjaudu").click()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateTo()
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lenghtFilterInput.fill("6")
        reservationPage.lenghtFilterInput.blur()
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        formPage.fillFormAndSubmit()

        val paymentPage = PaymentPage(page)
        assertThat(paymentPage.paymentPageTitle).isVisible()
        paymentPage.nordeaFailedButton.click()
        assertThat(paymentPage.paymentErrorMessage).isVisible()
        paymentPage.paymentErrorLink.click()
        assertThat(formPage.confirmCancelModal).isVisible()
        formPage.confirmCancelModalConfirm.click()
        assertThat(reservationPage.header).isVisible()
    }

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
