package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.pages.citizen.*
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

            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()
            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()

            // Check that the renewed reservation is visible
            citizenDetailsPage.navigateToPage()
            assertThat(reservationSection.renewButton).isHidden()
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
            }
            PaymentPage(page).payReservation()
            mockTimeProvider(timeProvider, startOfWinterSpaceRenewPeriod)

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservationSection = citizenDetailsPage.getFirstReservationSection()

            assertThat(reservationSection.renewButton).isVisible()
            reservationSection.renewButton.click()
            // Make sure that citizen is redirected to unfinished reservation switch form
            reservationPage.navigateToPage()

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

            val filterSection = reservationPage.getFilterSection()
            filterSection.trailerRadio.click()
            val trailerFilterSection = filterSection.getTrailerFilterSection()
            trailerFilterSection.widthInput.fill("1")
            trailerFilterSection.lengthInput.fill("3")

            reservationPage.getSearchResultsSection().firstReserveButton.click()

            val form = BoatSpaceFormPage(page)
            form.fillFormAndSubmit {
                getBoatSection().widthInput.fill("2")
                getBoatSection().lengthInput.fill("5")
                getWinterStorageTypeSection().trailerRegistrationNumberInput.fill("ABC-123")
            }
            PaymentPage(page).payReservation()
            mockTimeProvider(timeProvider, startofTrailerRenewPeriod)
            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservationSection = citizenDetailsPage.getFirstReservationSection()

            assertThat(reservationSection.renewButton).isVisible()
            reservationSection.renewButton.click()
            // Make sure that citizen is redirected to unfinished reservation switch form
            reservationPage.navigateToPage()

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
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
    @Test
    fun `should be able to renew storage reservation`() {
        try {
            mockTimeProvider(timeProvider, startOfStorageReservationPeriod)
             val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsLeoKorhonen()
            citizenHomePage.navigateToPage()
            citizenHomePage.languageSelector.click()
            citizenHomePage.languageSelector.getByText("Suomi").click()
             val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val filterSection = reservationPage.getFilterSection()
            filterSection.storageRadio.click()
            page.pause()
            val storageFilterSection = filterSection.getStorageFilterSection()
            storageFilterSection.trailerRadio.click()
            storageFilterSection.widthInput.fill("1")
            storageFilterSection.lengthInput.fill("3")

            reservationPage.getSearchResultsSection().firstReserveButton.click()

            val form = BoatSpaceFormPage(page)
            form.fillFormAndSubmit {
                getBoatSection().widthInput.fill("2")
                getBoatSection().lengthInput.fill("5")
                getWinterStorageTypeSection().trailerRegistrationNumberInput.fill("ABC-123")
            }
            PaymentPage(page).payReservation()
            assertThat(PaymentPage(page).reservationSuccessNotification).isVisible()
            mockTimeProvider(timeProvider, startOfStorageRenewPeriod)
             val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservationSection = citizenDetailsPage.getFirstReservationSection()

            assertThat(reservationSection.renewButton).isVisible()
            reservationSection.renewButton.click()
            // Make sure that citizen is redirected to unfinished reservation switch form
            reservationPage.navigateToPage()

            assertThat(form.getWinterStorageTypeSection().trailerRegistrationNumberInput).hasValue("ABC-123")

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

            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()
            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
            val paymentDiscountText = paymentPage.getByDataTestId("reservation-info-text")
            assertThat(paymentDiscountText).containsText("$discount %")
            assertThat(paymentDiscountText).containsText("$expectedPrice €")

            // Check that the renewed reservation is visible
            citizenDetailsPage.navigateToPage()
            assertThat(reservationSection.renewButton).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
