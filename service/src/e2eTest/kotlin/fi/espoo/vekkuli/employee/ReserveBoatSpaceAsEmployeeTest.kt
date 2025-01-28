package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.pages.employee.*
import fi.espoo.vekkuli.service.SendEmailServiceMock
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ActiveProfiles("test")
class ReserveBoatSpaceAsEmployeeTest : PlaywrightTest() {
    @Test
    fun `employee can change the language`() {
        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin("fi")
        assertThat(page.getByText("Varaukset").first()).isVisible()
        page.getByTestId("language-selection").click()
        page.getByText("English").click()
        assertThat(page.getByText("Reservations").first()).isVisible()
        page.getByTestId("language-selection").click()
        page.getByText("Svenska").click()
        assertThat(page.getByText("Reservationer").first()).isVisible()
    }

    private fun fillAndTestForm(formPage: BoatSpaceFormPage) {
        formPage.submitButton.click()

        assertThat(formPage.widthError).isHidden()
        assertThat(formPage.lengthError).isHidden()
        assertThat(formPage.depthError).isVisible()
        assertThat(formPage.weightError).isVisible()
        assertThat(formPage.boatRegistrationNumberError).isVisible()
        assertThat(formPage.certifyInfoError).isVisible()
        assertThat(formPage.agreementError).isVisible()

        // Fill in the boat information
        formPage.boatTypeSelect.selectOption("Sailboat")

        formPage.widthInput.clear()
        formPage.widthInput.blur()
        assertThat(formPage.widthError).isVisible()

        formPage.lengthInput.clear()
        formPage.lengthInput.blur()
        assertThat(formPage.lengthError).isVisible()

        // warning for boat size
        formPage.widthInput.fill("10")
        formPage.widthInput.blur()
        assertThat(formPage.boatSizeWarning).isVisible()

        formPage.widthInput.fill("3")
        formPage.widthInput.blur()
        assertThat(formPage.boatSizeWarning).isHidden()

        formPage.lengthInput.fill("20")
        formPage.lengthInput.blur()
        assertThat(formPage.boatSizeWarning).isVisible()

        formPage.lengthInput.fill("5")
        formPage.lengthInput.blur()
        assertThat(formPage.boatSizeWarning).isHidden()

        formPage.lengthInput.fill("25")
        formPage.lengthInput.blur()
        assertThat(formPage.boatSizeWarning).isVisible()
        formPage.lengthInput.fill("60")
        formPage.lengthInput.blur()

        formPage.depthInput.fill("1.5")
        formPage.depthInput.blur()
        assertThat(formPage.depthError).isHidden()

        formPage.weightInput.fill("2000")
        formPage.weightInput.blur()
        assertThat(formPage.weightError).isHidden()

        formPage.boatNameInput.fill("My Boat")
        formPage.otherIdentification.fill("ID12345")
        formPage.noRegistrationCheckbox.check()
        assertThat(formPage.boatRegistrationNumberError).isHidden()

        formPage.ownerRadioButton.check()

        formPage.firstNameInput.fill("John")
        formPage.lastNameInput.fill("Doe")
        formPage.ssnInput.fill("123456-789A")
        formPage.addressInput.fill("Test street 1")
        formPage.postalCodeInput.fill("12345")

        formPage.emailInput.fill("test@example.com")
        formPage.emailInput.blur()
        assertThat(formPage.emailError).isHidden()
        formPage.phoneInput.fill("123456789")
        formPage.phoneInput.blur()
        assertThat(formPage.phoneError).isHidden()

        formPage.certifyInfoCheckbox.check()
        formPage.agreementCheckbox.check()
    }

    @Test
    fun `Employee can reserve a boat space on behalf of a citizen, send the invoice and set the reservation as paid on citizen page`() {
        try {
            val listingPage = reservationListPage()

            listingPage.createReservation.click()

            val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)

            // fill in the filters
            assertThat(reservationPage.emptyDimensionsWarning).isVisible()
            reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
            reservationPage.widthFilterInput.fill("3")
            reservationPage.lengthFilterInput.fill("6")
            reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Slip).click()
            reservationPage.amenityBuoyCheckbox.check()
            reservationPage.amenityRearBuoyCheckbox.check()
            reservationPage.amenityBeamCheckbox.check()
            reservationPage.amenityWalkBeamCheckbox.check()

            assertThat(reservationPage.harborHeaders).hasCount(3)
            reservationPage.haukilahtiCheckbox.check()
            reservationPage.kivenlahtiCheckbox.check()
            assertThat(reservationPage.harborHeaders).hasCount(2)

            reservationPage.firstReserveButton.click()

            val formPage = BoatSpaceFormPage(page)
            fillAndTestForm(formPage)
            formPage.submitButton.click()

            val invoicePreviewPage = InvoicePreviewPage(page)
            assertThat(invoicePreviewPage.header).isVisible()
            invoicePreviewPage.sendButton.click()

            val reservationListPage = ReservationListPage(page)
            assertThat(reservationListPage.header).isVisible()
            page.getByText("Doe John").click()
            val citizenDetailsPage = CitizenDetailsPage(page)

            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }
            assertTrue("Valid until further notice" in citizenDetailsPage.reservationValidity.first().textContent())

            updateReservationToConfirmed(citizenDetailsPage)

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText("Varauksen tila: Maksettu 2024-04-22: 100000")).isVisible()

            messageService.sendScheduledEmails()
            assertEquals(1, SendEmailServiceMock.emails.size)
            assertTrue(
                SendEmailServiceMock.emails.get(
                    0
                ).contains("test@example.com with subject Espoon kaupungin venepaikan varaus")
            )

            citizenDetailsPage.paymentsNavi.click()

            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("Maksettu") }
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("Haukilahti D013") }
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("Laituri") }
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("100000") }
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("Lasku") }
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("22.04.2024") }
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("418,00") }

            citizenDetailsPage.refundPaymentButton.click()
            citizenDetailsPage.refundPaymentModalConfirm.click()

            citizenDetailsPage.paymentsNavi.click()
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("Hyvitetty") }
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    fun updateReservationToConfirmed(citizenDetailsPage: CitizenDetailsPage) {
        page.waitForCondition {
            citizenDetailsPage.paymentStatus.textContent().contains("Invoiced: due date: 22.04.2024")
        }
        page.waitForCondition { citizenDetailsPage.paymentStatus.textContent().contains("Invoice id: 100000") }
        citizenDetailsPage.updatePaymentStatusLink.click()

        assertEquals("100000", citizenDetailsPage.paymentStatusUpdateModalInfoTextInput.inputValue())
        assertEquals("2024-04-22", citizenDetailsPage.paymentStatusUpdateModalDateInput.inputValue())

        citizenDetailsPage.paymentStatusUpdateModalConfirmed.click()
        citizenDetailsPage.paymentStatusUpdateModalSubmit.click()

        page.waitForCondition { citizenDetailsPage.paymentStatus.textContent().contains("Confirmed, 22.04.2024") }
        page.waitForCondition { citizenDetailsPage.paymentStatus.textContent().contains("Invoice id: 100000") }
    }

    @Test
    fun `Employee can reserve a boat space on behalf of a citizen and to set the invoice as paid`() {
        try {
            val listingPage = reservationListPage()

            listingPage.createReservation.click()

            val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)

            // fill in the filters
            reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
            reservationPage.widthFilterInput.fill("3")
            reservationPage.lengthFilterInput.fill("6")
            reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Slip).click()

            reservationPage.firstReserveButton.click()

            val formPage = BoatSpaceFormPage(page)
            fillAndTestForm(formPage)
            formPage.submitButton.click()

            val invoicePreviewPage = InvoicePreviewPage(page)
            assertThat(invoicePreviewPage.header).isVisible()
            invoicePreviewPage.markAsPaid.click()
            invoicePreviewPage.confirmModalCancel.click()
            assertThat(invoicePreviewPage.header).isVisible()
            invoicePreviewPage.markAsPaid.click()
            invoicePreviewPage.confirmModalSubmit.click()

            val reservationListPage = ReservationListPage(page)
            assertThat(reservationListPage.header).isVisible()
            page.getByText("Doe John").click()
            val citizenDetailsPage = CitizenDetailsPage(page)

            assertThat(citizenDetailsPage.invoicePaidButton).isHidden()

            page.waitForCondition { citizenDetailsPage.paymentStatus.textContent().contains("Confirmed, 01.04.2024") }
            page.waitForCondition { citizenDetailsPage.paymentStatus.textContent().contains("Invoice id: 100000") }
            messageService.sendScheduledEmails()
            // no email should be sent
            assertEquals(0, SendEmailServiceMock.emails.size)
        } catch (
            e: AssertionError
        ) {
            handleError(e)
        }
    }

    @Test
    fun `When employee cancels the reservation from invoice page, the reservation is deleted`() {
        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin()

        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()

        listingPage.createReservation.click()

        val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)

        reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Slip).click()

        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        val place = page.getByTestId("place").innerText()
        fillAndTestForm(formPage)
        formPage.submitButton.click()

        val invoicePreviewPage = InvoicePreviewPage(page)
        assertThat(invoicePreviewPage.header).isVisible()
        // cancel the reservation
        invoicePreviewPage.cancelButton.click()
        assertThat(listingPage.header).isVisible()

        // check that the place is available for new reservations
        reservationPage.navigateTo()
        reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Slip).click()

        assertThat(page.getByText(place)).isVisible()
    }

    @Test
    fun `Employee can reserve a winter space on behalf of a citizen, the employee is then able to set the reservation as paid`() {
        try {
            val listingPage = reservationListPage()

            listingPage.boatSpaceTypeFilter("Winter").click()

            listingPage.createReservation.click()

            val reservationPage = ReserveWinterSpacePage(page, UserType.EMPLOYEE)

            // fill in the filters
            assertThat(reservationPage.emptyDimensionsWarning).isVisible()
            reservationPage.boatTypeSelectFilter.selectOption("OutboardMotor")
            reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Winter).click()

            reservationPage.widthFilterInput.fill("1.8")
            reservationPage.lengthFilterInput.fill("5.5")

            reservationPage.firstReserveButton.click()

            val formPage = BoatSpaceFormPage(page)

            formPage.submitButton.click()

            fillWinterBoatSpaceForm(formPage)

            formPage.submitButton.click()

            val invoicePreviewPage = InvoicePreviewPage(page)
            assertThat(invoicePreviewPage.header).isVisible()
            invoicePreviewPage.sendButton.click()

            val reservationListPage = ReservationListPage(page)
            assertThat(reservationListPage.header).isVisible()
            page.getByText("Doe John").click()
            val citizenDetailsPage = CitizenDetailsPage(page)

            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }
            assertTrue("Valid until further notice" in citizenDetailsPage.reservationValidity.first().textContent())

            messageService.sendScheduledEmails()
            assertEquals(1, SendEmailServiceMock.emails.size)
            assertTrue(
                SendEmailServiceMock.emails.get(
                    0
                ).contains("test@example.com with subject Espoon kaupungin venepaikan varaus")
            )

            updateReservationToConfirmed(citizenDetailsPage)

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText("Varauksen tila: Maksettu 2024-04-22: 100000")).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a winter space on behalf of a citizen, and change the reservation validity to fixed time`() {
        try {
            val listingPage = reservationListPage()

            listingPage.boatSpaceTypeFilter("Winter").click()

            listingPage.createReservation.click()

            val reservationPage = ReserveWinterSpacePage(page, UserType.EMPLOYEE)

            // fill in the filters
            assertThat(reservationPage.emptyDimensionsWarning).isVisible()
            reservationPage.boatTypeSelectFilter.selectOption("OutboardMotor")
            reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Winter).click()

            reservationPage.widthFilterInput.fill("1.8")
            reservationPage.lengthFilterInput.fill("5.5")

            reservationPage.firstReserveButton.click()

            val formPage = BoatSpaceFormPage(page)

            formPage.submitButton.click()

            fillWinterBoatSpaceForm(formPage)
            formPage.reservationValidityFixedTermRadioButton.click()
            assertContains(
                formPage.reservationSummeryReservationValidityFixedTerm.first().textContent(),
                "01.04.2024 - 31.12.2024"
            )
            assertContains(formPage.reservationValidityInformation.textContent(), "01.04.2024 - 31.12.2024")
            formPage.submitButton.click()

            val invoicePreviewPage = InvoicePreviewPage(page)
            assertThat(invoicePreviewPage.header).isVisible()
            invoicePreviewPage.sendButton.click()

            val reservationListPage = ReservationListPage(page)
            assertThat(reservationListPage.header).isVisible()
            page.getByText("Doe John").click()
            val citizenDetailsPage = CitizenDetailsPage(page)

            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }
            assertContains(citizenDetailsPage.reservationValidity.first().textContent(), "Until 31.08.2024")

            messageService.sendScheduledEmails()
            assertEquals(1, SendEmailServiceMock.emails.size)
            assertTrue(
                SendEmailServiceMock.emails.get(
                    0
                ).contains("test@example.com with subject Espoon kaupungin venepaikan varaus")
            )

            updateReservationToConfirmed(citizenDetailsPage)

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText("Varauksen tila: Maksettu 2024-04-22: 100000")).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    private fun fillWinterBoatSpaceForm(formPage: BoatSpaceFormPage) {
        assertThat(formPage.depthError).isVisible()
        assertThat(formPage.weightError).isVisible()
        assertThat(formPage.boatRegistrationNumberError).isVisible()
        assertThat(formPage.certifyInfoError).isVisible()
        assertThat(formPage.agreementError).isVisible()

        // Fill in the boat information
        formPage.boatTypeSelect.selectOption("OutboardMotor")

        formPage.boatNameInput.fill("My Boat")
        formPage.widthInput.fill("1.8")
        formPage.widthInput.blur()

        formPage.lengthInput.fill("5")
        formPage.lengthInput.blur()

        formPage.depthInput.fill("1.5")
        formPage.depthInput.blur()

        formPage.weightInput.fill("500")
        formPage.depthInput.blur()

        formPage.otherIdentification.fill("ID12345")
        formPage.noRegistrationCheckbox.check()
        assertThat(formPage.boatRegistrationNumberError).isHidden()

        formPage.ownerRadioButton.check()

        formPage.firstNameInput.fill("John")
        formPage.lastNameInput.fill("Doe")
        formPage.ssnInput.fill("123456-789A")
        formPage.addressInput.fill("Test street 1")
        formPage.postalCodeInput.fill("12345")

        formPage.emailInput.fill("test@example.com")
        formPage.emailInput.blur()
        assertThat(formPage.emailError).isHidden()
        formPage.phoneInput.fill("123456789")
        formPage.phoneInput.blur()
        assertThat(formPage.phoneError).isHidden()

        formPage.storageTypeBuckOption.click()

        formPage.certifyInfoCheckbox.check()
        formPage.agreementCheckbox.check()
    }

    @Test
    fun `Employee can reserve a boat space on behalf of a citizen and change the reservation validity type`() {
        try {
            val listingPage = reservationListPage()

            listingPage.createReservation.click()

            val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)

            // fill in the filters
            assertThat(reservationPage.emptyDimensionsWarning).isVisible()
            reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
            reservationPage.widthFilterInput.fill("3")
            reservationPage.lengthFilterInput.fill("6")
            reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Slip).click()
            reservationPage.firstReserveButton.click()

            val formPage = BoatSpaceFormPage(page)
            fillAndTestForm(formPage)
            formPage.reservationValidityFixedTermRadioButton.click()
            assertContains(formPage.reservationValidityInformation.textContent(), "01.04.2024 - 31.12.2024")
            formPage.submitButton.click()

            val invoicePreviewPage = InvoicePreviewPage(page)
            assertThat(invoicePreviewPage.header).isVisible()
            invoicePreviewPage.sendButton.click()

            val reservationListPage = ReservationListPage(page)
            assertThat(reservationListPage.header).isVisible()
            page.getByText("Doe John").click()
            val citizenDetailsPage = CitizenDetailsPage(page)

            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }
            assertTrue("Until 31.12.2024" in citizenDetailsPage.reservationValidity.first().textContent())

            messageService.sendScheduledEmails()
            assertEquals(1, SendEmailServiceMock.emails.size)
            assertTrue(
                SendEmailServiceMock.emails.get(
                    0
                ).contains("test@example.com with subject Espoon kaupungin venepaikan varaus")
            )
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `existing citizens can be searched`() {
        val listingPage = reservationListPage()
        listingPage.createReservation.click()

        val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        formPage.existingCitizenSelector.click()
        assertThat(formPage.citizenSearchContainer).isVisible()
        typeText(formPage.citizenSearchInput, "virtane")
        assertThat(formPage.citizenSearchOption1).isVisible()
        assertThat(formPage.citizenSearchOption2).isVisible()
        formPage.citizenEmptyInput.click()
        assertThat(formPage.citizenSearchOption1).isHidden()
        typeText(formPage.citizenSearchInput, "virtane")
        assertThat(formPage.citizenSearchOption1).containsText("Mikko Virtanen")
        formPage.citizenSearchInput.clear()
        typeText(formPage.citizenSearchInput, "010106A957V")
        assertThat(formPage.citizenSearchOption1).containsText("Mikko Virtanen")
        formPage.citizenSearchOption1.click()
        assertThat(formPage.citizenSearchInput).hasValue("Mikko Virtanen")
    }

    @Test
    fun `Employee can reserve a boat space on behalf of an existing citizen`() {
        val listingPage = reservationListPage()
        listingPage.createReservation.click()

        val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        val place = page.getByTestId("place").innerText()
        formPage.existingCitizenSelector.click()
        assertThat(formPage.citizenSearchContainer).isVisible()

        formPage.submitButton.click()

        assertThat(formPage.citizenIdError).isVisible()

        typeText(formPage.citizenSearchInput, "virtane")

        formPage.citizenSearchOption1.click()
        assertThat(page.getByTestId("firstName")).containsText("Mikko")
        assertThat(page.getByTestId("lastName")).containsText("Virtanen")
        // Fill in the boat information
        fillBoatAndOtherDetails(formPage)
        formPage.submitButton.click()

        val invoicePage = InvoicePreviewPage(page)
        assertThat(invoicePage.header).isVisible()
        assertThat(page.getByTestId("reserverName")).containsText("Mikko Virtanen")
        val description = page.getByTestId("description").inputValue()
        assertContains(description, place)

        invoicePage.sendButton.click()

        val reservationListPage = ReservationListPage(page)
        assertThat(reservationListPage.header).isVisible()

        // Check that the reservation is visible in the list
        assertThat(page.getByText(place)).isVisible()

        messageService.sendScheduledEmails()
        assertEquals(1, SendEmailServiceMock.emails.size)

        assertTrue(
            SendEmailServiceMock.emails.get(
                0
            ).contains("to mikko.virtanen@noreplytest.fi with subject Espoon kaupungin venepaikan varaus")
        )
    }

    @Test
    fun `After reselecting "new citizen", the previously selected citizen is no longer selected`() {
        val listingPage = reservationListPage()
        listingPage.createReservation.click()

        val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        formPage.existingCitizenSelector.click()
        assertThat(formPage.citizenSearchContainer).isVisible()

        typeText(formPage.citizenSearchInput, "olivia")

        formPage.citizenSearchOption1.click()
        assertThat(page.getByTestId("firstName")).containsText("Olivia")
        assertThat(page.getByTestId("lastName")).containsText("Virtanen")
        assertThat(page.getByText("Olivian vene")).isVisible()
        formPage.organizationRadioButton.click()

        page.getByText("Espoon Pursiseura").click()

        assertThat(page.getByText("Espoon lohi")).isVisible()
        assertThat(page.getByText("Espoon kuha")).isVisible()
        assertThat(page.getByText("Olivian vene")).isHidden()
        formPage.newCitizenSelector.click()

        assertThat(page.getByText("Espoon lohi")).isHidden()
        assertThat(page.getByText("Espoon kuha")).isHidden()
        assertThat(page.getByText("Olivian vene")).isHidden()
        assertThat(page.getByText("Espoon Pursiseura")).isHidden()
    }

    @Test
    fun reservingABoatSpaceAsOrganization() {
        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin()

        val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)
        reservationPage.navigateTo()

        reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()

        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)

        formPage.existingCitizenSelector.click()
        assertThat(formPage.citizenSearchContainer).isVisible()

        formPage.submitButton.click()

        typeText(formPage.citizenSearchInput, "virtane")
        formPage.citizenSearchOption1.click()
        assertThat(page.getByTestId("firstName")).containsText("Mikko")
        assertThat(page.getByTestId("lastName")).containsText("Virtanen")

        formPage.organizationRadioButton.click()
        formPage.orgNameInput.fill("My Organization")
        formPage.orgBusinessIdInput.fill("1234567-8")
        formPage.orgPhoneNumberInput.fill("123456789")
        formPage.orgEmailInput.fill("foo@bar.com")

        formPage.orgBillingNameInput.fill("Billing Name")
        formPage.orgBillingAddressInput.fill("Billing Name")
        formPage.orgBillingPostalCodeInput.fill("12345")
        formPage.orgBillingCityInput.fill("12345")

        formPage.depthInput.fill("1.5")
        formPage.depthInput.blur()
        assertThat(formPage.depthError).isHidden()

        formPage.weightInput.fill("2000")
        formPage.weightInput.blur()
        assertThat(formPage.weightError).isHidden()

        formPage.boatNameInput.fill("My Boat")
        formPage.otherIdentification.fill("ID12345")
        formPage.noRegistrationCheckbox.check()
        assertThat(formPage.boatRegistrationNumberError).isHidden()

        formPage.ownerRadioButton.check()

        formPage.certifyInfoCheckbox.check()
        formPage.agreementCheckbox.check()

        formPage.submitButton.click()

        val invoicePreviewPage = InvoicePreviewPage(page)
        assertThat(invoicePreviewPage.header).isVisible()
        invoicePreviewPage.sendButton.click()

        val reservationListPage = ReservationListPage(page)
        assertThat(reservationListPage.header).isVisible()

        messageService.sendScheduledEmails()
        // Email is sent to both organization representative and the reserver
        assertEquals(2, SendEmailServiceMock.emails.size)

        assertTrue(
            SendEmailServiceMock.emails.any {
                it.contains("foo@bar.com with subject Espoon kaupungin venepaikan varaus")
            }
        )
        assertTrue(
            SendEmailServiceMock.emails.any {
                it.contains("mikko.virtanen@noreplytest.fi with subject Espoon kaupungin venepaikan varaus")
            }
        )
    }

    @Test
    fun `Employee can reserve on behalf of an existing citizen acting on behalf of an existing organization`() {
        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin()

        val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)
        reservationPage.navigateTo()

        reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()

        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)

        formPage.submitButton.click()

        formPage.existingCitizenSelector.click()
        "olivia".forEach { character ->
            formPage.citizenSearchInput.press("$character")
        }
        page.waitForCondition { formPage.citizenSearchOption1.isVisible() }
        formPage.citizenSearchOption1.click()

        assertThat(page.getByText("Olivian vene")).isVisible()

        formPage.organizationRadioButton.click()

        assertThat(page.getByText("Olivian vene")).isHidden()
    }

    @Test
    fun `Employee can reserve a boat space on behalf of an existing citizen with a discount taken into account`() {
        val reserverLastName = "Korhonen"
        val reserverFirstName = "Leo"
        val discountPercentage = 50
        val priveWithtax = "418,00"
        val discountedPrice = "209,00"

        val listingPage = reservationListPage()
        setDiscountFor(listingPage, reserverLastName, discountPercentage)

        val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)
        reservationPage.navigateTo()
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        assertThat(formPage.boatSpacePriceInEuro).containsText("$priveWithtax €")

        formPage.existingCitizenSelector.click()
        assertThat(formPage.citizenSearchContainer).isVisible()

        formPage.submitButton.click()

        assertThat(formPage.citizenIdError).isVisible()

        typeText(formPage.citizenSearchInput, reserverLastName)

        formPage.citizenSearchOption1.click()
        assertThat(page.getByTestId("firstName")).containsText(reserverFirstName)
        assertThat(page.getByTestId("lastName")).containsText(reserverLastName)

        val reservationPriceInfo = formPage.getByDataTestId("reservation-price-info")
        page.waitForCondition { reservationPriceInfo.isVisible }
        assertThat(reservationPriceInfo)
            .hasText(
                "Reserver Leo Korhonen has a 50 % discount. After the discount, price of the boat space is $discountedPrice €"
            )

        // Fill in the boat information
        fillBoatAndOtherDetails(formPage)

        formPage.submitButton.click()

        val invoicePage = InvoicePreviewPage(page)
        page.waitForCondition { invoicePage.header.isVisible }

        assertThat(page.getByTestId("reserverName")).containsText("$reserverFirstName $reserverLastName")
        val price = invoicePage.getByDataTestId("priceWithTax").inputValue()
        assertEquals(discountedPrice.replace(",", "."), price)
        val discountInfo = invoicePage.getByDataTestId("invoice-discount-info")
        assertEquals(
            discountInfo.innerText(),
            "Customer discount of $discountPercentage% taken into account.\nOriginal price of the boat space ${
                priveWithtax.replace(
                    ",",
                    "."
                )
            } €."
        )
    }

    private fun fillBoatAndOtherDetails(formPage: BoatSpaceFormPage) {
        formPage.boatTypeSelect.selectOption("Sailboat")

        formPage.widthInput.fill("3")
        formPage.widthInput.blur()

        formPage.lengthInput.fill("5")
        formPage.lengthInput.blur()

        formPage.depthInput.fill("1.5")
        formPage.depthInput.blur()

        formPage.weightInput.fill("2000")
        formPage.weightInput.blur()

        formPage.boatNameInput.fill("My Boat")
        formPage.otherIdentification.fill("ID12345")
        formPage.noRegistrationCheckbox.check()

        formPage.ownerRadioButton.check()

        formPage.certifyInfoCheckbox.check()
        formPage.agreementCheckbox.check()
    }

    private fun setDiscountFor(
        listingPage: ReservationListPage,
        reserverName: String,
        discount: Int
    ) {
        listingPage
            .getByDataTestId("reserver-name")
            .getByText(reserverName)
            .click()
        val citizenDetails = CitizenDetailsPage(page)
        assertThat(citizenDetails.citizenLastNameField).hasText(reserverName)
        citizenDetails.exceptionsNavi.click()
        val discount0 = page.getByTestId("reserver_discount_0")
        assertThat(discount0).isChecked()
        page.getByTestId("reserver_discount_$discount").check()
        assertThat(citizenDetails.getByDataTestId("exceptions-tab-attention")).hasClass("attention on")
    }

    private fun reservationListPage(): ReservationListPage {
        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin()

        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        return listingPage
    }
}
