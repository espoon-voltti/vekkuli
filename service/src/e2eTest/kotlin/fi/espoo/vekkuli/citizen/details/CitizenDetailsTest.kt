package fi.espoo.vekkuli.citizen.details

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.citizenPageInEnglish
import fi.espoo.vekkuli.pages.citizen.CitizenDetailsPage
import fi.espoo.vekkuli.pages.citizen.CitizenHomePage
import fi.espoo.vekkuli.pages.employee.BoatSpaceFormPage
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.pages.employee.PaymentPage
import fi.espoo.vekkuli.pages.employee.ReservationListPage
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipRenewPeriod
import fi.espoo.vekkuli.utils.startOfWinterSpaceRenewPeriod
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import fi.espoo.vekkuli.pages.employee.CitizenDetailsPage as EmployeeCitizenDetailsPage

@ActiveProfiles("test")
class CitizenDetailsTest : PlaywrightTest() {
    @Test
    fun `citizen can edit trailer information and it should add warnings when trailer is too large`() {
        try {
            CitizenHomePage(page).loginAsOliviaVirtanen()

            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.navigateToPage()

            val firstReservationSection = citizenDetails.getReservationSection(1)
            val trailerSection = firstReservationSection.getTrailerSection()

            trailerSection.editButton.click()
            trailerSection.registrationCodeInput.fill("FOO-123")
            trailerSection.widthInput.fill("3")
            trailerSection.lengthInput.fill("5")
            trailerSection.saveButton.click()

            assertThat(trailerSection.registrationCodeField).hasText("FOO-123")
            assertThat(trailerSection.widthField).hasText("3,00")
            assertThat(trailerSection.lengthField).hasText("5,00")

            val employeeHomePage = EmployeeHomePage(page)
            employeeHomePage.employeeLogin()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()

            assertThat(listingPage.warningIcon8).isVisible()

            listingPage.boatSpace8.click()

            val employeeCitizenDetails = EmployeeCitizenDetailsPage(page)

            employeeCitizenDetails.trailerAckWarningButton(1).click()

            assertThat(employeeCitizenDetails.trailerWarningModalLengthInput).isVisible()
            assertThat(employeeCitizenDetails.trailerWarningModalWidthInput).isVisible()
            employeeCitizenDetails.trailerWarningModalLengthInput.click()

            val infoText = "Length and width ok"
            employeeCitizenDetails.boatWarningModalInfoInput.fill(infoText)
            employeeCitizenDetails.boatWarningModalConfirmButton.click()

            employeeCitizenDetails.memoNavi.click()
            assertThat(employeeCitizenDetails.userMemo(2)).containsText(infoText)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    @Disabled("Feature is not working")
    fun `citizen can renew slip reservation`() {
        try {
            mockTimeProvider(timeProvider, startOfSlipRenewPeriod)
            CitizenHomePage(page).loginAsLeoKorhonen()

            val citizenDetails = EmployeeCitizenDetailsPage(page)
            citizenDetails.navigateToPage()
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            citizenDetails.renewReservationButton(1).click()

            val formPage = BoatSpaceFormPage(page)
            assertThat(formPage.header).isVisible()
            formPage.backButton.click()
            formPage.confirmCancelModalConfirm.click()
            assertThat(citizenDetails.citizenDetailsSection).isVisible()

            citizenDetails.renewReservationButton(1).click()
            formPage.certifyInfoCheckbox.check()
            formPage.agreementCheckbox.check()
            formPage.submitButton.click()

            // assert that payment title is shown
            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.paymentPageTitle).hasCount(1)
            paymentPage.nordeaSuccessButton.click()

            page.navigate(citizenPageInEnglish)
            assertThat(citizenDetails.renewReservationButton(1)).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    @Disabled("Feature is not working")
    fun `citizen can renew winter storage reservation`() {
        try {
            mockTimeProvider(timeProvider, startOfWinterSpaceRenewPeriod)

            CitizenHomePage(page).loginAsOliviaVirtanen()

            val renewReservationId = 6
            val citizenDetails = EmployeeCitizenDetailsPage(page)
            citizenDetails.navigateToPage()
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            citizenDetails.renewReservationButton(renewReservationId).click()
            val formPage = BoatSpaceFormPage(page)

            assertThat(formPage.header).isVisible()
            formPage.backButton.click()

            formPage.confirmCancelModalConfirm.click()
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            citizenDetails.renewReservationButton(renewReservationId).click()
            assertThat(formPage.storageTypeTextBuck).isHidden()
            formPage.storageTypeBuckOption.click()
            assertThat(formPage.storageTypeTextBuck).isVisible()
            assertThat(formPage.trailerInformationInputs).isHidden()

            formPage.storageTypeTrailerOption.click()
            assertThat(formPage.trailerInformationInputs).isVisible()
            assertThat(formPage.trailerWidthInput).hasValue("2.00")
            assertThat(formPage.trailerLengthInput).hasValue("3.00")
            assertThat(formPage.trailerRegistrationNumberInput).hasValue("ABC123")
            formPage.certifyInfoCheckbox.check()
            formPage.agreementCheckbox.check()
            formPage.submitButton.click()
            // assert that payment title is shown
            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.paymentPageTitle).hasCount(1)
            paymentPage.nordeaSuccessButton.click()

            page.navigate(citizenPageInEnglish)
            assertThat(citizenDetails.renewReservationButton(renewReservationId)).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    @Disabled("Feature is not working")
    fun `citizen cannot renew reservation if it is not time to renew`() {
        try {
            // Set time over month before the reservation ends. Renewal should not be possible.
            mockTimeProvider(timeProvider, LocalDateTime.of(2024, 12, 30, 12, 0, 0))

            CitizenHomePage(page).loginAsLeoKorhonen()

            val citizenDetails = EmployeeCitizenDetailsPage(page)
            citizenDetails.navigateToPage()

            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            assertThat(citizenDetails.renewReservationButton(1)).not().isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `citizen can edit their own information`() {
        try {
            CitizenHomePage(page).loginAsLeoKorhonen()

            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.navigateToPage()

            val citizenSection = citizenDetails.getCitizenSection()
            citizenSection.editButton.click()

            val citizenPhone = "0405839281"
            val citizenEmail = "test2@email.com"

            citizenSection.emailInput.fill("")
            citizenSection.phoneInput.fill("")
            citizenSection.saveButton.click()

            // assert that email and phone can not be empty
            assertThat(citizenSection.emailError).isVisible()
            assertThat(citizenSection.phoneError).isVisible()
            citizenSection.emailInput.fill("asd")
            citizenSection.phoneInput.fill("asd")
            citizenSection.saveButton.click()

            // assert that email and phone have to be valid
            assertThat(citizenSection.emailError).isVisible()
            assertThat(citizenSection.emailError).hasText("Virheellinen sähköpostiosoite")
            assertThat(citizenSection.phoneError).isVisible()
            assertThat(citizenSection.phoneError).hasText("Virheellinen numero")
            citizenSection.emailInput.fill(citizenEmail)
            citizenSection.phoneInput.fill(citizenPhone)
            citizenSection.saveButton.click()

            // assert that the values are updated
            assertThat(citizenSection.phoneField).hasText(citizenPhone)
            assertThat(citizenSection.emailField).hasText(citizenEmail)
            assertThat(citizenSection.municipalityField).hasText("Espoo")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `citizen can edit their own boat`() {
        try {
            CitizenHomePage(page).loginAsLeoKorhonen()

            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.navigateToPage()

            citizenDetails.showAllBoatsButton.click()

            val boat = citizenDetails.getBoatSection(3)
            boat.editButton.click()

            boat.nameInput.fill("New Boat Name")
            boat.weightInput.fill("2000")
            boat.typeSelect.selectOption("Sailboat")
            boat.depthInput.fill("1.5")
            boat.widthInput.fill("3")
            boat.registrationNumberInput.fill("ABC123")
            boat.lengthInput.fill("6")
            boat.ownershipSelect.selectOption("Owner")
            boat.otherIdentifierInput.fill("ID12345")
            boat.extraInformationInput.fill("Extra info")

            boat.saveButton.click()

            assertThat(boat.nameField).hasText("New Boat Name")
            assertThat(boat.weightField).hasText("2000")
            assertThat(boat.typeField).hasText("Purjevene")
            assertThat(boat.depthField).hasText("1,50")
            assertThat(boat.widthField).hasText("3,00")
            assertThat(boat.registrationNumberField).hasText("ABC123")
            assertThat(boat.lengthField).hasText("6,00")
            assertThat(boat.ownershipField).hasText("Omistan veneen")
            assertThat(boat.otherIdentifierField).hasText("ID12345")
            assertThat(boat.extraInformationField).hasText("Extra info")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    @Disabled("Feature is not working")
    fun `citizen can delete their boat when it's without active reservation`() {
        // TODO delete the boat
        // page.getByTestId("delete-boat-3").click()
        // page.getByTestId("delete-modal-confirm-3").click()
        // assertThat(page.getByTestId("boat-3")).isHidden()
    }

    @Test
    fun `should add warning when citizen edits boat to be too heavy`() {
        try {
            CitizenHomePage(page).loginAsLeoKorhonen()

            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.navigateToPage()

            val boat = citizenDetails.getBoatSection(1)
            boat.editButton.click()
            boat.weightInput.fill("16000")
            boat.saveButton.click()

            val employeeHomePage = EmployeeHomePage(page)
            employeeHomePage.employeeLogin()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()
            assertThat(listingPage.warningIcon).isVisible()

            listingPage.boatSpace1.click()

            val employeeCitizenDetails = EmployeeCitizenDetailsPage(page)
            employeeCitizenDetails.acknowledgeWarningButton(1).click()
            assertThat(employeeCitizenDetails.boatWarningModalWeightInput).isVisible()
            employeeCitizenDetails.boatWarningModalWeightInput.click()
            val infoText = "Test info"
            employeeCitizenDetails.boatWarningModalInfoInput.fill(infoText)
            employeeCitizenDetails.boatWarningModalConfirmButton.click()

            employeeCitizenDetails.memoNavi.click()
            assertThat(employeeCitizenDetails.userMemo(2)).containsText(infoText)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
