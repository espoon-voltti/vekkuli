package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.ReserveTest
import fi.espoo.vekkuli.baseUrlWithEnglishLangParam
import fi.espoo.vekkuli.baseUrlWithFinnishLangParam
import fi.espoo.vekkuli.domain.PaymentStatus
import fi.espoo.vekkuli.pages.citizen.*
import fi.espoo.vekkuli.service.PaytrailMock
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@ActiveProfiles("test")
class SwitchReservationTest : ReserveTest() {
    @Test
    fun `should be able to switch slip reservation when the price is higher`() {
        mockTimeProvider(timeProvider, startOfSlipSwitchPeriodForEspooCitizen)
        val reservationPage = switchSlipBoatSpace(CitizenHomePage.leoKorhonenSsn, "3", "6", 3, false)

        // switch form
        val form = BoatSpaceFormPage(page)
        // Make sure that citizen is redirected to unfinished reservation switch form
        reservationPage.navigateToPage()

        val userAgreementSection = form.getUserAgreementSection()
        userAgreementSection.certifyInfoCheckbox.check()
        userAgreementSection.agreementCheckbox.check()
        form.submitButton.click()

        val paymentPage = PaymentPage(page)
        paymentPage.assertOnPaymentPage()
        assertZeroEmailsSent()
        paymentPage.nordeaSuccessButton.click()
        val confirmationPage = ConfirmationPage(page)
        assertThat(confirmationPage.reservationSuccessNotification).isVisible()

        assertCorrectPaymentForReserver(
            "korhonen",
            PaymentStatus.Success,
            "Haukilahti D 013",
            "150,81",
            "Paikan vaihto. Maksettu vain erotus."
        )

        assertEmailIsSentOfCitizensIndefiniteSlipSwitch("leo@noreplytest.fi")
        val citizenDetails = citizenPageInEmployeeView("korhonen", false)
        citizenDetails.memoNavi.click()
        assertThat(citizenDetails.userMemo(2))
            .containsText("Leo Korhonen vaihtoi paikan. Vanha paikka: Haukilahti D 013. Uusi paikka: Haukilahti B 001.")

        assertEquals(
            "Vaihto Venepaikka 2025 Haukilahti D 013",
            PaytrailMock.paytrailPayments
                .first()
                .items
                ?.first()
                ?.description
        )
    }

    @Test
    fun `should be able to switch slip reservation when the price is the same`() {
        mockTimeProvider(timeProvider, startOfSlipSwitchPeriodForEspooCitizen)
        val reservationPage = switchSlipBoatSpace(CitizenHomePage.leoKorhonenSsn, "2", "4", 5, false)

        // switch form
        val switchSpaceFormPage = SwitchSpaceFormPage(page)
        // Make sure that citizen is redirected to unfinished reservation switch form
        reservationPage.navigateToPage()

        val userAgreementSection = switchSpaceFormPage.getUserAgreementSection()
        userAgreementSection.certifyInfoCheckbox.check()
        userAgreementSection.agreementCheckbox.check()
        switchSpaceFormPage.reserveButton.click()

        // asserting that email is sent when there is no payment
        assertZeroEmailsSent()
        val paymentPage = PaymentPage(page)
        assertThat(paymentPage.getByDataTestId("payment-page")).not().isVisible()

        val confirmationPage = ConfirmationPage(page)
        assertThat(confirmationPage.reservationSuccessNotification).isVisible()

        assertCorrectPaymentForReserver(
            "korhonen",
            PaymentStatus.Success,
            "Haukilahti B 003",
            "0,00",
            "Paikan vaihto. Ei suoritusta, paikoilla sama hinta."
        )
        assertEmailIsSentOfCitizensIndefiniteSlipSwitch("leo@noreplytest.fi")
    }

    @Test
    fun `should be able to switch slip reservation for organization when the price is the same`() {
        mockTimeProvider(timeProvider, startOfSlipSwitchPeriodForEspooCitizen.minusYears(1))
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
    }

    @Test
    fun `should be able to switch slip reservation for organization`() {
        mockTimeProvider(timeProvider, endOfSlipSwitchPeriodForEspooCitizen.minusYears(1))
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
    }

    @Test
    fun `should be able to switch an indefinite trailer space`() {
        verifySuccesfullTrailerSwitch(true)
    }

    @Test
    fun `should be able to switch a fixed term trailer space`() {
        verifySuccesfullTrailerSwitch(false)
    }

    private fun verifySuccesfullTrailerSwitch(forEspooCitizen: Boolean) {
        mockTimeProvider(timeProvider, startOfTrailerReservationPeriod)
        page.navigate(baseUrlWithFinnishLangParam)
        val citizenHomePage = CitizenHomePage(page)
        if (forEspooCitizen) {
            citizenHomePage.loginAsOliviaVirtanen()
        } else {
            citizenHomePage.loginAsNonEspooCitizenMarko()
        }

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()

        val filterSection = reservationPage.getFilterSection()
        val trailerFilterSection = filterSection.getTrailerFilterSection()

        ReserveBoatSpacePage(page).reserveTrailerBoatSpace()
        if (forEspooCitizen) {
            assertEmailIsSentOfCitizensIndefiniteTrailerReservation()
        } else {
            assertEmailIsSentOfCitizensFixedTermTrailerReservation(endDate = "30.04.2026")
        }
        SendEmailServiceMock.resetEmails()

        if (forEspooCitizen) {
            mockTimeProvider(timeProvider, startOfTrailerSwitchPeriodForEspooCitizen)
        } else {
            // mockTimeProvider(timeProvider, startOfTrailerSwitchPeriodForNonEspooCitizen.plusYears(1))
        }

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

        if (forEspooCitizen) {
            assertEmailIsSentOfCitizensIndefiniteTrailerSwitch()
        } else {
            assertEmailIsSentOfCitizensFixedTermTrailerSwitch(endDate = "30.04.2026")
        }
        // Check that the renewed reservation is visible
        val firstReservationSection = citizenDetailsPage.getReservationSection("TRAILERI 015")
        assertThat(firstReservationSection.locationName).containsText("Suomenoja")
        assertThat(firstReservationSection.place).containsText("$expectedBoatSpaceSection $expectedPlaceNumber")
        assertThat(firstReservationSection.getTrailerSection().registrationCodeField).containsText(
            trailerRegistrationNumber
        )
    }

    @Test
    fun `should be able to switch a winter space as Espoo citizen that is the same price`() {
        val reservationPage = ReserveBoatSpacePage(page)
        val filterSection = reservationPage.getFilterSection()
        val winterFilterSection = filterSection.getWinterFilterSection()
        CitizenHomePage(page).loginAsLeoKorhonen()
        mockTimeProvider(timeProvider, startOfWinterReservationPeriod)
        reservationPage.reserveWinterBoatSpace()
        assertEmailIsSentOfCitizensWinterSpaceReservation()
        SendEmailServiceMock.resetEmails()

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
        assertEmailIsSentOfCitizensWinterSpaceSwitch()
    }

    @Test
    fun `should be able to switch a winter space as Espoo citizen that is more expensive`() {
        val reservationPage = ReserveBoatSpacePage(page)
        val filterSection = reservationPage.getFilterSection()
        val winterFilterSection = filterSection.getWinterFilterSection()
        CitizenHomePage(page).loginAsLeoKorhonen()
        mockTimeProvider(timeProvider, startOfWinterReservationPeriod)
        reservationPage.reserveWinterBoatSpace()
        assertEmailIsSentOfCitizensWinterSpaceReservation()
        SendEmailServiceMock.resetEmails()
        switchWinterSpace(
            reservationPage,
            filterSection,
            winterFilterSection,
            "3",
            "5",
            1,
            "Suomenoja",
            "B",
            "087",
            paymentFlow = true
        )
        assertEmailIsSentOfCitizensWinterSpaceSwitch()
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

        val firstReservationSection =
            citizenDetailsPage.getReservationSection("$expectedBoatSpaceSection $expectedPlaceNumber")
        assertThat(firstReservationSection.locationName).containsText(expectedHarbor)
        assertThat(firstReservationSection.place).containsText("$expectedBoatSpaceSection $expectedPlaceNumber")
        assertThat(firstReservationSection.getTrailerSection().registrationCodeField).containsText(
            trailerRegistrationNumber
        )
    }

    @Test
    fun `should be able to switch a storage space`() {
        mockTimeProvider(timeProvider, startOfStorageSwitchPeriodForEspooCitizen)

        val reservationPage = ReserveBoatSpacePage(page)
        val filterSection = reservationPage.getFilterSection()
        val storageFilterSection = filterSection.getStorageFilterSection()
        reservationPage.navigateToPage()

        reservationPage.reserveStorageWithTrailerType(filterSection, storageFilterSection)
        assertEmailIsSentOfCitizensStorageSpaceReservation()
        SendEmailServiceMock.resetEmails()

        mockTimeProvider(timeProvider, startOfStorageSwitchPeriodForEspooCitizen)

        reservationPage.navigateToPage()

        filterSection.storageRadio.click()

        storageFilterSection.trailerRadio.click()
        storageFilterSection.widthInput.fill("1")
        storageFilterSection.lengthInput.fill("3")
        val expectedBoatSpaceSection = "B"
        val expectedPlaceNumber = "009"
        selectBoatSpaceForSwitch(reservationPage, 4, expectedBoatSpaceSection, expectedPlaceNumber)
        // switch form
        val switchSpaceFormPage = SwitchSpaceFormPage(page)

        val trailerRegistrationNumber = "ABC-123"
        switchSpaceFormPage.getWinterStorageTypeSection().trailerRegistrationNumberInput.fill(
            trailerRegistrationNumber
        )
        switchSpaceFormPage.getWinterStorageTypeSection().trailerWidthInput.fill("1")
        switchSpaceFormPage.getWinterStorageTypeSection().trailerLengthInput.fill("3")
        val userAgreementSection = switchSpaceFormPage.getUserAgreementSection()
        userAgreementSection.certifyInfoCheckbox.check()
        userAgreementSection.agreementCheckbox.check()
        switchSpaceFormPage.reserveButton.click()

        val confirmationPage = ConfirmationPage(page)
        assertThat(confirmationPage.reservationSuccessNotification).isVisible()

        // Check that the renewed reservation is visible
        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()
        assertEmailIsSentOfCitizensStorageSpaceSwitch()

        val firstReservationSection =
            citizenDetailsPage.getReservationSection("$expectedBoatSpaceSection $expectedPlaceNumber")
        assertThat(firstReservationSection.locationName).containsText("Haukilahti")
        assertThat(firstReservationSection.place).containsText("$expectedBoatSpaceSection $expectedPlaceNumber")
        assertThat(firstReservationSection.getTrailerSection().registrationCodeField).containsText(
            trailerRegistrationNumber
        )
    }

    @Test
    fun `reservers discount is applied to the slip switch payment difference when new place is more expensive`() {
        val discount = 50
        val expectedDifference = "150,81"
        val expectedPrice = "75,41"
        mockTimeProvider(timeProvider, endOfSlipSwitchPeriodForEspooCitizen.minusYears(1))

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

        form.submitButton.click()
        assertZeroEmailsSent()

        val paymentPage = PaymentPage(page)
        paymentPage.nordeaSuccessButton.click()
        val confirmationPage = ConfirmationPage(page)
        assertThat(confirmationPage.reservationSuccessNotification).isVisible()

        assertCorrectPaymentForReserver(
            "virtanen",
            PaymentStatus.Success,
            "Haukilahti D 013",
            expectedPrice,
            "Paikan vaihto. Maksettu vain erotus. Hinnassa huomioitu $discount% alennus.",
            doLogin = false
        )
        // switched place was fixed term so the new place should be as well
        assertEmailIsSentOfCitizensFixedTermSlipSwitch("olivia@noreplytest.fi")
    }

    @Test
    fun `going to boat space search page from citizen profile switch button limits available space choices`() {
        mockTimeProvider(timeProvider, startOfSlipRenewPeriod)
        CitizenHomePage(page).loginAsEspooCitizenWithActiveSlipReservation()

        val citizenDetails = CitizenDetailsPage(page)
        citizenDetails.navigateToPage()

        val reservationSection = citizenDetails.getReservationSection("Haukilahti B 001")
        reservationSection.switchSpace.click()

        val searchSpacesPage = ReserveBoatSpacePage(page)
        assertThat(searchSpacesPage.header).isVisible()
        assertThat(searchSpacesPage.switchInfoBox).isVisible()

        val filterSection = searchSpacesPage.getFilterSection()
        assertThat(filterSection.slipRadio).not().isDisabled()
        assertThat(filterSection.trailerRadio).isDisabled()
        assertThat(filterSection.winterRadio).isDisabled()
        assertThat(filterSection.storageRadio).isDisabled()

        // start reserving
        searchSpacesPage.startReservingBoatSpaceB314()

        // we should be directly at the form page
        val formPage = BoatSpaceFormPage(page)
        assertThat(formPage.header).isVisible()

        // switch info box should be visible, making it clear that we are switching
        assertThat(formPage.switchInfoBox).isVisible()
    }

    @Test
    fun `back button in space search page when switching should lead back to citizen details page`() {
        mockTimeProvider(timeProvider, startOfSlipRenewPeriod)
        CitizenHomePage(page).loginAsEspooCitizenWithActiveSlipReservation()

        val citizenDetails = CitizenDetailsPage(page)
        citizenDetails.navigateToPage()

        val reservationSection = citizenDetails.getReservationSection("Haukilahti B 001")
        reservationSection.switchSpace.click()

        val searchSpacesPage = ReserveBoatSpacePage(page)
        assertThat(searchSpacesPage.header).isVisible()
        assertThat(searchSpacesPage.switchInfoBox).isVisible()

        searchSpacesPage.switchGoBackButton.click()
        assertThat(citizenDetails.header).isVisible()

        // Back button isn't visible when directly going to the page
        searchSpacesPage.navigateToPage()
        assertThat(searchSpacesPage.header).isVisible()
        assertThat(searchSpacesPage.switchInfoBox).not().isVisible()
        assertThat(searchSpacesPage.switchGoBackButton).not().isVisible()
    }

    @Test
    fun `switching a slip space should have the boat type and boat length and width filters pre-filled from existing boat`() {
        mockTimeProvider(timeProvider, startOfSlipSwitchPeriodForEspooCitizen)
        CitizenHomePage(page).loginAsEspooCitizenWithActiveSlipReservation()

        val citizenDetails = CitizenDetailsPage(page)
        citizenDetails.navigateToPage()

        val reservationSection = citizenDetails.getReservationSection("Haukilahti B 001")
        reservationSection.switchSpace.click()

        val searchSpacesPage = ReserveBoatSpacePage(page)
        assertThat(searchSpacesPage.header).isVisible()
        val filterSection = searchSpacesPage.getFilterSection()

        val slipFilterSection = filterSection.getSlipFilterSection()
        // User has a Ouboardmotor sized 1,2 * 4. Defined in seed
        assertThat(slipFilterSection.boatTypeSelect).hasValue("OutboardMotor")
        assertThat(slipFilterSection.widthInput).hasValue("1.2")
        assertThat(slipFilterSection.lengthInput).hasValue("4")
    }

    @Test
    fun `switching a trailer space should have the trailer length and width filters pre-filled from existing trailer`() {
        mockTimeProvider(timeProvider, startOfTrailerReservationPeriod)
        page.navigate(baseUrlWithFinnishLangParam)
        val citizenHomePage = CitizenHomePage(page)
        citizenHomePage.loginAsOliviaVirtanen()
        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        ReserveBoatSpacePage(page).reserveTrailerBoatSpace()

        val citizenDetails = CitizenDetailsPage(page)
        citizenDetails.navigateToPage()

        val reservationSection = citizenDetails.getReservationSection("Traileripaikka")
        reservationSection.switchSpace.click()

        val searchSpacesPage = ReserveBoatSpacePage(page)
        assertThat(searchSpacesPage.header).isVisible()
        val filterSection = searchSpacesPage.getFilterSection()

        val trailerFilterSection = filterSection.getTrailerFilterSection()
        assertThat(trailerFilterSection.widthInput).hasValue("1")
        assertThat(trailerFilterSection.lengthInput).hasValue("3")
    }

    @Test
    fun `switching a storage space should have the space length and width filters pre-filled from existing space`() {
        val reservationPage = ReserveBoatSpacePage(page)
        val filterSection = reservationPage.getFilterSection()
        val storageFilterSection = filterSection.getStorageFilterSection()
        reservationPage.navigateToPage()
        reservationPage.reserveStorageWithTrailerType(filterSection, storageFilterSection)

        val citizenDetails = CitizenDetailsPage(page)
        citizenDetails.navigateToPage()

        val reservationSection = citizenDetails.getReservationSection("Säilytyspaikka Ämmäsmäessä")
        reservationSection.switchSpace.click()

        val searchSpacesPage = ReserveBoatSpacePage(page)
        assertThat(searchSpacesPage.header).isVisible()

        // the reserved space is defined in seed and is sized 2.5 * 4.5 m
        assertThat(storageFilterSection.widthInput).hasValue("2.5")
        assertThat(storageFilterSection.lengthInput).hasValue("4.5")
    }

    private fun switchSlipBoatSpace(
        citizenSsn: String,
        width: String,
        length: String,
        expectedHarbors: Int,
        cancelAtFirst: Boolean = true
    ): ReserveBoatSpacePage {
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
