package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.baseUrlWithEnglishLangParam
import fi.espoo.vekkuli.baseUrlWithFinnishLangParam
import fi.espoo.vekkuli.pages.citizen.*
import fi.espoo.vekkuli.utils.*
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
    fun `should be able to switch slip reservation for organization when the price is the same`() {
        try {
            mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
            page.navigate(baseUrlWithFinnishLangParam)
            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsOliviaVirtanen()
            citizenHomePage.navigateToPage()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val filterSection = reservationPage.getFilterSection()
            filterSection.slipRadio.click()

            reservationPage.startReservingBoatSpaceB059()

            val searchResultsSection = reservationPage.getSearchResultsSection()

            searchResultsSection.firstReserveButton.click()
            val reserveModal = reservationPage.getReserveModal()

            val expectedBoatSpaceSection = "B"
            val expectedPlaceNumber = "059"
            assertThat(reserveModal.root).isVisible()
            assertThat(reserveModal.secondSwitchReservationButton).isVisible()
            reserveModal.secondSwitchReservationButton.click()

            val form = BoatSpaceFormPage(page)
            val userAgreementSection = form.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            form.confirmButton.click()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()

            val organizationDetailsSection = OrganizationDetailsPage(page).getFirstReservationSection()
            assertThat(organizationDetailsSection.locationName).containsText("Haukilahti")
            assertThat(organizationDetailsSection.place).containsText("$expectedBoatSpaceSection $expectedPlaceNumber")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to switch slip reservation for organization`() {
        try {
            mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
            page.navigate(baseUrlWithFinnishLangParam)
            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsOliviaVirtanen()
            citizenHomePage.navigateToPage()
            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()
            reservationPage.startReservingBoatSpaceB314()

            val reserveModal = reservationPage.getReserveModal()
            assertThat(reserveModal.reserveANewSpace).isVisible()
            reserveModal.reserveANewSpace.click()

            val form = BoatSpaceFormPage(page)
            form.fillFormAndSubmit()

            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()

            reservationPage.navigateToPage()
            reservationPage.startReservingBoatSpaceB059()

            assertThat(reserveModal.root).isVisible()
            assertThat(reserveModal.reserveANewSpace).isVisible()
            assertThat(reserveModal.reserveANewSpace).isVisible()
            reserveModal.reserveANewSpace.click()
            form.fillFormAndSubmit()

            paymentPage.nordeaSuccessButton.click()

            assertThat(confirmationPage.reservationSuccessNotification).isVisible()

            reservationPage.navigateToPage()
            reservationPage.filterForSlipBoatSpace()
            reservationPage.getSearchResultsSection().firstReserveButton.click()
            assertThat(reserveModal.reserveANewSpace).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to switch a trailer space`() {
        try {
            mockTimeProvider(timeProvider, startOfTrailerReservationPeriod)
            page.navigate(baseUrlWithFinnishLangParam)
            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsOliviaVirtanen()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()

            val filterSection = reservationPage.getFilterSection()
            val trailerFilterSection = filterSection.getTrailerFilterSection()

            ReserveBoatSpacePage(page).reserveTrailerBoatSpace()

            mockTimeProvider(timeProvider, startOfTrailerSwitchPeriodForEspooCitizen)

            reservationPage.navigateToPage()

            filterSection.trailerRadio.click()

            trailerFilterSection.widthInput.fill("1")
            trailerFilterSection.lengthInput.fill("3")
            val expectedBoatSpaceSection = "TRAILERI"
            val expectedPlaceNumber = "015"
            selectBoatSpaceForSwitch(reservationPage, 1, expectedBoatSpaceSection, expectedPlaceNumber)
            // switch form
            val switchSpaceFormPage = SwitchSpaceFormPage(page)
            // Make sure that citizen is redirected to unfinished reservation switch form
            reservationPage.navigateToPage()
            val trailerRegistrationNumber = "ABC-456"
            switchSpaceFormPage.getTrailerStorageTypeSection().trailerRegistrationNumberInput.fill(
                trailerRegistrationNumber
            )
            val userAgreementSection = switchSpaceFormPage.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            switchSpaceFormPage.reserveButton.click()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            // Check that the renewed reservation is visible
            val firstReservationSection = citizenDetailsPage.getFirstReservationSection()
            assertThat(firstReservationSection.locationName).containsText("Suomenoja")
            assertThat(firstReservationSection.place).containsText("$expectedBoatSpaceSection $expectedPlaceNumber")
            assertThat(firstReservationSection.getTrailerSection().registrationCodeField).containsText(trailerRegistrationNumber)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to switch a winter space as Espoo citizen that is the same price`() {
        try {
            val reservationPage = ReserveBoatSpacePage(page)
            val filterSection = reservationPage.getFilterSection()
            val winterFilterSection = filterSection.getWinterFilterSection()
            CitizenHomePage(page).loginAsLeoKorhonen()
            mockTimeProvider(timeProvider, startOfWinterReservationPeriod)
            reservationPage.reserveWinterBoatSpace()

            val expectedBoatSpaceSection = "B"
            val expectedPlaceNumber = "017"
            switchWinterSpace(
                reservationPage,
                filterSection,
                winterFilterSection,
                "1",
                "3",
                2,
                expectedHarbor = "Haukilahti",
                expectedBoatSpaceSection,
                expectedPlaceNumber
            )
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to switch a winter space as Espoo citizen that is more expensive`() {
        try {
            val reservationPage = ReserveBoatSpacePage(page)
            val filterSection = reservationPage.getFilterSection()
            val winterFilterSection = filterSection.getWinterFilterSection()
            CitizenHomePage(page).loginAsLeoKorhonen()
            mockTimeProvider(timeProvider, startOfWinterReservationPeriod)
            reservationPage.reserveWinterBoatSpace()
            switchWinterSpace(reservationPage, filterSection, winterFilterSection, "3", "5", 1, "Suomenoja", "B", "087", paymentFlow = true)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    private fun switchWinterSpace(
        reservationPage: ReserveBoatSpacePage,
        filterSection: ReserveBoatSpacePage.FilterSection,
        winterFilterSection: ReserveBoatSpacePage.WinterFilterSection,
        width: String?,
        length: String?,
        expectedHarbors: Int = 1,
        expectedHarbor: String? = null,
        expectedBoatSpaceSection: String? = null,
        expectedPlaceNumber: String? = null,
        paymentFlow: Boolean = false
    ) {
        mockTimeProvider(timeProvider, startOfWinterSwitchPeriodForEspooCitizen)

        reservationPage.navigateToPage()

        filterSection.winterRadio.click()
        winterFilterSection.widthInput.fill(width)
        winterFilterSection.lengthInput.fill(length)

        selectBoatSpaceForSwitch(reservationPage, expectedHarbors, expectedBoatSpaceSection, expectedPlaceNumber)
        // switch form
        val switchSpaceFormPage = SwitchSpaceFormPage(page)
        // Make sure that citizen is redirected to unfinished reservation switch form
        reservationPage.navigateToPage()
        val trailerRegistrationNumber = "ABC-456"
        switchSpaceFormPage.getWinterStorageTypeSection().trailerRegistrationNumberInput.fill(
            trailerRegistrationNumber
        )
        val userAgreementSection = switchSpaceFormPage.getUserAgreementSection()
        userAgreementSection.certifyInfoCheckbox.check()
        userAgreementSection.agreementCheckbox.check()
        if (!paymentFlow) {
            switchSpaceFormPage.reserveButton.click()
        } else {
            switchSpaceFormPage.submitButton.click()
            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()
        }

        val confirmationPage = ConfirmationPage(page)
        assertThat(confirmationPage.reservationSuccessNotification).isVisible()

        // Check that the renewed reservation is visible
        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()

        val firstReservationSection = citizenDetailsPage.getFirstReservationSection()
        assertThat(firstReservationSection.locationName).containsText(expectedHarbor)
        assertThat(firstReservationSection.place).containsText("$expectedBoatSpaceSection $expectedPlaceNumber")
        assertThat(firstReservationSection.getTrailerSection().registrationCodeField).containsText(trailerRegistrationNumber)
    }

    @Test
    fun `should be able to switch a storage space`() {
        try {
            val reservationPage = ReserveBoatSpacePage(page)
            val filterSection = reservationPage.getFilterSection()
            val storageFilterSection = filterSection.getStorageFilterSection()
            reservationPage.navigateToPage()

            reservationPage.reserveStorageWithTrailerType(filterSection, storageFilterSection)

            mockTimeProvider(timeProvider, startOfStorageSwitchPeriodForEspooCitizen)

            reservationPage.navigateToPage()

            filterSection.storageRadio.click()

            storageFilterSection.trailerRadio.click()
            storageFilterSection.widthInput.fill("1")
            storageFilterSection.lengthInput.fill("3")
            val expectedBoatSpaceSection = "B"
            val expectedPlaceNumber = "009"
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

            val firstReservationSection = citizenDetailsPage.getFirstReservationSection()
            assertThat(firstReservationSection.locationName).containsText("Haukilahti")
            assertThat(firstReservationSection.place).containsText("$expectedBoatSpaceSection $expectedPlaceNumber")
            assertThat(firstReservationSection.getTrailerSection().registrationCodeField).containsText(trailerRegistrationNumber)
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
            assertThat(reserveModal.reserveANewSpace).isVisible()
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
        choosePlace(section, placeNumber, searchResultsSection)
        val reserveModal = reservationPage.getReserveModal()
        assertThat(reserveModal.root).isVisible()
        if (cancelAtFirst) {
            reserveModal.cancelButton.click()
            assertThat(reserveModal.root).isHidden()
            choosePlace(section, placeNumber, searchResultsSection)
        }
        reserveModal.firstSwitchReservationButton.click()
    }

    private fun choosePlace(
        section: String?,
        placeNumber: String?,
        searchResultsSection: ReserveBoatSpacePage.SearchResultsSection,
    ) {
        if (section != null && placeNumber != null) {
            searchResultsSection.reserveButtonByPlace(section, placeNumber).click()
        } else {
            searchResultsSection.firstReserveButton.click()
        }
    }
}
