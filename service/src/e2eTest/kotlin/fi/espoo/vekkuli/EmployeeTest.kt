package fi.espoo.vekkuli

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.pages.BoatSpaceFormPage
import fi.espoo.vekkuli.pages.ReservationListPage
import fi.espoo.vekkuli.pages.ReserveBoatSpacePage
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class EmployeeTest : PlaywrightTest() {
    @Test
    fun `Employee can reserve a boat space on behalf of a citizen`() {
        page.navigate(baseUrl + "/virkailija")
        page.getByTestId("employeeLoginButton").click()
        page.getByText("Kirjaudu").click()

        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()

        listingPage.createReservation.click()

        val reservationPage = ReserveBoatSpacePage(page)

        assertThat(reservationPage.emptyDimensionsWarning).isVisible()
        reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lenghtFilterInput.fill("6")
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

        formPage.firstNameInput.fill("John")
        formPage.lastNameInput.fill("Doe")
        formPage.ssnInput.fill("123456-789A")
        formPage.addressInput.fill("Test street 1")
        formPage.postalCodeInput.fill("12345")

        formPage.emailInput.fill("test@example.com")
        formPage.emailInput.blur()
        assertThat(formPage.emailError).isHidden()
        formPage.phoneInput.fill("123456789")
        formPage.phoneInput.blur()
        assertThat(formPage.phoneError).isHidden()

        formPage.certifyInfoCheckbox.check()
        formPage.agreementCheckbox.check()
        formPage.submitButton.click()
    }

    @Test
    fun `existing citizens can be searched`() {
        page.navigate(baseUrl + "/virkailija")
        page.getByTestId("employeeLoginButton").click()
        page.getByText("Kirjaudu").click()
        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        listingPage.createReservation.click()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lenghtFilterInput.fill("6")
        reservationPage.lenghtFilterInput.blur()
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        formPage.existingCitizenSelector.click()
        assertThat(formPage.citizenSearchContainer).isVisible()
        formPage.citizenSearchInput.pressSequentially("virtane")
        assertThat(formPage.citizenSearchOption1).isVisible()
        assertThat(formPage.citizenSearchOption2).isVisible()
        formPage.citizenEmptyInput.click()
        assertThat(formPage.citizenSearchOption1).isHidden()
        formPage.citizenSearchInput.pressSequentially("virtane")
        formPage.citizenSearchOption1.click()
        assertThat(formPage.citizenSearchInput).hasValue("Mikko Virtanen")
        assertThat(formPage.citizenInformationContainer).isVisible()
    }

    @Test
    fun `Employee can reserve a boat space on behalf of an existing citizen`() {
        page.navigate(baseUrl + "/virkailija")
        page.getByTestId("employeeLoginButton").click()
        page.getByText("Kirjaudu").click()
        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        listingPage.createReservation.click()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lenghtFilterInput.fill("6")
        reservationPage.lenghtFilterInput.blur()
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        formPage.existingCitizenSelector.click()
        assertThat(formPage.citizenSearchContainer).isVisible()

        formPage.submitButton.click()
        // TODO: Add validation for existing citizen search
//        assertThat(formPage.citizenIdError).isVisible()

        formPage.citizenSearchInput.pressSequentially("virtane")
        formPage.citizenSearchOption1.click()
        // Fill in the boat information
        formPage.boatTypeSelect.selectOption("Sailboat")

        formPage.widthInput.fill("3")
        formPage.widthInput.blur()

        formPage.lengthInput.fill("5")
        formPage.lengthInput.blur()

        formPage.depthInput.fill("1.5")
        formPage.depthInput.blur()

        formPage.weightInput.fill("2000")
        formPage.weightInput.blur()

        formPage.boatNameInput.fill("My Boat")
        formPage.otherIdentification.fill("ID12345")
        formPage.noRegistrationCheckbox.check()

        formPage.ownerRadioButton.check()

        formPage.certifyInfoCheckbox.check()
        formPage.agreementCheckbox.check()
        formPage.submitButton.click()
    }
}
