package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.baseUrlWithEnglishLangParam
import fi.espoo.vekkuli.pages.citizen.*
import fi.espoo.vekkuli.pages.employee.CitizenDetailsPage
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipReservationPeriod
import fi.espoo.vekkuli.utils.startOfStorageReservationPeriod
import fi.espoo.vekkuli.utils.startOfStorageSwitchPeriodForEspooCitizen
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class SwitchReservationTest : ReserveTest() {
    @Test
    fun `should be able to switch slip reservation when the price is higher`() {
        try {
            val reservationPage = switchSlipBoatSpace(CitizenHomePage.leoKorhonenSsn, "3", "6", 3)

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
    fun `should be able to switch slip reservation when the price is the same`() {
        try {
            val reservationPage = switchSlipBoatSpace(CitizenHomePage.leoKorhonenSsn, "2", "4", 5)

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
    fun `should be able to switch a storage space`() {
        try {
            mockTimeProvider(timeProvider, startOfStorageReservationPeriod)
            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsMikkoVirtanen()
            citizenHomePage.navigateToPage()
            citizenHomePage.languageSelector.click()
            citizenHomePage.languageSelector.getByText("Suomi").click()
            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val filterSection = reservationPage.getFilterSection()
            filterSection.storageRadio.click()

            val storageFilterSection = filterSection.getStorageFilterSection()
            storageFilterSection.buckRadio.click()
            storageFilterSection.widthInput.fill("1")
            storageFilterSection.lengthInput.fill("3")
            reservationPage.getSearchResultsSection().reserveButtonByPlace("B", "011").click()
            val form = BoatSpaceFormPage(page)
            form.fillFormAndSubmit {
                getBoatSection().widthInput.fill("2")
                getBoatSection().lengthInput.fill("5")
            }

            PaymentPage(page).payReservation()
            assertThat(PaymentPage(page).reservationSuccessNotification).isVisible()

            mockTimeProvider(timeProvider, startOfStorageSwitchPeriodForEspooCitizen)

            reservationPage.navigateToPage()

            filterSection.storageRadio.click()

            storageFilterSection.trailerRadio.click()
            storageFilterSection.widthInput.fill("1")
            storageFilterSection.lengthInput.fill("3")

            val expectedBoatSpaceSection = "B"
            val expectedPlaceNumber = "007"
            selectBoatSpaceForSwitch(reservationPage, 3, expectedBoatSpaceSection, expectedPlaceNumber)
            // switch form
            val switchSpaceFormPage = SwitchSpaceFormPage(page)
            // Make sure that citizen is redirected to unfinished reservation switch form
            reservationPage.navigateToPage()
            val trailerRegistrationNumber = "ABC-123"
            switchSpaceFormPage.getWinterStorageTypeSection().trailerRegistrationNumberInput.fill(
                trailerRegistrationNumber
            )
            val userAgreementSection = switchSpaceFormPage.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            switchSpaceFormPage.reserveButton.click()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()

            // Check that the renewed reservation is visible
            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()

            assertThat(citizenDetailsPage.firstBoatSpaceReservationCard).isVisible()
            assertThat(citizenDetailsPage.firstBoatSpaceReservationCard).containsText("Haukilahti")
            assertThat(citizenDetailsPage.firstBoatSpaceReservationCard).containsText("$expectedBoatSpaceSection $expectedPlaceNumber")
            assertThat(citizenDetailsPage.firstBoatSpaceReservationCard).containsText("Trailerisäilytys")
            assertThat(citizenDetailsPage.firstBoatSpaceReservationCard).containsText(trailerRegistrationNumber)
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
            val reservationPage = switchSlipBoatSpace(CitizenHomePage.oliviaVirtanenSsn, "3", "6", 3, false)

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

    private fun switchSlipBoatSpace(
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
            assertThat(reserveModal.reserveAnotherButton).isVisible()
        }
        reserveModal.firstSwitchReservationButton.click()

        return reservationPage
    }

    private fun selectBoatSpaceForSwitch(
        reservationPage: ReserveBoatSpacePage,
        expectedHarbors: Int,
        section: String?,
        placeNumber: String?,
        cancelAtFirst: Boolean = true,
    ) {
        val searchResultsSection = reservationPage.getSearchResultsSection()
        assertThat(searchResultsSection.harborHeaders).hasCount(expectedHarbors)
        if (section != null && placeNumber != null) {
            searchResultsSection.reserveButtonByPlace(section, placeNumber).click()
        } else {
            searchResultsSection.firstReserveButton.click()
        }
        val reserveModal = reservationPage.getReserveModal()
        assertThat(reserveModal.root).isVisible()
        if (cancelAtFirst) {
            reserveModal.cancelButton.click()
            assertThat(reserveModal.root).isHidden()
            searchResultsSection.firstReserveButton.click()
        }
        reserveModal.firstSwitchReservationButton.click()
    }
}
