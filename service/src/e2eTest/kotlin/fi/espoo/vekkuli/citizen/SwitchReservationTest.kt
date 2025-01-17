package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.baseUrlWithEnglishLangParam
import fi.espoo.vekkuli.pages.*
import fi.espoo.vekkuli.pages.citizen.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipReservationPeriod
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class SwitchReservationTest : PlaywrightTest() {
    @Test
    fun `should be able to switch reservation when the price is higher`() {
        try {
            mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
            page.navigate(baseUrlWithEnglishLangParam)
            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsLeoKorhonen()

            citizenHomePage.navigateToPage()
            citizenHomePage.languageSelector.click()
            citizenHomePage.languageSelector.getByText("Suomi").click()

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

            searchResultsSection.firstReserveButton.click()

            val reserveModal = reservationPage.getReserveModal()

            assertThat(reserveModal.root).isVisible()

            reserveModal.cancelButton.click()
            assertThat(reserveModal.root).isHidden()
            searchResultsSection.firstReserveButton.click()
            assertThat(reserveModal.reserveAnotherButton).isVisible()
            reserveModal.firstSwitchReservationButton.click()

            // switch form
            val form = BoatSpaceFormPage(page)
            // Make sure that citizen is redirected to unfinished reservation switch form
            reservationPage.navigateToPage()

            val userAgreementSection = form.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            form.submitButton.click()

            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()
            assertThat(paymentPage.reservationSuccessNotification).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to switch reservation when the price is the same`() {
        try {
            mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
            page.navigate(baseUrlWithEnglishLangParam)
            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsLeoKorhonen()

            citizenHomePage.navigateToPage()
            citizenHomePage.languageSelector.click()
            citizenHomePage.languageSelector.getByText("Suomi").click()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val filterSection = reservationPage.getFilterSection()
            filterSection.slipRadio.click()

            val slipFilterSection = filterSection.getSlipFilterSection()
            slipFilterSection.boatTypeSelect.selectOption("Sailboat")
            slipFilterSection.widthInput.fill("2")
            slipFilterSection.lengthInput.fill("4")
            slipFilterSection.amenityBuoyCheckbox.check()
            slipFilterSection.amenityRearBuoyCheckbox.check()
            slipFilterSection.amenityBeamCheckbox.check()
            slipFilterSection.amenityWalkBeamCheckbox.check()

            val searchResultsSection = reservationPage.getSearchResultsSection()
            assertThat(searchResultsSection.harborHeaders).hasCount(5)

            searchResultsSection.firstReserveButton.click()

            val reserveModal = reservationPage.getReserveModal()

            assertThat(reserveModal.root).isVisible()

            reserveModal.cancelButton.click()
            assertThat(reserveModal.root).isHidden()
            searchResultsSection.firstReserveButton.click()
            assertThat(reserveModal.reserveAnotherButton).isVisible()
            reserveModal.firstSwitchReservationButton.click()

            // switch form
            val switchSpaceFormPage = SwitchSpaceFormPage(page)
            // Make sure that citizen is redirected to unfinished reservation switch form
            reservationPage.navigateToPage()

            val userAgreementSection = switchSpaceFormPage.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            switchSpaceFormPage.reserveButton.click()

            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.reservationSuccessNotification).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
