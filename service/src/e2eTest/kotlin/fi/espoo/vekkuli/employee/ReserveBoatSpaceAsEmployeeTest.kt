package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.pages.*
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.utils.formatAsTestDate
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import kotlin.test.assertContains
import kotlin.test.assertTrue

@ActiveProfiles("test")
class ReserveBoatSpaceAsEmployeeTest : PlaywrightTest() {
    @Test
    fun `employee can change the language`() {
        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin("fi")
        assertThat(page.getByText("Varaukset").first()).isVisible()
        val listingPage = ReservationListPage(page)
        page.getByTestId("language-selection").click()
        page.getByText("Englanti").click()
        assertThat(page.getByText("Reservations").first()).isVisible()
        page.getByTestId("language-selection").click()
        page.getByText("Swedish").click()
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
    fun `Employee can reserve a boat space on behalf of a citizen, the employee is then able to set the reservation as paid`() {
        try {
            val employeeHome = EmployeeHomePage(page)
            employeeHome.employeeLogin()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()

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

            citizenDetailsPage.invoicePaidButton.click()
            val info = "invoice has been paid"
            citizenDetailsPage.invoicePaidInfo.fill(info)
            val testDate = LocalDate.of(2024, 7, 22)

            citizenDetailsPage.invoicePaymentDate.fill(formatAsTestDate(testDate))

            citizenDetailsPage.invoiceModalConfirm.click()
            assertThat(citizenDetailsPage.paidFieldInfo).hasText(formatAsFullDate(testDate))

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText(info)).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a winter space on behalf of a citizen, the employee is then able to set the reservation as paid`() {
        try {
            val employeeHome = EmployeeHomePage(page)
            employeeHome.employeeLogin()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()

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

            citizenDetailsPage.invoicePaidButton.click()
            val info = "invoice has been paid"
            citizenDetailsPage.invoicePaidInfo.fill(info)
            val testDate = LocalDate.of(2024, 7, 22)

            citizenDetailsPage.invoicePaymentDate.fill(formatAsTestDate(testDate))

            citizenDetailsPage.invoiceModalConfirm.click()
            assertThat(citizenDetailsPage.paidFieldInfo).hasText(formatAsFullDate(testDate))

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText(info)).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a winter space on behalf of a citizen, and change the reservation validity to fixed time`() {
        try {
            val employeeHome = EmployeeHomePage(page)
            employeeHome.employeeLogin()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()

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
            assertContains(formPage.reservationSummeryReservationValidityFixedTerm.first().textContent(), "01.04.2024 - 31.12.2024")
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

            citizenDetailsPage.invoicePaidButton.click()
            val info = "invoice has been paid"
            citizenDetailsPage.invoicePaidInfo.fill(info)
            val testDate = LocalDate.of(2024, 7, 22)
            citizenDetailsPage.invoicePaymentDate.fill(formatAsTestDate(testDate))

            citizenDetailsPage.invoiceModalConfirm.click()
            assertThat(citizenDetailsPage.paidFieldInfo).hasText(formatAsFullDate(testDate))

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText(info)).isVisible()
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
            val employeeHome = EmployeeHomePage(page)
            employeeHome.employeeLogin()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()

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
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `existing citizens can be searched`() {
        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin()
        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
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
        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin()
        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        listingPage.createReservation.click()

        val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)
        reservationPage.widthFilterInput.fill("3")
        reservationPage.lengthFilterInput.fill("6")
        reservationPage.lengthFilterInput.blur()
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        formPage.existingCitizenSelector.click()
        assertThat(formPage.citizenSearchContainer).isVisible()

        formPage.submitButton.click()

        assertThat(formPage.citizenIdError).isVisible()

        "virtane".forEach { character ->
            formPage.citizenSearchInput.press("$character")
        }

        formPage.citizenSearchOption1.click()
        // Fill in the boat information
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
        formPage.submitButton.click()
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

        formPage.organizationRadioButton.click()
        formPage.orgNameInput.fill("My Organization")
        formPage.orgBusinessIdInput.fill("1234567-8")
        formPage.orgPhoneNumberInput.fill("123456789")
        formPage.orgEmailInput.fill("foo@bar.com")

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
        formPage.citizenSearchOption1.click()

        assertThat(page.getByText("Olivian vene")).isVisible()

        formPage.organizationRadioButton.click()

        assertThat(page.getByText("Olivian vene")).isHidden()
    }
}
