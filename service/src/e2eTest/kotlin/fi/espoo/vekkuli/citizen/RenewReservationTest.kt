package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.pages.citizen.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipRenewPeriod
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class RenewReservationTest : PlaywrightTest() {
    @Test
    fun `should be able to renew reservation`() {
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
            assertThat(paymentPage.reservationSuccessNotification).isVisible()

            // Check that the renewed reservation is visible
            citizenDetailsPage.navigateToPage()
            assertThat(reservationSection.renewButton).isVisible()
            reservationSection.renewButton.click()

            val errorModal = citizenDetailsPage.getErrorModal()
            assertThat(errorModal.title).isVisible()
            errorModal.title.click()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
