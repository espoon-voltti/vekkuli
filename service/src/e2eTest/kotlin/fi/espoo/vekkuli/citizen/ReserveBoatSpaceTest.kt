package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.baseUrlWithEnglishLangParam
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.pages.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
class ReserveBoatSpaceTest : PlaywrightTest() {
    @Test
    fun `employee can change the language`() {
        page.navigate("$baseUrl?lang=fi")
        assertThat(page.getByText("Venepaikat").first()).isVisible()
        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        page.getByTestId("language-selection").click()
        page.getByText("Englanti").click()
        assertThat(page.getByText("Boat spaces").first()).isVisible()
        page.getByTestId("language-selection").click()
        page.getByText("Swedish").click()
        assertThat(page.getByText("Båtplats").first()).isVisible()
    }

    @Test
    fun reservingShouldFailOutsidePeriod() {
        try {
            mockTimeProvider(timeProvider, LocalDateTime.of(2024, 1, 1, 22, 22, 22))
            page.navigate(baseUrlWithEnglishLangParam)
            page.getByTestId("loginButton").click()
            page.getByText("Kirjaudu").click()

            val reservationPage = ReserveBoatSpacePage(page, UserType.CITIZEN)
            reservationPage.navigateTo()
            assertThat(reservationPage.emptyDimensionsWarning).isVisible()
            reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
            reservationPage.widthFilterInput.fill("3")
            reservationPage.lengthFilterInput.fill("6")
            reservationPage.lengthFilterInput.blur()
            reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Slip).click()
            reservationPage.amenityBuoyCheckbox.check()
            reservationPage.amenityRearBuoyCheckbox.check()
            reservationPage.amenityBeamCheckbox.check()
            reservationPage.amenityWalkBeamCheckbox.check()

            assertThat(reservationPage.harborHeaders).hasCount(3)
            reservationPage.haukilahtiCheckbox.check()
            reservationPage.kivenlahtiCheckbox.check()
            assertThat(reservationPage.harborHeaders).hasCount(2)

            reservationPage.firstReserveButton.click()
            assertThat(page.locator("body")).containsText("Reservation not possible")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving a boat space slip as a citizen`() {
        try {
            page.navigate(baseUrlWithEnglishLangParam)
            page.getByTestId("loginButton").click()
            page.getByText("Kirjaudu").click()

            val reservationPage = ReserveBoatSpacePage(page, UserType.CITIZEN)
            reservationPage.navigateTo()
            assertThat(reservationPage.emptyDimensionsWarning).isVisible()
            reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
            reservationPage.widthFilterInput.fill("3")
            reservationPage.lengthFilterInput.fill("6")
            reservationPage.lengthFilterInput.blur()
            reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Slip).click()
            reservationPage.amenityBuoyCheckbox.check()
            reservationPage.amenityRearBuoyCheckbox.check()
            reservationPage.amenityBeamCheckbox.check()
            reservationPage.amenityWalkBeamCheckbox.check()

            assertThat(reservationPage.harborHeaders).hasCount(3)
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

            assertThat(formPage.validationWarning).isVisible()

            // Fill in the boat information
            formPage.boatTypeSelect.selectOption("Sailboat")

            formPage.widthInput.clear()
            formPage.widthInput.blur()
            assertThat(formPage.widthError).isVisible()

            formPage.lengthInput.clear()
            formPage.lengthInput.blur()
            assertThat(formPage.lengthError).isVisible()

            formPage.widthInput.fill("-1")
            formPage.widthInput.blur()
            assertThat(formPage.widthInput).hasValue("1")
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

            assertThat(formPage.validationWarning).isHidden()
            formPage.submitButton.click()

            // assert that payment title is shown
            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.paymentPageTitle).hasCount(1)
            // Cancel the payment at first
            paymentPage.nordeaFailedButton.click()
            // Then go through the payment
            paymentPage.nordeaSuccessButton.click()
            // Now we should be on the confirmation page and can go back to the home page
            paymentPage.backToHomePageButton.click()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving a winter storage boat space as a citizen`() {
        try {
            page.navigate(baseUrlWithEnglishLangParam)
            page.getByTestId("loginButton").click()
            page.getByText("Kirjaudu").click()

            val reservationPage = ReserveBoatSpacePage(page, UserType.CITIZEN)
            reservationPage.navigateTo()
            assertThat(reservationPage.emptyDimensionsWarning).isVisible()
            reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Winter).click()
            reservationPage.widthFilterInput.fill("1")
            reservationPage.lengthFilterInput.fill("3")
            reservationPage.lengthFilterInput.blur()

            reservationPage.firstReserveButton.click()

            // click send to trigger validation
            val formPage = BoatSpaceFormPage(page)
            formPage.submitButton.click()
            assertThat(formPage.widthError).isVisible()
            assertThat(formPage.lengthError).isVisible()
            assertThat(formPage.depthError).isVisible()
            assertThat(formPage.weightError).isVisible()
            assertThat(formPage.boatRegistrationNumberError).isVisible()
            assertThat(formPage.certifyInfoError).isVisible()
            assertThat(formPage.agreementError).isVisible()

            assertThat(formPage.validationWarning).isVisible()

            // Fill in the boat information
            formPage.boatTypeSelect.selectOption("Sailboat")

            formPage.widthInput.clear()
            formPage.widthInput.blur()
            assertThat(formPage.widthError).isVisible()

            formPage.lengthInput.clear()
            formPage.lengthInput.blur()
            assertThat(formPage.lengthError).isVisible()
            // warning for boat size
            formPage.widthInput.fill("3")
            formPage.widthInput.blur()

            formPage.lengthInput.fill("5")
            formPage.lengthInput.blur()

            formPage.lengthInput.fill("60")
            formPage.lengthInput.blur()

            formPage.depthInput.fill("1.5")
            formPage.depthInput.blur()
            assertThat(formPage.depthError).isHidden()

            formPage.weightInput.fill("2000")
            formPage.weightInput.blur()
            assertThat(formPage.weightError).isHidden()

            formPage.boatNameInput.fill("My Boat")
            formPage.otherIdentification.fill("Other identification")
            formPage.noRegistrationCheckbox.check()
            assertThat(formPage.boatRegistrationNumberError).isHidden()

            formPage.ownerRadioButton.check()

            formPage.emailInput.fill("test@example.com")
            formPage.emailInput.blur()
            assertThat(formPage.emailError).isHidden()

            formPage.phoneInput.fill("123456789")
            formPage.phoneInput.blur()
            assertThat(formPage.phoneError).isHidden()

            assertThat(formPage.storageTypeSelector).isVisible()

            assertThat(formPage.trailerRegistrationNumberError).isVisible()
            assertThat(formPage.storageTypeTextBuck).isHidden()
            formPage.storageTypeBuckOption.click()
            assertThat(formPage.storageTypeTextBuck).isVisible()
            assertThat(formPage.trailerInformationInputs).isHidden()

            formPage.storageTypeTrailerOption.click()
            assertThat(formPage.trailerInformationInputs).isVisible()

            val trailerRegistrationCode = "ID12345"
            val trailerWidth = "1.5"
            val trailerLength = "1.5"

            formPage.trailerRegistrationNumberInput.fill(trailerRegistrationCode)
            formPage.trailerWidthInput.fill(trailerWidth)
            formPage.trailerLengthInput.fill(trailerLength)
            assertThat(formPage.storageTypeTextTrailer).isVisible()

            formPage.certifyInfoCheckbox.check()
            formPage.agreementCheckbox.check()

            assertThat(formPage.validationWarning).isHidden()
            formPage.submitButton.click()

            // assert that payment title is shown
            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.paymentPageTitle).hasCount(1)
            // Cancel the payment at first
            paymentPage.nordeaFailedButton.click()
            // Then go through the payment
            paymentPage.nordeaSuccessButton.click()
            // Now we should be on the confirmation page and can go back to the home page
            paymentPage.backToHomePageButton.click()

            val citizenDetailPage = CitizenDetailsPage(page)
            citizenDetailPage.navigateToPage()

            val id = 2
            assertThat(citizenDetailPage.trailerInformation(id)).isVisible()
            assertThat(citizenDetailPage.trailerWidth(id)).containsText(trailerWidth)
            assertThat(citizenDetailPage.trailerLength(id)).containsText(trailerLength)
            assertThat(citizenDetailPage.trailerRegistrationCode(id)).containsText(trailerRegistrationCode)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun reservingABoatSpaceAsOrganization() {
        try {
            page.navigate(baseUrlWithEnglishLangParam)
            page.getByTestId("loginButton").click()
            page.getByText("Kirjaudu").click()

            val reservationPage = ReserveBoatSpacePage(page, UserType.CITIZEN)
            reservationPage.navigateTo()
            assertThat(reservationPage.emptyDimensionsWarning).isVisible()
            reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
            reservationPage.widthFilterInput.fill("3")
            reservationPage.lengthFilterInput.fill("6")
            reservationPage.lengthFilterInput.blur()

            reservationPage.firstReserveButton.click()

            // click send to trigger validation
            val formPage = BoatSpaceFormPage(page)

            formPage.organizationRadioButton.click()
            formPage.orgNameInput.fill("My Organization")
            formPage.orgBusinessIdInput.fill("1234567-8")
            formPage.orgPhoneNumberInput.fill("123456789")
            formPage.orgEmailInput.fill("foo@bar.com")

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
        page.navigate(baseUrlWithEnglishLangParam)
        page.getByTestId("loginButton").click()
        page.getByText("Kirjaudu").click()

        val reservationPage = ReserveBoatSpacePage(page, UserType.CITIZEN)
        reservationPage.navigateTo()
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()
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
        val reservationPage = ReserveBoatSpacePage(page, UserType.CITIZEN)
        reservationPage.navigateTo()

        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()
        reservationPage.firstReserveButton.click()
        assertThat(reservationPage.authModal).isVisible()

        reservationPage.authModalCancel.click()
        assertThat(reservationPage.authModal).isHidden()
        reservationPage.firstReserveButton.click()
        reservationPage.authModalContinue.click()
        page.getByText("Kirjaudu").click()
        val formPage = BoatSpaceFormPage(page)
        assertThat(formPage.header).isVisible()

        assertThat(reservationPage.widthFilterInput).hasValue("3")
        assertThat(reservationPage.lengthFilterInput).hasValue("6")

        formPage.fillFormAndSubmit()
    }

    @Test
    fun formValuesArePreservedAfterPaymentPageBackButton() {
        page.navigate(baseUrlWithEnglishLangParam)
        page.getByTestId("loginButton").click()
        page.getByText("Kirjaudu").click()

        val reservationPage = ReserveBoatSpacePage(page, UserType.CITIZEN)
        reservationPage.navigateTo()
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        assertThat(formPage.header).isVisible()
        formPage.fillFormAndSubmit()

        val paymentPage = PaymentPage(page)
        assertThat(paymentPage.paymentPageTitle).hasCount(1)
        paymentPage.backButtonOnPaymentPage.click()

        // assert that form is filled with the previous values
        assertThat(formPage.header).isVisible()
        assertThat(formPage.widthInput).hasValue("3.00")
        assertThat(formPage.lengthInput).hasValue("6.00")
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
        page.navigate(baseUrlWithEnglishLangParam)
        page.getByTestId("loginButton").click()
        page.getByText("Kirjaudu").click()

        val reservationPage = ReserveBoatSpacePage(page, UserType.CITIZEN)
        reservationPage.navigateTo()
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()
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
        page.navigate(baseUrlWithEnglishLangParam)
        page.getByTestId("loginButton").click()
        page.getByText("Kirjaudu").click()

        val reservationPage = ReserveBoatSpacePage(page, UserType.CITIZEN)
        reservationPage.navigateTo()
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()
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
    fun `show error page when reserving space off season`() {
        // login and pick first free space
        mockTimeProvider(timeProvider, LocalDateTime.of(2024, 1, 1, 0, 0, 0))
        page.navigate(baseUrlWithEnglishLangParam)
        page.getByTestId("loginButton").click()
        page.getByText("Kirjaudu").click()

        val reservationPage = ReserveBoatSpacePage(page, UserType.CITIZEN)
        reservationPage.navigateTo()
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()
        reservationPage.firstReserveButton.click()
        val errorPage = ErrorPage(page)
        assertThat(errorPage.errorPageContainer).isVisible()
    }
}
