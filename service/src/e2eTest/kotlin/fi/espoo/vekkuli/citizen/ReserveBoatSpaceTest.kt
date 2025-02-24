package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.ReserveTest
import fi.espoo.vekkuli.domain.PaymentStatus
import fi.espoo.vekkuli.pages.citizen.*
import fi.espoo.vekkuli.pages.citizen.CitizenDetailsPage
import fi.espoo.vekkuli.pages.citizen.ReserveBoatSpacePage
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ActiveProfiles("test")
class ReserveBoatSpaceTest : ReserveTest() {
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
    fun `reserving a second boat space slip as a Espoo citizen`() {
        try {
            CitizenHomePage(page).loginAsLeoKorhonen()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()
            reservationPage.startReservingBoatSpaceB314()

            val reserveModal = reservationPage.getReserveModal()
            assertThat(reserveModal.root).isVisible()
            assertThat(reserveModal.firstSwitchReservationButton).isVisible()
            assertThat(reserveModal.reserveANewSpace).isVisible()
            reserveModal.reserveANewSpace.click()

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
            assertThat(formPage.getByDataTestId("reservation-validity")).hasText("31.12.2024 asti")

            formPage.submitButton.click()

            // assert that payment title is shown
            val paymentPage = PaymentPage(page)
            paymentPage.assertOnPaymentPage()
            assertZeroEmailsSent()

            // Cancel the payment at first
            paymentPage.nordeaFailedButton.click()
            // Then go through the payment
            paymentPage.nordeaSuccessButton.click()
            // Now we should be on the confirmation page
            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
            assertThat(confirmationPage.getByDataTestId("reservation-validity")).hasText("31.12.2024 asti")
            assertEmailIsSentOfCitizensFixedTermSlipReservation()
            assertCorrectPaymentForReserver("korhonen", PaymentStatus.Success, "Haukilahti B 314", "418,00", "")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving an all year storage for buck places as Espoo citizen, the reservation is indefinite`() {
        try {
            mockTimeProvider(timeProvider, startOfStorageReservationPeriodForOthers)
            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsLeoKorhonen()
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

            reservationPage.getSearchResultsSection().firstReserveButton.click()

            val form = BoatSpaceFormPage(page)
            form.fillFormAndSubmit {
                assertThat(form.getReservedSpaceSection().storageTypeField).hasText("Pukkisäilytys")
                getWinterStorageTypeSection().buckWithTentStorageTypeRadio.click()
                assertThat(form.getReservedSpaceSection().storageTypeField).hasText("Pukkisäilytys suojateltalla")
                assertThat(form.getByDataTestId("reservation-validity")).hasText("Toistaiseksi, jatko vuosittain")
            }
            assertZeroEmailsSent()

            PaymentPage(page).payReservation()
            assertThat(PaymentPage(page).reservationSuccessNotification).isVisible()

            assertEmailIsSentOfCitizensStorageSpaceReservation()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservation = citizenDetailsPage.getReservationSection(1)
            assertEquals("Maksettu 15.09.2024", reservation.paymentStatus.textContent())
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving an all year storage for buck places as non-Espoo citizen, reservation should be indefinite`() {
        try {
            mockTimeProvider(timeProvider, startOfStorageReservationPeriod)
            val citizenHomePage = CitizenHomePage(page)
            citizenHomePage.loginAsNonEspooCitizenMarko()
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

            reservationPage.getSearchResultsSection().firstReserveButton.click()
            val form = BoatSpaceFormPage(page)
            form.fillFormAndSubmit {
                assertThat(form.getReservedSpaceSection().storageTypeField).hasText("Pukkisäilytys")
                getWinterStorageTypeSection().buckWithTentStorageTypeRadio.click()
                assertThat(form.getReservedSpaceSection().storageTypeField).hasText("Pukkisäilytys suojateltalla")
                assertThat(form.getByDataTestId("reservation-validity")).hasText("Toistaiseksi, jatko vuosittain")
            }
            assertZeroEmailsSent()
            PaymentPage(page).payReservation()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
            assertThat(confirmationPage.getByDataTestId("reservation-validity")).hasText("Toistaiseksi, jatko vuosittain")

            assertEmailIsSentOfCitizensStorageSpaceReservation()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving an all year storage for trailer places for Espoo citizen should result in indefinite reservation`() {
        try {
            mockTimeProvider(timeProvider, startOfStorageReservationPeriod)

            val reservationPage = ReserveBoatSpacePage(page)
            val filterSection = reservationPage.getFilterSection()
            val storageFilterSection = filterSection.getStorageFilterSection()

            reservationPage.reserveStorageWithTrailerType(filterSection, storageFilterSection)

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
            assertThat(confirmationPage.getByDataTestId("reservation-validity")).hasText("Toistaiseksi, jatko vuosittain")
            assertEmailIsSentOfCitizensStorageSpaceReservation()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving a trailer space as a Espoo citizen should result in indefinite reservation`() {
        try {
            mockTimeProvider(timeProvider, startOfTrailerReservationPeriod)

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

            val trailerSection = formPage.getTrailerStorageTypeSection()
            trailerSection.trailerRegistrationNumberInput.fill("RGST1234")
            val citizenSection = formPage.getCitizenSection()
            citizenSection.emailInput.fill("test@example.com")
            assertThat(citizenSection.emailError).isHidden()

            citizenSection.phoneInput.fill("123456789")
            assertThat(citizenSection.phoneError).isHidden()

            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()
            assertThat(formPage.getByDataTestId("reservation-validity")).hasText("Toistaiseksi, jatko vuosittain")
            formPage.submitButton.click()
            assertZeroEmailsSent()
            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
            assertThat(confirmationPage.getByDataTestId("reservation-validity")).hasText("Toistaiseksi, jatko vuosittain")

            assertEmailIsSentOfCitizensIndefiniteTrailerReservation()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving a trailer space as a non-Espoo citizen should result in fixed term reservation`() {
        try {
            val fixedDateEndDate = "30.04.2025"
            mockTimeProvider(timeProvider, startOfTrailerReservationPeriod)

            CitizenHomePage(page).loginAsNonEspooCitizenMarko()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()
            reservationPage.startReservingBoatSpace012()

            val formPage = BoatSpaceFormPage(page)
            val boatSection = formPage.getBoatSection()
            val userAgreementSection = formPage.getUserAgreementSection()

            // Fill in the boat information
            boatSection.typeSelect.selectOption("OutboardMotor")
            boatSection.nameInput.fill("My Boat")
            boatSection.lengthInput.fill("3")
            boatSection.widthInput.fill("2.5")
            boatSection.depthInput.fill("1.5")
            boatSection.weightInput.fill("2000")
            boatSection.otherIdentifierInput.fill("ID12345")
            boatSection.noRegistrationCheckbox.check()
            boatSection.ownerRadio.click()

            val trailerSection = formPage.getTrailerStorageTypeSection()
            trailerSection.trailerRegistrationNumberInput.fill("RGST1234")
            val citizenSection = formPage.getCitizenSection()
            citizenSection.emailInput.fill("test@example.com")
            assertThat(citizenSection.emailError).isHidden()

            citizenSection.phoneInput.fill("123456789")
            assertThat(citizenSection.phoneError).isHidden()

            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()

            assertThat(formPage.getByDataTestId("reservation-validity")).hasText("$fixedDateEndDate asti")
            formPage.submitButton.click()
            assertZeroEmailsSent()
            val paymentPage = PaymentPage(page)
            paymentPage.nordeaSuccessButton.click()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
            assertThat(confirmationPage.getByDataTestId("reservation-validity")).hasText("$fixedDateEndDate asti")
            assertEmailIsSentOfCitizensFixedTermTrailerReservation(endDate = fixedDateEndDate)

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservation = citizenDetailsPage.getReservationSection(0)
            assertEquals("Maksettu 01.05.2024", reservation.paymentStatus.textContent())
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving a winter storage boat space as Espoo citizen should result in indefinite reservation`() {
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

            assertThat(formPage.getByDataTestId("reservation-validity")).hasText("Toistaiseksi, jatko vuosittain")
            formPage.submitButton.click()

            assertZeroEmailsSent()

            // assert that payment title is shown
            val paymentPage = PaymentPage(page)
            // Cancel the payment at first
            paymentPage.nordeaFailedButton.click()
            // Then go through the payment
            paymentPage.nordeaSuccessButton.click()
            // Now we should be on the confirmation page
            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
            assertThat(confirmationPage.getByDataTestId("reservation-validity")).hasText("Toistaiseksi, jatko vuosittain")

            val citizenDetailPage = CitizenDetailsPage(page)
            citizenDetailPage.navigateToPage()

            val reservationSection = citizenDetailPage.getReservationSection("Talvipaikka: Haukilahti B 013")
            val trailerSection = reservationSection.getTrailerSection()

            page.waitForCondition { trailerSection.widthField.isVisible }
            assertThat(trailerSection.widthField).containsText("1,50")
            assertThat(trailerSection.lengthField).containsText("2,50")
            assertThat(trailerSection.registrationCodeField).containsText(trailerRegistrationCode)

            assertEmailIsSentOfCitizensWinterSpaceReservation()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservation = citizenDetailsPage.getReservationSection(1)
            assertEquals("Maksettu 15.09.2024", reservation.paymentStatus.textContent())
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun asEspooCitizenReservingASecondBoatSpaceForOrganization() {
        try {
            CitizenHomePage(page).loginAsOliviaVirtanen()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()
            reservationPage.startReservingBoatSpaceB314()

            val reserveModal = reservationPage.getReserveModal()
            assertThat(reserveModal.root).isVisible()
            assertThat(reserveModal.firstSwitchReservationButton).isVisible()
            assertThat(reserveModal.reserveANewSpace).isVisible()
            reserveModal.reserveANewSpace.click()

            val formPage = BoatSpaceFormPage(page)
            val organizationSection = formPage.getOrganizationSection()

            assertThat(page.getByText("Olivian vene")).isVisible()
            organizationSection.reserveForOrganization.click()
            organizationSection.organization("Espoon Pursiseura").click()

            organizationSection.phoneNumberInput.fill("123456789")
            organizationSection.emailInput.fill("foo@bar.com")

            val boatSection = formPage.getBoatSection()
            assertThat(page.getByText("Espoon lohi")).isVisible()
            assertThat(page.getByText("Espoon kuha")).isVisible()
            assertThat(page.getByText("Olivian vene")).isHidden()

            page.getByText("Espoon lohi").click()
            assertThat(boatSection.nameInput).hasValue("Espoon lohi")
            assertThat(boatSection.lengthInput).hasValue("4")

            boatSection.newBoatSelection.click()

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

            assertThat(formPage.getByDataTestId("reservation-validity")).hasText("31.12.2024 asti")

            formPage.submitButton.click()
            assertZeroEmailsSent()

            // assert that payment page is shown
            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.paymentProviders).isVisible()
            paymentPage.nordeaSuccessButton.click()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
            assertThat(formPage.getByDataTestId("reservation-validity")).hasText("31.12.2024 asti")
            messageService.sendScheduledEmails()
            assertEquals(2, SendEmailServiceMock.emails.size)

            // Email to citizen
            assertEmailIsSentOfCitizensFixedTermSlipReservation(sendAndAssertSendCount = false)

            // Email to organization
            assertEmailIsSentOfCitizensFixedTermSlipReservation(emailAddress = "foo@bar.com", sendAndAssertSendCount = false)

            assertCorrectPaymentForReserver("Espoon Pursiseura", PaymentStatus.Success, "Haukilahti B 314", "418,00", "")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Organization cannot be selected on the reservation form if it's reservations are full`() {
        try {
            CitizenHomePage(page).loginAsOliviaVirtanen()
            // Olivia has already reserved 2 boat spaces, so we need to terminate a reservation
            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val firstReservationSection = citizenDetailsPage.getFirstReservationSection()
            firstReservationSection.terminateButton.click()

            val terminateReservationModal = citizenDetailsPage.getTerminateReservationModal()
            terminateReservationModal.confirmButton.click()
            assertThat(terminateReservationModal.confirmButton).isHidden()

            // Reserve a second boat space for the organization
            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()
            reservationPage.startReservingBoatSpaceB314()

            val formPage = BoatSpaceFormPage(page)
            val organizationSection = formPage.getOrganizationSection()

            organizationSection.reserveForOrganization.click()
            organizationSection.organization("Espoon Pursiseura").click()
            page.getByText("Espoon lohi").click()

            val citizenSection = formPage.getCitizenSection()
            citizenSection.emailInput.fill("test@example.com")
            citizenSection.phoneInput.fill("123456789")

            val userAgreementSection = formPage.getUserAgreementSection()
            userAgreementSection.certifyInfoCheckbox.check()
            userAgreementSection.agreementCheckbox.check()

            formPage.submitButton.click()

            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.paymentProviders).isVisible()
            paymentPage.nordeaSuccessButton.click()
            assertThat(paymentPage.reservationSuccessNotification).isVisible()

            // Go to the form, assert that organization cannot be selected
            reservationPage.navigateToPage()
            reservationPage.filterForBoatSpaceB314()
            reservationPage.getSearchResultsSection().firstReserveButton.click()

            assertThat(organizationSection.reserveForOrganization).isHidden()
            assertThat(page.getByText("Espoon pursiseura")).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Citizen cannot be selected on the reservation form if it's reservations are full`() {
        try {
            CitizenHomePage(page).loginAsOliviaVirtanen()
            // Reserve a second boat space for the organization
            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateToPage()
            reservationPage.startReservingBoatSpaceB314()
            reservationPage.getReserveModal().reserveANewSpace.click()

            val formPage = BoatSpaceFormPage(page)

            formPage.fillFormAndSubmit()

            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.paymentProviders).isVisible()
            paymentPage.nordeaSuccessButton.click()
            assertThat(paymentPage.reservationSuccessNotification).isVisible()

            // Go to the form, assert that citizen cannot be selected
            reservationPage.navigateToPage()
            reservationPage.filterForBoatSpaceB314()
            reservationPage.getSearchResultsSection().firstReserveButton.click()
            reservationPage.getReserveModal().reserveANewSpace.click()

            val organizationSection = formPage.getOrganizationSection()
            assertThat(organizationSection.reserveForOrganization).isVisible()
            assertThat(page.getByText("Espoon lohi")).isVisible()
            assertThat(page.getByText("Espoon kuha")).isVisible()
            assertThat(page.getByText("Olivian vene")).isHidden()
            assertThat(organizationSection.reserveForCitizen).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving a boat space slip as a citizen with a 50 percent discount should halve the total price`() {
        val reserverName = "Virtanen"
        val discount = 50
        val boatSpacePrice = "418,00"
        val expectedPrice = "209,00"
        try {
            setDiscountForReserver(page, reserverName, discount)
            val formPage = fillReservationInfoAndAssertCorrectDiscount(discount, expectedPrice, boatSpacePrice)
            val submitButton = formPage.submitButton
            submitButton.click()

            // asserting that email is not sent too early, only after payment has been received
            assertZeroEmailsSent()

            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.getByDataTestId("payment-page")).isVisible()
            paymentPage.nordeaSuccessButton.click()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()

            val paymentReservedSpaceSection = confirmationPage.getReservedSpaceSection()
            assertThat(
                paymentReservedSpaceSection.fields.getField("Hinta").last()
            ).containsText("Yhteensä: $boatSpacePrice €")

            val paymentDiscountText = paymentPage.getByDataTestId("reservation-info-text")
            assertThat(paymentDiscountText).containsText("$discount %")
            assertThat(paymentDiscountText).containsText("$expectedPrice €")

            // asserting that only one email is sent after payment
            assertEmailIsSentOfCitizensIndefiniteSlipReservation()

            assertCorrectPaymentForReserver(
                reserverName,
                PaymentStatus.Success,
                "Haukilahti B 314",
                expectedPrice,
                "Hinnassa huomioitu $discount% alennus.",
                doLogin = false
            )

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservation = citizenDetailsPage.getReservationSection(2)
            assertEquals("Maksettu 01.04.2024", reservation.paymentStatus.textContent())
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reserving a boat space slip as a citizen with a 100 percent discount should not require payment`() {
        val reserverName = "Virtanen"
        val discount = 100
        val expectedPrice = "0,00"
        val boatSpacePrice = "418,00"
        try {
            setDiscountForReserver(page, reserverName, discount)
            val formPage = fillReservationInfoAndAssertCorrectDiscount(discount, expectedPrice, boatSpacePrice)
            val confirmButton = formPage.confirmButton
            confirmButton.click()

            assertZeroEmailsSent()

            val paymentPage = PaymentPage(page)
            assertThat(paymentPage.getByDataTestId("payment-page")).not().isVisible()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
            val paymentReservedSpaceSection = confirmationPage.getReservedSpaceSection()
            assertThat(
                paymentReservedSpaceSection.fields.getField("Hinta").last()
            ).containsText("Yhteensä: $boatSpacePrice €")

            val paymentDiscountText = confirmationPage.getByDataTestId("reservation-info-text")
            assertThat(paymentDiscountText).containsText("$discount %")
            assertThat(paymentDiscountText).containsText("$expectedPrice €")

            // asserting that email is sent when there is no payment
            assertEmailIsSentOfCitizensIndefiniteSlipReservation()

            assertCorrectPaymentForReserver(
                reserverName,
                PaymentStatus.Success,
                "Haukilahti B 314",
                expectedPrice,
                "Hinnassa huomioitu $discount% alennus.",
                doLogin = false
            )

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservation = citizenDetailsPage.getReservationSection(2)
            assertEquals("Maksettu 01.04.2024", reservation.paymentStatus.textContent())
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun discountIsAppliedFromSelectedOrganizationReservingASecondPlace() {
        val citizenDiscount = 50
        val organizationDiscount = 100
        val boatSpacePrice = "418,00"
        val expectedPriceForCitizen = "209,00"
        val expectedPriceForOrganization = "0,00"
        try {
            setDiscountForReserver(page, "Virtanen", citizenDiscount)
            setDiscountForReserver(page, "Espoon Pursiseura", organizationDiscount, false)

            val formPage =
                fillReservationInfoAndAssertCorrectDiscount(citizenDiscount, expectedPriceForCitizen, boatSpacePrice)

            val organizationSection = formPage.getOrganizationSection()
            organizationSection.reserveForOrganization.click()
            organizationSection.organization("Espoon Pursiseura").click()

            val discountText = formPage.getByDataTestId("reservation-info-text")
            assertThat(discountText).containsText("$organizationDiscount %")
            assertThat(discountText).containsText("$expectedPriceForOrganization €")

            organizationSection.phoneNumberInput.fill("123456789")
            organizationSection.emailInput.fill("foo@bar.com")

            page.getByText("Espoon lohi").click()

            // Reserving for organization with 100% discount
            val confirmButton = formPage.confirmButton
            confirmButton.click()
            assertZeroEmailsSent()

            val confirmationPage = ConfirmationPage(page)
            assertThat(confirmationPage.reservationSuccessNotification).isVisible()
            val paymentReservedSpaceSection = confirmationPage.getReservedSpaceSection()
            assertThat(
                paymentReservedSpaceSection.fields.getField("Hinta").last()
            ).containsText("Yhteensä: $boatSpacePrice €")

            val paymentDiscountText = confirmationPage.getByDataTestId("reservation-info-text")
            assertThat(paymentDiscountText).containsText("$organizationDiscount %")
            assertThat(paymentDiscountText).containsText("$expectedPriceForOrganization €")

            assertCorrectPaymentForReserver(
                "Espoon Pursiseura",
                PaymentStatus.Success,
                "Haukilahti B 314",
                expectedPriceForOrganization,
                "Hinnassa huomioitu $organizationDiscount% alennus.",
                doLogin = false
            )

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()
            val reservation = citizenDetailsPage.getReservationSection(0)
            assertEquals("-", reservation.paymentStatus.textContent())
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

        val reserveModal = reservationPage.getReserveModal()
        assertThat(reserveModal.root).isVisible()
        assertThat(reserveModal.firstSwitchReservationButton).isVisible()
        assertThat(reserveModal.reserveANewSpace).isVisible()
        reserveModal.reserveANewSpace.click()

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
    fun formValuesArePreservedAfterPaymentPageBackButton() {
        CitizenHomePage(page).loginAsMikkoVirtanen()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingBoatSpaceB314()

        val boatSpaceFormPage = BoatSpaceFormPage(page)
        boatSpaceFormPage.fillFormAndSubmit {
            val boatSection = getBoatSection()
            boatSection.widthInput.fill("3")
            boatSection.lengthInput.fill("6")
        }

        PaymentPage(page).backButton.click()

        // assert that form is filled with the previous values
        val boatSection = boatSpaceFormPage.getBoatSection()
        assertThat(boatSection.widthInput).hasValue("3")
        assertThat(boatSection.lengthInput).hasValue("6")
    }

    @Test
    fun paymentSuccess() {
        // login and pick first free space
        CitizenHomePage(page).loginAsOliviaVirtanen()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingBoatSpaceB314()

        val reserveModal = reservationPage.getReserveModal()
        assertThat(reserveModal.root).isVisible()
        assertThat(reserveModal.firstSwitchReservationButton).isVisible()
        assertThat(reserveModal.reserveANewSpace).isVisible()
        reserveModal.reserveANewSpace.click()

        val formPage = BoatSpaceFormPage(page)
        formPage.fillFormAndSubmit()

        val paymentPage = PaymentPage(page)
        paymentPage.nordeaSuccessButton.click()

        val confirmationPage = ConfirmationPage(page)
        assertThat(confirmationPage.reservationSuccessNotification).isVisible()
    }

    @Test
    fun paymentFailed() {
        // login and pick first free space
        CitizenHomePage(page).loginAsOliviaVirtanen()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingBoatSpaceB314()

        val reserveModal = reservationPage.getReserveModal()
        assertThat(reserveModal.root).isVisible()
        assertThat(reserveModal.firstSwitchReservationButton).isVisible()
        assertThat(reserveModal.reserveANewSpace).isVisible()
        reserveModal.reserveANewSpace.click()

        val formPage = BoatSpaceFormPage(page)
        formPage.fillFormAndSubmit()

        val paymentPage = PaymentPage(page)
        paymentPage.nordeaFailedButton.click()
        assertThat(paymentPage.paymentFailedNotification).isVisible()

        paymentPage.backButton.click()
        formPage.cancelButton.click()

        val confirmCancelReservationModal = formPage.getConfirmCancelReservationModal()
        assertThat(confirmCancelReservationModal.root).isVisible()
        confirmCancelReservationModal.confirmButton.click()

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

    @Test
    fun `Payment page displays remaining reservation time`() {
        CitizenHomePage(page).loginAsEspooCitizenWithoutReservations()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingBoatSpaceB314()
        BoatSpaceFormPage(page).fillFormAndSubmit()

        val paymentPage = PaymentPage(page)
        assertThat(paymentPage.header).isVisible()

        val reservationTimerSection = paymentPage.getReservationTimerSection()
        assertThat(reservationTimerSection.root).isVisible()
    }

    @Test
    fun `Payment after reservation time expiry results in valid reservation if space is still available`() {
        val currentTime = timeProvider.getCurrentDateTime()
        val reservationTimerExpired = currentTime.plus(moreThanSessionDuration)

        CitizenHomePage(page).loginAsEspooCitizenWithoutReservations()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingBoatSpaceB314()
        BoatSpaceFormPage(page).fillFormAndSubmit()

        val paymentPage = PaymentPage(page)
        assertThat(paymentPage.header).isVisible()

        mockTimeProvider(timeProvider, reservationTimerExpired)

        paymentPage.payReservation()
        assertThat(paymentPage.reservationSuccessNotification).isVisible()
        assertCorrectPaymentForReserver("Virtanen Mikko", PaymentStatus.Success, "Haukilahti B 314", "418,00", "")

        val citizenDetailPage = CitizenDetailsPage(page)
        citizenDetailPage.navigateToPage()

        val reservationSection = citizenDetailPage.getReservationSection("Haukilahti B 314")
        assertThat(reservationSection.root).isVisible()
    }

    @Test
    fun `Payment after reservation time expiry fails if space is not available`() {
        val currentTime = timeProvider.getCurrentDateTime()
        val reservationTimerExpired = currentTime.plus(moreThanSessionDuration)

        val mikkoPage = page

        // start reservation as Mikko
        CitizenHomePage(mikkoPage).loginAsMikkoVirtanen()

        val mikkoReservationPage = ReserveBoatSpacePage(mikkoPage)
        mikkoReservationPage.navigateToPage()
        mikkoReservationPage.startReservingBoatSpaceB314()
        BoatSpaceFormPage(mikkoPage).fillFormAndSubmit()

        val mikkoPaymentPage = PaymentPage(mikkoPage)
        assertThat(mikkoPaymentPage.header).isVisible()

        mockTimeProvider(timeProvider, reservationTimerExpired)

        // start reservation as Olivia
        browser.newContext().use { oliviaContext ->
            val oliviaPage = oliviaContext.newPage()
            CitizenHomePage(oliviaPage).loginAsOliviaVirtanen()

            val oliviaReservationPage = ReserveBoatSpacePage(oliviaPage)
            oliviaReservationPage.navigateToPage()
            oliviaReservationPage.startReservingBoatSpaceB314()

            val oliviaReserveModal = oliviaReservationPage.getReserveModal()
            oliviaReserveModal.reserveANewSpace.click()
            assertThat(BoatSpaceFormPage(oliviaPage).header).isVisible()
        }

        // pay reservation as mikko
        mikkoPaymentPage.payReservation()
        assertThat(mikkoPaymentPage.reservationFailedNotification).isVisible()
        assertCorrectPaymentForReserver("Virtanen Mikko", PaymentStatus.Success, "Haukilahti B 314", "418,00", "", filterReservations = {
            reservationStateFilter("PAYMENT").click()
        })

        val citizenDetailPage = CitizenDetailsPage(page)
        citizenDetailPage.navigateToPage()

        val reservationSection = citizenDetailPage.getReservationSection("Haukilahti B 314")
        assertThat(reservationSection.root).not().isVisible()
    }

    @Test
    fun `Deleted citizen boats are not available in reservation form`() {
        CitizenHomePage(page).loginAsLeoKorhonen()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        val boatSpaceFormPage = BoatSpaceFormPage(page)
        val boatSection = boatSpaceFormPage.getBoatSection()
        val citizenDetailsPage = CitizenDetailsPage(page)

        // check boat is available in reservation form
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()
        reserveBoatSpacePage.getReserveModal().reserveANewSpace.click()
        assertThat(boatSection.existingBoat("Leon vene")).isVisible()
        assertThat(boatSection.existingBoat("Leon toinen liian iso vene")).isVisible()

        boatSpaceFormPage.cancelButton.click()
        boatSpaceFormPage.getConfirmCancelReservationModal().confirmButton.click()

        // delete boat
        citizenDetailsPage.navigateToPage()
        citizenDetailsPage.showAllBoatsButton.click()
        citizenDetailsPage.getBoatSection("Leon toinen liian iso vene").deleteButton.click()
        citizenDetailsPage.getDeleteBoatModal().confirmButton.click()
        assertThat(citizenDetailsPage.getDeleteBoatSuccessModal().root).isVisible()

        // check boat is not available in reservation form
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()
        reserveBoatSpacePage.getReserveModal().reserveANewSpace.click()
        assertThat(boatSection.existingBoat("Leon vene")).isVisible()
        assertThat(boatSection.existingBoat("Leon toinen liian iso vene")).not().isVisible()
    }

    @Test
    fun `Deleted organization boats are not available in reservation form`() {
        CitizenHomePage(page).loginAsEspooCitizenWithActiveOrganization()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        val boatSpaceFormPage = BoatSpaceFormPage(page)
        val boatSection = boatSpaceFormPage.getBoatSection()
        val citizenDetailsPage = CitizenDetailsPage(page)
        val organizationDetailsPage = OrganizationDetailsPage(page)

        // check boat is available in reservation form
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()
        reserveBoatSpacePage.getReserveModal().getSwitchReservationButton("Haukilahti B 005").click()
        assertThat(boatSection.existingBoat("Espoon lohi")).isVisible()
        assertThat(boatSection.existingBoat("Espoon kuha")).isVisible()

        boatSpaceFormPage.cancelButton.click()
        boatSpaceFormPage.getConfirmCancelReservationModal().confirmButton.click()

        // delete boat
        citizenDetailsPage.navigateToPage()
        citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()
        organizationDetailsPage.showAllBoatsButton.click()
        organizationDetailsPage.getBoatSection("Espoon kuha").deleteButton.click()
        organizationDetailsPage.getDeleteBoatModal().confirmButton.click()
        assertThat(organizationDetailsPage.getDeleteBoatSuccessModal().root).isVisible()

        // check boat is not available in reservation form
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()
        reserveBoatSpacePage.getReserveModal().getSwitchReservationButton("Haukilahti B 005").click()
        assertThat(boatSection.existingBoat("Espoon lohi")).isVisible()
        assertThat(boatSection.existingBoat("Espoon kuha")).not().isVisible()
    }

    @Test
    fun `Citizen with trailer space should be allowed to reserve trailer space for organization`() {
        mockTimeProvider(timeProvider, startOfTrailerReservationPeriod)

        CitizenHomePage(page).loginAsEspooCitizenWithActiveOrganization()

        // reserve for citizen
        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpace012()
        BoatSpaceFormPage(page).fillFormAndSubmit {
            getOrganizationSection().reserveForCitizen.click()
            getTrailerStorageTypeSection().trailerRegistrationNumberInput.fill("ABC-111")
        }
        PaymentPage(page).payReservation()

        // reserve for organization
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.getSearchResultsSection().reserveButtonByPlace("TRAILERI", "013").click()
        reserveBoatSpacePage.getReserveModal().reserveANewSpace.click()

        BoatSpaceFormPage(page).fillFormAndSubmit {
            getOrganizationSection().reserveForOrganization.click()
            getOrganizationSection().organization("Espoon pursiseura").click()
            getTrailerStorageTypeSection().trailerRegistrationNumberInput.fill("ABC-222")
        }
        PaymentPage(page).payReservation()

        // check citizen has reservation
        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()
        assertThat(citizenDetailsPage.getReservationSection("TRAILERI 012").root).isVisible()

        // check organization has reservation
        citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()
        val organizationDetailsPage = OrganizationDetailsPage(page)
        assertThat(organizationDetailsPage.getReservationSection("TRAILERI 013").root).isVisible()
    }

    private fun fillReservationInfoAndAssertCorrectDiscount(
        discount: Int,
        expectedPrice: String,
        boatSpacePrice: String
    ): BoatSpaceFormPage {
        CitizenHomePage(page).loginAsOliviaVirtanen()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingBoatSpaceB314()

        val reserveModal = reservationPage.getReserveModal()
        assertThat(reserveModal.root).isVisible()
        assertThat(reserveModal.firstSwitchReservationButton).isVisible()
        assertThat(reserveModal.reserveANewSpace).isVisible()
        reserveModal.reserveANewSpace.click()

        val formPage = BoatSpaceFormPage(page)

        val boatSection = formPage.getBoatSection()
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
        citizenSection.phoneInput.fill("123456789")

        val userAgreementSection = formPage.getUserAgreementSection()
        userAgreementSection.certifyInfoCheckbox.check()
        userAgreementSection.agreementCheckbox.check()

        val reservedSpaceSection = formPage.getReservedSpaceSection()
        assertThat(reservedSpaceSection.fields.getField("Hinta").last()).containsText("Yhteensä: $boatSpacePrice €")
        val discountText = formPage.getByDataTestId("reservation-info-text")
        assertThat(discountText).containsText("$discount %")
        assertThat(discountText).containsText("$expectedPrice €")
        return formPage
    }

    @Test
    fun `Search values are not carried over to the form for winter reservations`() {
        CitizenHomePage(page).loginAsOliviaVirtanen()

        mockTimeProvider(timeProvider, startOfWinterReservationPeriod)
        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingWinterBoatSpaceB013()
        reservationPage.getReserveModal().reserveANewSpace.click()

        val formPage = BoatSpaceFormPage(page)
        val winterStorageType = formPage.getWinterStorageTypeSection()
        assertThat(winterStorageType.trailerLengthInput).hasValue("")
        assertThat(winterStorageType.trailerWidthInput).hasValue("")
    }

    @Test
    fun `Boat spaces are ordered correctly by scandic alphabet`() {
        CitizenHomePage(page).loginAsEspooCitizenWithoutReservations()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.filterForSlipBoatSpaceB001AndÄ001()
        val searchResults = reserveBoatSpacePage.getSearchResultsSection()

        // wait for the results to be loaded before checking the order
        assertThat(searchResults.firstReserveButton).isVisible()
        val b001Index = searchResults.getReserveButtonPlacementByPlace("Svinö", "B 001")
        val ä001Index = searchResults.getReserveButtonPlacementByPlace("Svinö", "Ä 001")

        assertTrue(b001Index < ä001Index, "B 001 should be before Ä 001")
    }

    @Test
    fun `Buoy boat spaces are ordered last and do not display size`() {
        CitizenHomePage(page).loginAsEspooCitizenWithoutReservations()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        val filterSection = reserveBoatSpacePage.getFilterSection()
        val slipFilterSection = filterSection.getSlipFilterSection()
        val searchResultsSection = reserveBoatSpacePage.getSearchResultsSection()

        reserveBoatSpacePage.navigateToPage()
        filterSection.slipRadio.click()
        slipFilterSection.boatTypeSelect.selectOption("Sailboat")
        slipFilterSection.widthInput.fill("4")
        slipFilterSection.lengthInput.fill("14")
        slipFilterSection.suomenojaCheckbox.click()

        searchResultsSection.showMoreToggle("Suomenoja").click()

        val buoySpaceRowIndex = searchResultsSection.getReserveButtonPlacementByPlace("Suomenoja", "POIJU 001")
        val referenceSpaceRowIndex = searchResultsSection.getReserveButtonPlacementByPlace("Suomenoja", "G 117")

        assertTrue(referenceSpaceRowIndex < buoySpaceRowIndex, "Buoy boat spaces should be ordered last")

        val buoySpaceRow = searchResultsSection.reserveRowByPlace("POIJU", "001")
        assertThat(buoySpaceRow).not().containsText("1 000,00 x 1 000,00 m")
    }

    @Test
    fun `For a slip, a warning is shown if the boat size is not within the limits`() {
        CitizenHomePage(page).loginAsEspooCitizenWithoutReservations()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingBoatSpaceB314()
        val formPage = BoatSpaceFormPage(page)
        val boatSection = formPage.getBoatSection()

        boatSection.weightInput.fill("15000")
        assertThat(boatSection.boatWeightWarning).isHidden()
        boatSection.weightInput.fill("15001")
        assertThat(boatSection.boatWeightWarning).isVisible()
        boatSection.weightInput.fill("100")
        assertThat(boatSection.boatWeightWarning).isHidden()

        assertThat(boatSection.boatSizeWarning).isHidden()
        boatSection.lengthInput.fill("7.8")
        assertThat(boatSection.boatSizeWarning).isHidden()
        boatSection.lengthInput.fill("7.81")
        assertThat(boatSection.boatSizeWarning).isVisible()
        boatSection.lengthInput.fill("5")
        assertThat(boatSection.boatSizeWarning).isHidden()

        boatSection.widthInput.fill("3.1")
        assertThat(boatSection.boatSizeWarning).isHidden()
        boatSection.widthInput.fill("3.11")
        assertThat(boatSection.boatSizeWarning).isVisible()
        boatSection.widthInput.fill("3")
        assertThat(boatSection.boatSizeWarning).isHidden()

        boatSection.widthInput.fill("10")
        boatSection.boatSizeWarningBackButton.click()
        val confirmCancelReservationModal = formPage.getConfirmCancelReservationModal()
        assertThat(confirmCancelReservationModal.root).isVisible()
        confirmCancelReservationModal.confirmButton.click()

        assertThat(reservationPage.header).isVisible()
    }

    @Test
    fun `For a trailer, a warning is shown for if the boat size is not within the limits`() {
        mockTimeProvider(timeProvider, startOfTrailerReservationPeriod)
        CitizenHomePage(page).loginAsEspooCitizenWithoutReservations()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingBoatSpace012()
        val formPage = BoatSpaceFormPage(page)
        val boatSection = formPage.getBoatSection()

        assertThat(boatSection.boatSizeWarning).isHidden()
        boatSection.lengthInput.fill("7")
        assertThat(boatSection.boatSizeWarning).isHidden()
        boatSection.lengthInput.fill("7.01")
        assertThat(boatSection.boatSizeWarning).isVisible()
        boatSection.lengthInput.fill("6")
        assertThat(boatSection.boatSizeWarning).isHidden()

        boatSection.widthInput.fill("2.6")
        assertThat(boatSection.boatSizeWarning).isHidden()
        boatSection.widthInput.fill("2.61")
        assertThat(boatSection.boatSizeWarning).isVisible()
        boatSection.widthInput.fill("2")
        assertThat(boatSection.boatSizeWarning).isHidden()

        // TODO: Implement warnings for the trailer's size

        boatSection.widthInput.fill("10")
        boatSection.boatSizeWarningBackButton.click()
        val confirmCancelReservationModal = formPage.getConfirmCancelReservationModal()
        assertThat(confirmCancelReservationModal.root).isVisible()
        confirmCancelReservationModal.confirmButton.click()

        assertThat(reservationPage.header).isVisible()
    }

    @Test
    fun `For winter storage, a warning is shown for if the boat size is not within the limits`() {
        mockTimeProvider(timeProvider, startOfWinterReservationPeriod)
        CitizenHomePage(page).loginAsEspooCitizenWithoutReservations()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingWinterBoatSpaceB013()
        val formPage = BoatSpaceFormPage(page)
        val boatSection = formPage.getBoatSection()

        assertThat(boatSection.boatSizeWarning).isHidden()
        boatSection.lengthInput.fill("4.5")
        assertThat(boatSection.boatSizeWarning).isHidden()
        boatSection.lengthInput.fill("4.51")
        assertThat(boatSection.boatSizeWarning).isVisible()
        boatSection.lengthInput.fill("4")
        assertThat(boatSection.boatSizeWarning).isHidden()

        boatSection.widthInput.fill("2.5")
        assertThat(boatSection.boatSizeWarning).isHidden()
        boatSection.widthInput.fill("2.51")
        assertThat(boatSection.boatSizeWarning).isVisible()
        boatSection.widthInput.fill("2")
        assertThat(boatSection.boatSizeWarning).isHidden()

        // TODO: Implement warnings for the trailer's size

        boatSection.widthInput.fill("10")
        boatSection.boatSizeWarningBackButton.click()
        val confirmCancelReservationModal = formPage.getConfirmCancelReservationModal()
        assertThat(confirmCancelReservationModal.root).isVisible()
        confirmCancelReservationModal.confirmButton.click()

        assertThat(reservationPage.header).isVisible()
    }

    @Test
    fun `For storage, a warning is shown for if the boat size is not within the limits`() {
        CitizenHomePage(page).loginAsEspooCitizenWithoutReservations()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        val filterSection = reservationPage.getFilterSection()
        val storageFilterSection = filterSection.getStorageFilterSection()
        filterSection.storageRadio.click()

        storageFilterSection.trailerRadio.click()
        storageFilterSection.widthInput.fill("1")
        storageFilterSection.lengthInput.fill("3")

        reservationPage.getSearchResultsSection().b007ReserveButton.click()
        val formPage = BoatSpaceFormPage(page)
        val boatSection = formPage.getBoatSection()

        assertThat(boatSection.boatSizeWarning).isHidden()
        boatSection.lengthInput.fill("4.5")
        assertThat(boatSection.boatSizeWarning).isHidden()
        boatSection.lengthInput.fill("4.51")
        assertThat(boatSection.boatSizeWarning).isVisible()
        boatSection.lengthInput.fill("4")
        assertThat(boatSection.boatSizeWarning).isHidden()

        boatSection.widthInput.fill("2.5")
        assertThat(boatSection.boatSizeWarning).isHidden()
        boatSection.widthInput.fill("2.51")
        assertThat(boatSection.boatSizeWarning).isVisible()
        boatSection.widthInput.fill("2")
        assertThat(boatSection.boatSizeWarning).isHidden()

        // TODO: Implement warnings for the trailer's size

        boatSection.widthInput.fill("10")
        boatSection.boatSizeWarningBackButton.click()
        val confirmCancelReservationModal = formPage.getConfirmCancelReservationModal()
        assertThat(confirmCancelReservationModal.root).isVisible()
        confirmCancelReservationModal.confirmButton.click()

        assertThat(reservationPage.header).isVisible()
    }
}
