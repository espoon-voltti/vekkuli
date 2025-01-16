package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.baseUrlWithEnglishLangParam
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.pages.citizen.BoatSpaceFormPage
import fi.espoo.vekkuli.pages.citizen.CitizenDetailsPage
import fi.espoo.vekkuli.pages.citizen.CitizenHomePage
import fi.espoo.vekkuli.pages.citizen.PaymentPage
import fi.espoo.vekkuli.pages.citizen.ReserveBoatSpacePage
import fi.espoo.vekkuli.pages.employee.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfWinterReservationPeriod
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import fi.espoo.vekkuli.pages.employee.BoatSpaceFormPage as EmployeeBoatSpaceFormPage
import fi.espoo.vekkuli.pages.employee.PaymentPage as EmployeePaymentPage

@ActiveProfiles("test")
class ReserveBoatSpaceTest : PlaywrightTest() {
    @Test
    fun `citizen can change the language`() {
        val citizenHomePage = CitizenHomePage(page)
        citizenHomePage.navigateToPage()

        assertThat(citizenHomePage.languageSelector).isVisible()
        assertThat(citizenHomePage.finnishTitle).isVisible()

        citizenHomePage.languageSelector.click()
        citizenHomePage.languageSelector.getByText("English").click()
        assertThat(citizenHomePage.englishTitle).isVisible()

        citizenHomePage.languageSelector.click()
        citizenHomePage.languageSelector.getByText("Svenska").click()
        assertThat(citizenHomePage.swedishTitle).isVisible()

        citizenHomePage.languageSelector.click()
        citizenHomePage.languageSelector.getByText("Suomi").click()
        assertThat(citizenHomePage.finnishTitle).isVisible()
    }

    @Test
    fun reservingShouldFailOutsidePeriod() {
        try {
            mockTimeProvider(timeProvider, LocalDateTime.of(2024, 1, 1, 22, 22, 22))

            CitizenHomePage(page).loginAsLeoKorhonen()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val filterSection = reservationPage.getFilterSection()
            filterSection.slipRadio.click()

            val slipFilterSection = filterSection.getSlipFilterSection()
            slipFilterSection.boatTypeSelect.selectOption("Sailboat")
            slipFilterSection.widthInput.fill("3")
            slipFilterSection.lengthInput.fill("6")
            slipFilterSection.amenityBuoyCheckbox.check()
            slipFilterSection.amenityRearBuoyCheckbox.check()
            slipFilterSection.amenityBeamCheckbox.check()
            slipFilterSection.amenityWalkBeamCheckbox.check()

            val searchResultsSection = reservationPage.getSearchResultsSection()
            assertThat(searchResultsSection.harborHeaders).hasCount(3)

            slipFilterSection.haukilahtiCheckbox.check()
            slipFilterSection.kivenlahtiCheckbox.check()
            assertThat(searchResultsSection.harborHeaders).hasCount(2)

            searchResultsSection.firstReserveButton.click()
            assertThat(page.locator("body")).containsText("Varaaminen ei ole mahdollista")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving a boat space slip as a citizen`() {
        try {
            CitizenHomePage(page).loginAsLeoKorhonen()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()
            reservationPage.startReservingBoatSpaceB314()

            // click send to trigger validation
            val formPage = BoatSpaceFormPage(page)
            formPage.submitButton.click()

            val boatSection = formPage.getBoatSection()
            assertThat(boatSection.widthError).not().isVisible()
            assertThat(boatSection.lengthError).not().isVisible()
            assertThat(boatSection.depthError).isVisible()
            assertThat(boatSection.weightError).isVisible()
            assertThat(boatSection.registrationNumberError).isVisible()

            val userAgreementSection = formPage.getUserAgreementSection()
            assertThat(userAgreementSection.certifyInfoError).isVisible()
            assertThat(userAgreementSection.agreementError).isVisible()

            assertThat(formPage.validationWarning).isVisible()

            // Fill in the boat information
            boatSection.typeSelect.selectOption("Sailboat")

            boatSection.widthInput.clear()
            assertThat(boatSection.widthError).isVisible()

            boatSection.lengthInput.clear()
            assertThat(boatSection.lengthError).isVisible()

            boatSection.widthInput.fill("-1")
            assertThat(boatSection.widthError).isVisible()
            assertThat(boatSection.widthError).hasText("Anna positiivinen luku")

            boatSection.nameInput.fill("My Boat")
            assertThat(boatSection.nameError).isHidden()

            boatSection.lengthInput.fill("3")
            assertThat(boatSection.lengthError).isHidden()

            boatSection.widthInput.fill("25")
            assertThat(boatSection.widthError).isHidden()

            boatSection.depthInput.fill("1.5")
            assertThat(boatSection.depthError).isHidden()

            boatSection.weightInput.fill("2000")
            assertThat(boatSection.weightError).isHidden()

            boatSection.otherIdentifierInput.fill("ID12345")
            assertThat(boatSection.otherIdentifierError).isHidden()

            boatSection.noRegistrationCheckbox.check()
            assertThat(boatSection.registrationNumberError).isHidden()

            boatSection.ownerRadio.click()

            val citizenSection = formPage.getCitizenSection()
            citizenSection.emailInput.fill("test@example.com")
            assertThat(citizenSection.emailError).isHidden()

            citizenSection.phoneInput.fill("123456789")
            assertThat(citizenSection.phoneError).isHidden()

            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()

            formPage.submitButton.click()

            // assert that payment title is shown
            val paymentPage = PaymentPage(page)
            // Cancel the payment at first
            paymentPage.nordeaFailedButton.click()
            // Then go through the payment
            paymentPage.nordeaSuccessButton.click()
            // Now we should be on the confirmation page
            assertThat(paymentPage.reservationSuccessNotification).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving a trailer space as a citizen`() {
        try {
            mockTimeProvider(timeProvider, LocalDateTime.of(2024, 5, 1, 22, 22, 22))

            CitizenHomePage(page).loginAsLeoKorhonen()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()
            reservationPage.startReservingBoatSpace012()

            val formPage = BoatSpaceFormPage(page)
            val boatSection = formPage.getBoatSection()
            val userAgreementSection = formPage.getUserAgreementSection()

            // Fill in the boat information
            boatSection.typeSelect.selectOption("Sailboat")
            boatSection.nameInput.fill("My Boat")
            boatSection.lengthInput.fill("3")
            boatSection.widthInput.fill("25")
            boatSection.depthInput.fill("1.5")
            boatSection.weightInput.fill("2000")
            boatSection.otherIdentifierInput.fill("ID12345")
            boatSection.noRegistrationCheckbox.check()
            boatSection.ownerRadio.click()

            val citizenSection = formPage.getCitizenSection()
            citizenSection.emailInput.fill("test@example.com")
            assertThat(citizenSection.emailError).isHidden()

            citizenSection.phoneInput.fill("123456789")
            assertThat(citizenSection.phoneError).isHidden()

            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()

            formPage.submitButton.click()

            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()

            assertThat(paymentPage.reservationSuccessNotification).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving a storage space as a citizen`() {
        try {
            mockTimeProvider(timeProvider, LocalDateTime.of(2024, 9, 1, 22, 22, 22))

            CitizenHomePage(page).loginAsLeoKorhonen()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()
            reservationPage.startReservingStorageSpaceB007()

            val formPage = BoatSpaceFormPage(page)
            val boatSection = formPage.getBoatSection()
            val userAgreementSection = formPage.getUserAgreementSection()

            // Fill in the boat information
            boatSection.typeSelect.selectOption("Sailboat")
            boatSection.nameInput.fill("My Boat")
            boatSection.lengthInput.fill("3")
            boatSection.widthInput.fill("25")
            boatSection.depthInput.fill("1.5")
            boatSection.weightInput.fill("2000")
            boatSection.otherIdentifierInput.fill("ID12345")
            boatSection.noRegistrationCheckbox.check()
            boatSection.ownerRadio.click()

            val citizenSection = formPage.getCitizenSection()
            citizenSection.emailInput.fill("test@example.com")
            assertThat(citizenSection.emailError).isHidden()

            citizenSection.phoneInput.fill("123456789")
            assertThat(citizenSection.phoneError).isHidden()

            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()

            formPage.submitButton.click()

            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()

            assertThat(paymentPage.reservationSuccessNotification).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving a winter storage boat space as a citizen`() {
        try {
            mockTimeProvider(timeProvider, startOfWinterReservationPeriod)

            CitizenHomePage(page).loginAsLeoKorhonen()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val filterSection = reservationPage.getFilterSection()
            filterSection.winterRadio.click()

            val winterFilterSection = filterSection.getWinterFilterSection()
            winterFilterSection.widthInput.fill("1")
            winterFilterSection.lengthInput.fill("3")

            val searchResultsSection = reservationPage.getSearchResultsSection()
            searchResultsSection.firstReserveButton.click()

            // click send to trigger validation
            val formPage = BoatSpaceFormPage(page)
            formPage.submitButton.click()

            val boatSection = formPage.getBoatSection()
            assertThat(boatSection.widthError).isVisible()
            assertThat(boatSection.lengthError).isVisible()
            assertThat(boatSection.depthError).isVisible()
            assertThat(boatSection.weightError).isVisible()
            assertThat(boatSection.registrationNumberError).isVisible()

            val userAgreementSection = formPage.getUserAgreementSection()
            assertThat(userAgreementSection.certifyInfoError).isVisible()
            assertThat(userAgreementSection.agreementError).isVisible()

            assertThat(formPage.validationWarning).isVisible()

            // Fill in the boat information
            boatSection.typeSelect.selectOption("Sailboat")

            boatSection.widthInput.clear()
            assertThat(boatSection.widthError).isVisible()

            boatSection.lengthInput.clear()
            assertThat(boatSection.lengthError).isVisible()

            boatSection.widthInput.fill("-1")
            assertThat(boatSection.widthError).isVisible()
            assertThat(boatSection.widthError).hasText("Anna positiivinen luku")

            boatSection.nameInput.fill("My Boat")
            assertThat(boatSection.nameError).isHidden()

            boatSection.lengthInput.fill("3")
            assertThat(boatSection.lengthError).isHidden()

            boatSection.widthInput.fill("25")
            assertThat(boatSection.widthError).isHidden()

            boatSection.depthInput.fill("1.5")
            assertThat(boatSection.depthError).isHidden()

            boatSection.weightInput.fill("2000")
            assertThat(boatSection.weightError).isHidden()

            boatSection.otherIdentifierInput.fill("ID12345")
            assertThat(boatSection.otherIdentifierError).isHidden()

            boatSection.noRegistrationCheckbox.check()
            assertThat(boatSection.registrationNumberError).isHidden()

            boatSection.ownerRadio.click()

            val citizenSection = formPage.getCitizenSection()
            citizenSection.emailInput.fill("test@example.com")
            assertThat(citizenSection.emailError).isHidden()

            citizenSection.phoneInput.fill("123456789")
            assertThat(citizenSection.phoneError).isHidden()

            val winterStorageTypeSection = formPage.getWinterStorageTypeSection()
            val reservedSpaceSection = formPage.getReservedSpaceSection()

            assertThat(winterStorageTypeSection.trailerRegistrationNumberError).isVisible()
            assertThat(reservedSpaceSection.storageTypeField).hasText("Trailerisäilytys")
            winterStorageTypeSection.buckStorageTypeRadio.click()
            assertThat(reservedSpaceSection.storageTypeField).hasText("Pukkisäilytys")
            assertThat(winterStorageTypeSection.trailerRegistrationNumberInput).isHidden()

            winterStorageTypeSection.trailerStorageTypeRadio.click()
            assertThat(winterStorageTypeSection.trailerRegistrationNumberInput).isVisible()

            val trailerRegistrationCode = "ID12345"

            winterStorageTypeSection.trailerRegistrationNumberInput.fill(trailerRegistrationCode)
            winterStorageTypeSection.trailerWidthInput.fill("1.5")
            winterStorageTypeSection.trailerLengthInput.fill("2.5")
            assertThat(reservedSpaceSection.storageTypeField).hasText("Trailerisäilytys")

            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            formPage.submitButton.click()

            // assert that payment title is shown
            val paymentPage = PaymentPage(page)
            // Cancel the payment at first
            paymentPage.nordeaFailedButton.click()
            // Then go through the payment
            paymentPage.nordeaSuccessButton.click()
            // Now we should be on the confirmation page
            assertThat(paymentPage.reservationSuccessNotification).isVisible()

            val citizenDetailPage = CitizenDetailsPage(page)
            citizenDetailPage.navigateToPage()

            val reservationSection = citizenDetailPage.getReservationSection(1)
            val trailerSection = reservationSection.getTrailerSection()
            assertThat(trailerSection.widthField).containsText("1,50")
            assertThat(trailerSection.lengthField).containsText("2,50")
            assertThat(trailerSection.registrationCodeField).containsText(trailerRegistrationCode)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun reservingABoatSpaceAsOrganization() {
        try {
            CitizenHomePage(page).loginAsOliviaVirtanen()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()
            reservationPage.startReservingBoatSpaceB314()

            val formPage = BoatSpaceFormPage(page)
            val organizationSection = formPage.getOrganizationSection()

            organizationSection.reserveForOrganization.click()
            organizationSection.organization("Espoon Pursiseura").click()
            organizationSection.phoneNumberInput.fill("123456789")
            organizationSection.emailInput.fill("foo@bar.com")

            val boatSection = formPage.getBoatSection()
            boatSection.depthInput.fill("1.5")
            boatSection.weightInput.fill("2000")
            boatSection.nameInput.fill("My Boat")
            boatSection.otherIdentifierInput.fill("ID12345")
            boatSection.noRegistrationCheckbox.check()
            boatSection.ownerRadio.check()

            val citizenSection = formPage.getCitizenSection()
            citizenSection.emailInput.fill("test@example.com")
            citizenSection.phoneInput.fill("123456789")

            val userAgreementSection = formPage.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()

            formPage.submitButton.click()

            // assert that payment page is shown
            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.paymentProviders).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun cancelReservationFromForm() {
        CitizenHomePage(page).loginAsOliviaVirtanen()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingBoatSpaceB314()

        val formPage = BoatSpaceFormPage(page)
        val confirmCancelReservationModal = formPage.getConfirmCancelReservationModal()

        // Cancel, then cancel in modal
        formPage.cancelButton.click()
        assertThat(confirmCancelReservationModal.root).isVisible()
        confirmCancelReservationModal.cancelButton.click()
        assertThat(confirmCancelReservationModal.root).isHidden()

        // Cancel, then confirm in modal
        formPage.cancelButton.click()
        assertThat(confirmCancelReservationModal.root).isVisible()
        confirmCancelReservationModal.confirmButton.click()
        assertThat(reservationPage.header).isVisible()
    }

    @Test
    fun authenticationOnReservation() {
        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()

        val filterSection = reservationPage.getFilterSection()
        filterSection.slipRadio.click()

        val slipFilterSection = filterSection.getSlipFilterSection()
        slipFilterSection.boatTypeSelect.selectOption("Sailboat")
        slipFilterSection.widthInput.fill("3")
        slipFilterSection.lengthInput.fill("6")

        val searchResultsSection = reservationPage.getSearchResultsSection()
        searchResultsSection.firstReserveButton.click()

        val loginModal = reservationPage.getLoginModal()
        assertThat(loginModal.root).isVisible()

        loginModal.cancelButton.click()
        assertThat(loginModal.root).isHidden()

        searchResultsSection.firstReserveButton.click()
        loginModal.continueButton.click()
        page.getByText("Kirjaudu").click()

        assertThat(slipFilterSection.widthInput).hasValue("3")
        assertThat(slipFilterSection.lengthInput).hasValue("6")
    }

    @Test
    @Disabled("Feature is not working")
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

        val formPage = EmployeeBoatSpaceFormPage(page)
        assertThat(formPage.header).isVisible()
        formPage.fillFormAndSubmit()

        val paymentPage = EmployeePaymentPage(page)
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
        CitizenHomePage(page).loginAsOliviaVirtanen()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingBoatSpaceB314()

        val formPage = BoatSpaceFormPage(page)
        formPage.fillFormAndSubmit()

        val paymentPage = PaymentPage(page)
        paymentPage.nordeaSuccessButton.click()

        assertThat(paymentPage.reservationSuccessNotification).isVisible()
    }

    @Test
    @Disabled("Feature is not working")
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

        val formPage = EmployeeBoatSpaceFormPage(page)
        formPage.fillFormAndSubmit()

        val paymentPage = EmployeePaymentPage(page)
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

        CitizenHomePage(page).loginAsLeoKorhonen()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()

        val filterSection = reservationPage.getFilterSection()
        filterSection.slipRadio.click()

        val slipFilter = filterSection.getSlipFilterSection()
        slipFilter.widthInput.fill("3")
        slipFilter.lengthInput.fill("6")

        val searchResults = reservationPage.getSearchResultsSection()
        searchResults.firstReserveButton.click()

        assertThat(page.locator("body")).containsText("Varaaminen ei ole mahdollista")
    }
}
