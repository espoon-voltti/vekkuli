package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.ReserveTest
import fi.espoo.vekkuli.domain.PaymentStatus
import fi.espoo.vekkuli.pages.citizen.*
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class RenewReservationTest : ReserveTest() {
    @Test
    fun `should be able to renew slip reservation`() {
        try {
            mockTimeProvider(timeProvider, startOfSlipRenewPeriod)

            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsLeoKorhonen()
            citizenHomePage.navigateToPage()
            citizenHomePage.languageSelector.click()
            citizenHomePage.languageSelector.getByText("Suomi").click()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservationSection = citizenDetailsPage.getFirstReservationSection()

            assertThat(reservationSection.renewButton).isVisible()
            reservationSection.renewButton.click()
            // renew form
            val form = BoatSpaceFormPage(page)
            assertThat(form.header).isVisible()
            // Make sure that citizen is redirected to unfinished reservation switch form
            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val citizenSection = form.getCitizenSection()
            assertThat(citizenSection.emailInput).isVisible()
            assertThat(citizenSection.phoneInput).isVisible()

            val userAgreementSection = form.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            form.submitButton.click()

            assertZeroEmailsSent()
            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()
            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()

            // Check that the renewed reservation is visible
            citizenDetailsPage.navigateToPage()
            assertThat(reservationSection.renewButton).isHidden()
            assertThat(citizenDetailsPage.reservationListCards).containsText("Laituripaikka: Haukilahti B 001")

            assertEmailIsSentOfCitizensSlipRenewal("leo@noreplytest.fi")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should show renew notification when it's time to renew boat space`() {
        mockTimeProvider(timeProvider, startOfSlipRenewPeriod)

        CitizenHomePage(page).loginAsEspooCitizenWithActiveSlipReservation()
        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()

        val reservationSection = citizenDetailsPage.getFirstReservationSection()
        assertThat(reservationSection.renewNotification).isVisible()
        assertThat(reservationSection.renewNotification).containsText("31.01.2025")

        reservationSection.renewButton.click()
        BoatSpaceFormPage(page).fillFormAndSubmit()
        PaymentPage(page).payReservation()

        mockTimeProvider(timeProvider, startOfSlipRenewPeriod.plusYears(1))
        citizenDetailsPage.navigateToPage()
        assertThat(reservationSection.renewNotification).isVisible()
        assertThat(reservationSection.renewNotification).containsText("31.01.2026")
    }

    @Test
    fun `should be able to renew slip reservation for organization`() {
        try {
            mockTimeProvider(timeProvider, startOfSlipRenewPeriod)

            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsOliviaVirtanen()
            citizenHomePage.navigateToPage()
            citizenHomePage.languageSelector.click()
            citizenHomePage.languageSelector.getByText("Suomi").click()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            page.getByText("Espoon Pursiseura").click()

            val reservationSection = citizenDetailsPage.getFirstReservationSection()

            assertThat(reservationSection.renewButton).isVisible()
            reservationSection.renewButton.click()
            // renew form
            val form = BoatSpaceFormPage(page)
            assertThat(form.header).isVisible()
            // Make sure that citizen is redirected to unfinished reservation switch form
            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val userAgreementSection = form.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            form.submitButton.click()

            assertZeroEmailsSent()
            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()
            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()

            // Check that the renewed reservation is visible
            citizenDetailsPage.navigateToPage()
            page.getByText("Espoon Pursiseura").click()
            assertThat(reservationSection.renewButton).isHidden()
            assertThat(citizenDetailsPage.reservationListCards).containsText("Laituripaikka: Haukilahti B 005")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to renew winter reservation`() {
        try {
            mockTimeProvider(timeProvider, startOfWinterReservationPeriod)
            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsLeoKorhonen()
            citizenHomePage.navigateToPage()
            citizenHomePage.languageSelector.click()
            citizenHomePage.languageSelector.getByText("Suomi").click()
            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val filterSection = reservationPage.getFilterSection()
            filterSection.winterRadio.click()
            val winterFilterSection = filterSection.getWinterFilterSection()
            winterFilterSection.widthInput.fill("1")
            winterFilterSection.lengthInput.fill("3")

            reservationPage.getSearchResultsSection().firstReserveButton.click()

            val form = BoatSpaceFormPage(page)
            form.fillFormAndSubmit {
                getBoatSection().widthInput.fill("2")
                getBoatSection().lengthInput.fill("5")
                getWinterStorageTypeSection().trailerRegistrationNumberInput.fill("ABC-123")
                getWinterStorageTypeSection().trailerLengthInput.fill("3")
                getWinterStorageTypeSection().trailerWidthInput.fill("1")
            }
            PaymentPage(page).payReservation()

            assertEmailIsSentOfCitizensWinterSpaceReservation()
            SendEmailServiceMock.resetEmails()
            mockTimeProvider(timeProvider, startOfWinterSpaceRenewPeriod2026)

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservationSection = citizenDetailsPage.getFirstReservationSection()

            assertThat(reservationSection.renewButton).isVisible()
            reservationSection.renewButton.click()

            val userAgreementSection = form.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            form.submitButton.click()

            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()
            assertThat(paymentPage.reservationSuccessNotification).isVisible()

            // Check that the renewed reservation is visible
            citizenDetailsPage.navigateToPage()
            assertThat(reservationSection.renewButton).isHidden()

            assertThat(citizenDetailsPage.reservationListCards).containsText("Talvipaikka: Haukilahti B 013")

            assertEmailIsSentOfCitizensWinterSpaceRenewal()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to renew trailer reservation`() {
        try {
            mockTimeProvider(timeProvider, startOfTrailerReservationPeriod)
            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsLeoKorhonen()
            citizenHomePage.navigateToPage()
            citizenHomePage.languageSelector.click()
            citizenHomePage.languageSelector.getByText("Suomi").click()
            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val form = reservationPage.reserveTrailerBoatSpace()

            assertEmailIsSentOfCitizensIndefiniteTrailerReservation()
            SendEmailServiceMock.resetEmails()

            mockTimeProvider(timeProvider, startofTrailerRenewPeriod)
            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservationSection = citizenDetailsPage.getFirstReservationSection()

            assertThat(reservationSection.renewButton).isVisible()
            reservationSection.renewButton.click()

            val userAgreementSection = form.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            form.submitButton.click()
            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()
            assertThat(paymentPage.reservationSuccessNotification).isVisible()

            // Check that the renewed reservation is visible
            citizenDetailsPage.navigateToPage()

            assertEmailIsSentOfCitizensTrailerRenewal()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to renew storage reservation`() {
        try {
            mockTimeProvider(timeProvider, startOfStorageReservationPeriod)

            val reservationPage = ReserveBoatSpacePage(page)
            val filterSection = reservationPage.getFilterSection()
            val storageFilterSection = filterSection.getStorageFilterSection()
            reservationPage.reserveStorageWithTrailerType(filterSection, storageFilterSection)

            assertEmailIsSentOfCitizensStorageSpaceReservation()
            SendEmailServiceMock.resetEmails()
            mockTimeProvider(timeProvider, startOfStorageRenewPeriod2026)

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservationSection = citizenDetailsPage.getReservationSection("Haukilahti B 007")

            assertThat(reservationSection.renewButton).isVisible()
            reservationSection.renewButton.click()

            val form = BoatSpaceFormPage(page)
            assertThat(form.getWinterStorageTypeSection().trailerLengthInput).isVisible()
            form.getWinterStorageTypeSection().trailerRegistrationNumberInput.fill("ABC-123")
            form.getWinterStorageTypeSection().trailerLengthInput.fill("5")
            form.getWinterStorageTypeSection().trailerWidthInput.fill("2")

            val userAgreementSection = form.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            form.submitButton.click()

            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()
            assertThat(paymentPage.reservationSuccessNotification).isVisible()
            assertEmailIsSentOfCitizensStorageSpaceRenewal()

            // Check that the renewed reservation is visible
            citizenDetailsPage.navigateToPage()
            assertThat(reservationSection.renewButton).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `if reserver has a discount should get a discount when renewing reservation`() {
        val discount = 50
        val expectedPrice = "133,60"
        try {
            setDiscountForReserver(page, "Korhonen", discount)
            mockTimeProvider(timeProvider, startOfSlipRenewPeriod)

            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsLeoKorhonen()
            citizenHomePage.navigateToPage()
            citizenHomePage.languageSelector.click()
            citizenHomePage.languageSelector.getByText("Suomi").click()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservationSection = citizenDetailsPage.getFirstReservationSection()

            assertThat(reservationSection.renewButton).isVisible()
            reservationSection.renewButton.click()
            // renew form
            val form = BoatSpaceFormPage(page)
            assertThat(form.header).isVisible()
            // Make sure that citizen is redirected to unfinished reservation switch form
            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val citizenSection = form.getCitizenSection()
            assertThat(citizenSection.emailInput).isVisible()
            assertThat(citizenSection.phoneInput).isVisible()

            val userAgreementSection = form.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()

            val reservedSpaceSection = form.getReservedSpaceSection()
            assertThat(reservedSpaceSection.fields.getField("Hinta").last()).containsText("Yhteensä: 267,19 €")
            val discountText = form.getByDataTestId("reservation-info-text")
            assertThat(discountText).containsText("$discount %")
            assertThat(discountText).containsText("$expectedPrice €")
            form.submitButton.click()
            assertZeroEmailsSent()

            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()
            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()

            // Check that the renewed reservation is visible
            citizenDetailsPage.navigateToPage()
            assertThat(reservationSection.renewButton).isHidden()

            assertCorrectPaymentForReserver(
                "korhonen",
                PaymentStatus.Success,
                "Haukilahti B 001",
                expectedPrice,
                "Hinnassa huomioitu $discount% alennus.",
                doLogin = false
            )

            assertEmailIsSentOfCitizensSlipRenewal("leo@noreplytest.fi")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `if reserver has a discount of 100% the reservation should be immediately confirmed`() {
        val discount = 100
        val expectedPrice = "0,00"
        try {
            mockTimeProvider(timeProvider, startOfSlipRenewPeriod)
            setDiscountForReserver(page, "Korhonen", discount)
            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsLeoKorhonen()
            citizenHomePage.navigateToPage()
            citizenHomePage.languageSelector.click()
            citizenHomePage.languageSelector.getByText("Suomi").click()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservationSection = citizenDetailsPage.getFirstReservationSection()

            assertThat(reservationSection.renewButton).isVisible()
            reservationSection.renewButton.click()
            // renew form
            val form = BoatSpaceFormPage(page)
            assertThat(form.header).isVisible()
            // Make sure that citizen is redirected to unfinished reservation switch form
            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val citizenSection = form.getCitizenSection()
            assertThat(citizenSection.emailInput).isVisible()
            assertThat(citizenSection.phoneInput).isVisible()

            val userAgreementSection = form.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()

            val reservedSpaceSection = form.getReservedSpaceSection()
            assertThat(reservedSpaceSection.fields.getField("Hinta").last()).containsText("Yhteensä: 267,19 €")

            form.confirmButton.click()

            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.getByDataTestId("payment-page")).not().isVisible()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()

            assertEmailIsSentOfCitizensSlipRenewal("leo@noreplytest.fi")

            // Check that the renewed reservation is visible
            citizenDetailsPage.navigateToPage()
            assertThat(reservationSection.renewButton).isHidden()

            assertCorrectPaymentForReserver(
                "korhonen",
                PaymentStatus.Success,
                "Haukilahti B 001",
                expectedPrice,
                "Hinnassa huomioitu $discount% alennus.",
                doLogin = false
            )
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
