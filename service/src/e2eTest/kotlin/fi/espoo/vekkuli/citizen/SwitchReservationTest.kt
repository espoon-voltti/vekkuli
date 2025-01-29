package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.baseUrlWithEnglishLangParam
import fi.espoo.vekkuli.pages.citizen.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipReservationPeriod
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class SwitchReservationTest : ReserveTest() {
    @Test
    fun `should be able to switch reservation when the price is higher`() {
        try {
            val reservationPage = reserveSecondBoatSpace(CitizenHomePage.leoKorhonenSsn, "3", "6", 3)

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
            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to switch reservation when the price is the same`() {
        try {
            val reservationPage = reserveSecondBoatSpace(CitizenHomePage.leoKorhonenSsn, "2", "4", 5)

            // switch form
            val switchSpaceFormPage = SwitchSpaceFormPage(page)
            // Make sure that citizen is redirected to unfinished reservation switch form
            reservationPage.navigateToPage()

            val userAgreementSection = switchSpaceFormPage.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            switchSpaceFormPage.reserveButton.click()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reservers discount is applied to the switch payment difference when new place is more expensive`() {
        val discount = 50
        val expectedDifference = "150,81"
        val expectedPrice = "75,41"
        try {
            setDiscountForReserver(page, "Virtanen", discount)
            val reservationPage = reserveSecondBoatSpace(CitizenHomePage.oliviaVirtanenSsn, "3", "6", 3, false)

            // switch form
            val form = BoatSpaceFormPage(page)
            // Make sure that citizen is redirected to unfinished reservation switch form
            reservationPage.navigateToPage()

            val userAgreementSection = form.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()

            val reservedSpaceSection = form.getReservedSpaceSection()
            assertThat(reservedSpaceSection.fields.getField("Hinta").last()).containsText("Yhteensä: 418,00 €")
            val discountText = form.getByDataTestId("reservation-info-text")
            assertThat(discountText).containsText("erotus $expectedDifference €")
            assertThat(discountText).containsText("$discount %")
            assertThat(discountText).containsText("$expectedPrice €")
            form.submitButton.click()

            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()
            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()

            val paymentDiscountText = paymentPage.getByDataTestId("reservation-info-text")
            assertThat(paymentDiscountText).containsText("erotus $expectedDifference €")
            assertThat(paymentDiscountText).containsText("$discount %")
            assertThat(paymentDiscountText).containsText("$expectedPrice €")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    private fun reserveSecondBoatSpace(
        citizenSsn: String,
        width: String,
        length: String,
        expectedHarbors: Int,
        cancelAtFirst: Boolean = true
    ): ReserveBoatSpacePage {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        page.navigate(baseUrlWithEnglishLangParam)
        val citizenHomePage = CitizenHomePage(page)
        citizenHomePage.loginAsCitizen(citizenSsn)

        citizenHomePage.navigateToPage()
        citizenHomePage.languageSelector.click()
        citizenHomePage.languageSelector.getByText("Suomi").click()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()

        val filterSection = reservationPage.getFilterSection()
        filterSection.slipRadio.click()

        val slipFilterSection = filterSection.getSlipFilterSection()
        slipFilterSection.boatTypeSelect.selectOption("Sailboat")
        slipFilterSection.widthInput.fill(width)
        slipFilterSection.lengthInput.fill(length)
        slipFilterSection.amenityBuoyCheckbox.check()
        slipFilterSection.amenityRearBuoyCheckbox.check()
        slipFilterSection.amenityBeamCheckbox.check()
        slipFilterSection.amenityWalkBeamCheckbox.check()

        val searchResultsSection = reservationPage.getSearchResultsSection()
        assertThat(searchResultsSection.harborHeaders).hasCount(expectedHarbors)

        searchResultsSection.firstReserveButton.click()
        val reserveModal = reservationPage.getReserveModal()
        assertThat(reserveModal.root).isVisible()
        if (cancelAtFirst) {
            reserveModal.cancelButton.click()
            assertThat(reserveModal.root).isHidden()
            searchResultsSection.firstReserveButton.click()
            assertThat(reserveModal.reserveANewSpace).isVisible()
        }
        reserveModal.firstSwitchReservationButton.click()

        return reservationPage
    }
}
