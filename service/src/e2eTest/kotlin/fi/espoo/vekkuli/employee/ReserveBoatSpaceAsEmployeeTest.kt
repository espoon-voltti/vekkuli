package fi.espoo.vekkuli.employee

import com.microsoft.playwright.Locator
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.ReserveTest
import fi.espoo.vekkuli.boatSpace.invoice.InvoicePaymentService
import fi.espoo.vekkuli.boatSpace.invoice.MockInvoicePaymentClient
import fi.espoo.vekkuli.boatSpace.invoice.Receipt
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.PaymentStatus
import fi.espoo.vekkuli.pages.citizen.CitizenHomePage
import fi.espoo.vekkuli.pages.employee.*
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import fi.espoo.vekkuli.pages.citizen.BoatSpaceFormPage as CitizenBoatSpaceFormPage
import fi.espoo.vekkuli.pages.citizen.PaymentPage as CitizenPaymentPage
import fi.espoo.vekkuli.pages.citizen.ReserveBoatSpacePage as CitizenReserveBoatSpacePage

@ActiveProfiles("test")
class ReserveBoatSpaceAsEmployeeTest : ReserveTest() {
    @Autowired
    private lateinit var invoicePaymentService: InvoicePaymentService

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

            val citizenDetailsPage = CitizenDetailsPage(page)
            assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()

            val reservationListPage = ReservationListPage(page)
            reservationListPage.navigateTo()
            assertThat(reservationListPage.header).isVisible()

            val reservationRow =
                reservationListPage.reservations.filter(
                    Locator.FilterOptions().setHasText("Doe John")
                )
            assertTrue(reservationRow.textContent().contains("Laskutettu, eräpäivä 22.04.24"))

            page.getByText("Doe John").click()

            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }
            assertTrue("Valid until further notice" in citizenDetailsPage.reservationValidity.first().textContent())

            updateReservationToConfirmed(citizenDetailsPage)

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText("Varauksen tila: Maksettu 2024-04-22: 100000")).isVisible()

            assertEmailIsSentOfEmployeesIndefiniteSlipReservationWithInvoice(invoiceAddress = "Test street 1, 12345")

            MockInvoicePaymentClient.payments.add(
                Receipt(100000, BigDecimal(418), "2024-04-22", "VEK_100000")
            )
            invoicePaymentService.fetchAndStoreInvoicePayments()

            citizenDetailsPage.paymentsNavi.click()

            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("Maksettu") }
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("Haukilahti D 013") }
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("Laituri") }
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("100000") }
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("Lasku") }
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("22.04.2024") }
            page.waitForCondition { citizenDetailsPage.paymentsTable.textContent().contains("418,00") }

            page.waitForCondition { citizenDetailsPage.settlementRows.textContent().contains("100000") }
            page.waitForCondition { citizenDetailsPage.settlementRows.textContent().contains("418,00") }

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

            val citizenDetailsPage = CitizenDetailsPage(page)
            assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()

            val reservationListPage = ReservationListPage(page)
            reservationListPage.navigateTo()
            assertThat(reservationListPage.header).isVisible()
            page.getByText("Doe John").click()

            assertThat(citizenDetailsPage.invoicePaidButton).isHidden()

            page.waitForCondition { citizenDetailsPage.paymentStatus.textContent().contains("Confirmed, 01.04.2024") }
            page.waitForCondition { citizenDetailsPage.paymentStatus.textContent().contains("Invoice id: 100000") }

            assertCorrectPaymentForReserver(
                "doe",
                PaymentStatus.Success,
                "Haukilahti D 013",
                "0,00",
                "Laituripaikka 2024-2025 Haukilahti D 013",
                doLogin = false
            )

            assertEmailIsSentOfEmployeesIndefiniteSlipReservationWithoutPayment()
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

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun `Employee can reserve a indefinite winter space on behalf of a citizen and send invoice, the employee is then able to set the reservation as paid`() {
        try {
            reserveWinterSpace(false, true)

            val citizenDetailsPage = CitizenDetailsPage(page)

            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }
            assertTrue("Valid until further notice" in citizenDetailsPage.reservationValidity.first().textContent())

            assertEmailIsSentOfEmployeeIndefiniteWinterSpaceReservationWithInvoice()
            updateReservationToConfirmed(citizenDetailsPage)

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText("Varauksen tila: Maksettu 2024-04-22: 100000")).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a indefinite winter space on behalf of a citizen without payment`() {
        try {
            reserveWinterSpace(false, false)

            val citizenDetailsPage = CitizenDetailsPage(page)

            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }
            assertTrue("Valid until further notice" in citizenDetailsPage.reservationValidity.first().textContent())

            assertEmailIsSentOfEmployeeIndefiniteWinterSpaceReservationWithoutPayment()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a fixed term winter space on behalf of a citizen and send invoice`() {
        try {
            reserveWinterSpace(true, true)
            val citizenDetailsPage = CitizenDetailsPage(page)

            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }
            assertContains(citizenDetailsPage.reservationValidity.first().textContent(), "Until 14.09.2024")

            assertEmailIsSentOfEmployeeFixedTermWinterSpaceReservationWithInvoice(endDate = "14.09.2024")
            updateReservationToConfirmed(citizenDetailsPage)

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText("Varauksen tila: Maksettu 2024-04-22: 100000")).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a fixed term winter space on behalf of a citizen without payment`() {
        try {
            reserveWinterSpace(true, false)
            val citizenDetailsPage = CitizenDetailsPage(page)

            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }
            assertContains(citizenDetailsPage.reservationValidity.first().textContent(), "Until 14.09.2024")

            assertEmailIsSentOfEmployeeFixedTermWinterSpaceReservationWithoutPayment(endDate = "14.09.2024")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    private fun reserveWinterSpace(
        isFixed: Boolean,
        sendInvoice: Boolean
    ) {
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
        if (isFixed) {
            formPage.reservationValidityFixedTermRadioButton.click()
            // todo: the end date is wrong, should be 31.08.2024
            assertContains(formPage.reservationValidityInformation.textContent(), "01.04.2024 - 31.12.2024")
        }

        formPage.submitButton.click()

        val invoicePreviewPage = InvoicePreviewPage(page)
        assertThat(invoicePreviewPage.header).isVisible()
        if (sendInvoice) {
            invoicePreviewPage.sendButton.click()
        } else {
            invoicePreviewPage.markAsPaid.click()
            invoicePreviewPage.confirmModalSubmit.click()
        }
        val citizenDetailsPage = CitizenDetailsPage(page)
        assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()
    }

    @Test
    fun `Employee can reserve a storage space with trailer amenity on behalf of a citizen`() {
        try {
            val listingPage = reservationListPage()

            listingPage.createReservation.click()

            val reservationPage = ReserveWinterSpacePage(page, UserType.EMPLOYEE)

            // fill in the filters
            assertThat(reservationPage.emptyDimensionsWarning).isVisible()
            reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Storage).click()

            reservationPage.amenityTrailerRadioButton.click()
            reservationPage.widthFilterInput.fill("1.8")
            reservationPage.lengthFilterInput.fill("5.5")

            reservationPage.firstReserveButton.click()

            val formPage = BoatSpaceFormPage(page)
            formPage.fillFormAndSubmit {
                assertThat(formPage.storageTypeTextTrailer).isVisible()
                trailerWidthInput.fill("1.8")
                trailerLengthInput.fill("5.5")
                trailerRegistrationNumberInput.fill("ABC-123")
            }

            val invoicePreviewPage = InvoicePreviewPage(page)
            assertThat(invoicePreviewPage.header).isVisible()
            invoicePreviewPage.sendButton.click()

            val citizenDetailsPage = CitizenDetailsPage(page)
            assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()

            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }

            assertEmailIsSentOfEmployeesIndefiniteStorageSpaceReservationWithInvoice()
            updateReservationToConfirmed(citizenDetailsPage)

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText("Varauksen tila: Maksettu 2024-04-22: 100000")).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a storage space with buck amenity space on behalf of a citizen`() {
        try {
            val listingPage = reservationListPage()

            listingPage.createReservation.click()

            val reservationPage = ReserveWinterSpacePage(page, UserType.EMPLOYEE)

            // fill in the filters
            assertThat(reservationPage.emptyDimensionsWarning).isVisible()
            reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Storage).click()

            reservationPage.amenityBuckRadioButton.click()
            reservationPage.widthFilterInput.fill("1.8")
            reservationPage.lengthFilterInput.fill("5.5")

            reservationPage.firstReserveButton.click()

            val formPage = BoatSpaceFormPage(page)
            formPage.fillFormAndSubmit {
                assertThat(formPage.storageTypeTextTrailer).isVisible()
                storageTypeBuckWithTentOption.click()
            }

            val invoicePreviewPage = InvoicePreviewPage(page)
            assertThat(invoicePreviewPage.header).isVisible()
            invoicePreviewPage.sendButton.click()

            val citizenDetailsPage = CitizenDetailsPage(page)
            assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()

            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }

            assertEmailIsSentOfEmployeesIndefiniteStorageSpaceReservationWithInvoice()
            updateReservationToConfirmed(citizenDetailsPage)

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText("Varauksen tila: Maksettu 2024-04-22: 100000")).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a indefinite trailer space on behalf of a citizen and send an invoice`() {
        try {
            reserveTrailerSpace(false, true)

            val citizenDetailsPage = CitizenDetailsPage(page)
            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }

            assertEmailIsSentOfEmployeeIndefiniteTrailerReservationWithInvoice()
            updateReservationToConfirmed(citizenDetailsPage)

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText("Varauksen tila: Maksettu 2024-04-22: 100000")).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a indefinite trailer space on behalf of a citizen without payment`() {
        try {
            reserveTrailerSpace(false, false)

            val citizenDetailsPage = CitizenDetailsPage(page)
            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }

            assertEmailIsSentOfEmployeeIndefiniteTrailerReservationWithoutPayment()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a fixed term trailer space on behalf of a citizen and send an invoice`() {
        try {
            reserveTrailerSpace(true, true)

            val citizenDetailsPage = CitizenDetailsPage(page)
            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }

            assertEmailIsSentOfEmployeeFixedTermTrailerReservationWithInvoice(endDate = "30.04.2024")
            updateReservationToConfirmed(citizenDetailsPage)

            citizenDetailsPage.memoNavi.click()
            assertThat(page.getByText("Varauksen tila: Maksettu 2024-04-22: 100000")).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a fixed term trailer space on behalf of a citizen without payment`() {
        try {
            reserveTrailerSpace(true, false)

            val citizenDetailsPage = CitizenDetailsPage(page)
            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }

            assertEmailIsSentOfEmployeeFixedTermTrailerReservationWithoutPayment(endDate = "30.04.2024")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    private fun reserveTrailerSpace(
        isFixed: Boolean,
        sendInvoice: Boolean
    ) {
        val listingPage = reservationListPage()

        listingPage.createReservation.click()

        val reservationPage = ReserveWinterSpacePage(page, UserType.EMPLOYEE)

        // fill in the filters
        assertThat(reservationPage.emptyDimensionsWarning).isVisible()
        reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Trailer).click()

        reservationPage.widthFilterInput.fill("1.8")
        reservationPage.lengthFilterInput.fill("5.5")

        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        formPage.fillFormAndSubmit {
            assertThat(formPage.storageTypeTextTrailer).isVisible()
            trailerWidthInput.fill("1.8")
            trailerLengthInput.fill("5.5")
            trailerRegistrationNumberInput.fill("ABC-123")
            if (isFixed) {
                formPage.reservationValidityFixedTermRadioButton.click()
                // todo: the end date is wrong, should be 30.04.2024
                assertContains(formPage.reservationValidityInformation.textContent(), "01.04.2024 - 31.12.2024")
            }
        }

        val invoicePreviewPage = InvoicePreviewPage(page)
        assertThat(invoicePreviewPage.header).isVisible()
        if (sendInvoice) {
            invoicePreviewPage.sendButton.click()
        } else {
            invoicePreviewPage.markAsPaid.click()
            invoicePreviewPage.confirmModalSubmit.click()
        }

        val citizenDetailsPage = CitizenDetailsPage(page)
        assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()
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

            val citizenDetailsPage = CitizenDetailsPage(page)
            assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()
            page.waitForCondition { citizenDetailsPage.reservationValidity.count() == 1 }
            assertTrue("Until 31.12.2024" in citizenDetailsPage.reservationValidity.first().textContent())

            assertEmailIsSentOfEmployeesFixedTermSlipReservationWithInvoice()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a fixed term boat space on behalf of a citizen and not invoice it`() {
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
            invoicePreviewPage.markAsPaid.click()
            invoicePreviewPage.confirmModalSubmit.click()

            // we need to give some time for test to be able to evaluate the email sending
            val citizenDetailsPage = CitizenDetailsPage(page)
            assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()
            assertEmailIsSentOfEmployeesFixedTermSlipReservationWithoutPayment()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a fixed term storage space on behalf of a citizen without payment`() {
        try {
            reserveStoragePlace(true, false)

            // we need to give some time for test to be able to evaluate the email sending
            val citizenDetailsPage = CitizenDetailsPage(page)
            assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()
            assertEmailIsSentOfEmployeesFixedTermStorageReservationWithoutPayment(endDate = "14.09.2024")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve a fixed term storage space on behalf of a citizen and send an invoice`() {
        try {
            reserveStoragePlace(true, true)

            // we need to give some time for test to be able to evaluate the email sending
            val citizenDetailsPage = CitizenDetailsPage(page)
            assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()
            assertEmailIsSentOfEmployeesFixedTermStorageReservationWithInvoice(endDate = "14.09.2024")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can reserve an indefinite storage space on behalf of a citizen without payment`() {
        try {
            reserveStoragePlace(false, false)

            // we need to give some time for test to be able to evaluate the email sending
            val citizenDetailsPage = CitizenDetailsPage(page)
            assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()
            assertEmailIsSentOfEmployeesIndefiniteStorageSpaceReservationWithoutPayment()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    private fun reserveStoragePlace(
        isFixed: Boolean,
        sendInvoice: Boolean
    ) {
        val listingPage = reservationListPage()
        listingPage.createReservation.click()

        val reservationPage = ReserveWinterSpacePage(page, UserType.EMPLOYEE)

        // fill in the filters
        reservationPage.boatSpaceTypeSlipRadio(BoatSpaceType.Storage).click()
        reservationPage.amenityTrailerRadioButton.click()
        reservationPage.widthFilterInput.fill("1.8")
        reservationPage.lengthFilterInput.fill("5.5")
        reservationPage.firstReserveButton.click()

        val formPage = BoatSpaceFormPage(page)
        formPage.fillFormAndSubmit {
            assertThat(formPage.storageTypeTextTrailer).isVisible()
            trailerWidthInput.fill("1.8")
            trailerLengthInput.fill("5.5")
            trailerRegistrationNumberInput.fill("ABC-123")
            if (isFixed) {
                formPage.reservationValidityFixedTermRadioButton.click()
                // todo: the end date is wrong, should be 14.09.2024
                assertContains(formPage.reservationValidityInformation.textContent(), "01.04.2024 - 31.12.2024")
            }
        }
        val invoicePreviewPage = InvoicePreviewPage(page)
        assertThat(invoicePreviewPage.header).isVisible()
        if (sendInvoice) {
            invoicePreviewPage.sendButton.click()
        } else {
            invoicePreviewPage.markAsPaid.click()
            invoicePreviewPage.confirmModalSubmit.click()
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
        val citizenDetailsPage = CitizenDetailsPage(page)
        assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()
        val reservationListPage = ReservationListPage(page)
        reservationListPage.navigateTo()
        assertThat(reservationListPage.header).isVisible()
        // Check that the reservation is visible in the list
        assertThat(page.getByText(place)).isVisible()

        messageService.sendScheduledEmails()
        assertEquals(1, SendEmailServiceMock.emails.size)

        assertEmailIsSentOfEmployeesIndefiniteSlipReservationWithInvoice("mikko.virtanen@noreplytest.fi", "Katu 1, 00100")
    }

    @Test
    fun `Employee can reserve a boat space on behalf of an existing citizen with turvakielto`() {
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

        typeText(formPage.citizenSearchInput, "kieltoinen")

        formPage.citizenSearchOption1.click()
        assertThat(page.getByTestId("firstName")).containsText("Turvald")
        assertThat(page.getByTestId("lastName")).containsText("Kieltoinen")

        // Fill in the boat information
        fillBoatAndOtherDetails(formPage)
        formPage.submitButton.click()
        val invoicePage = InvoicePreviewPage(page)
        assertThat(invoicePage.header).isVisible()

        assertThat(page.getByTestId("reserverName")).containsText("Turvald Kieltoinen")
        assertThat(page.getByTestId("reserverAddress")).hasText("Tieto puuttuu")
        val description = page.getByTestId("description").inputValue()
        assertContains(description, place)

        invoicePage.sendButton.click()

        val citizenDetailsPage = CitizenDetailsPage(page)
        assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()

        val reservationListPage = ReservationListPage(page)
        reservationListPage.navigateTo()
        // Check that the reservation is visible in the list
        assertThat(page.getByText(place)).isVisible()

        assertEmailIsSentOfEmployeesIndefiniteSlipReservationWithInvoice("turvald@kieltoinen.fi", "*salainen*")
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
    fun reservingABoatSpaceAsNewOrganization() {
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

        val orgName = "My Organization"
        formPage.organizationRadioButton.click()
        formPage.orgNameInput.fill(orgName)
        formPage.orgBusinessIdInput.fill("1234567-8")
        formPage.orgPhoneNumberInput.fill("123456789")
        formPage.orgEmailInput.fill("foo@bar.com")
        formPage.orgAddressInput.fill("Organisaation käyntiosoite")
        formPage.orgPostalCodeInput.fill("03300")
        formPage.orgCityInput.fill("Espoo")

        val orgBillingName = "Laskun vastaanottaja"
        val orgBillingAddress = "Laskutusosoite 12"
        val orgBillingPostalCode = "02320"
        val orgBillingCity = "Espoo"

        formPage.orgBillingNameInput.fill(orgBillingName)
        formPage.orgBillingAddressInput.fill(orgBillingAddress)
        formPage.orgBillingPostalCodeInput.fill(orgBillingPostalCode)
        formPage.orgBillingCityInput.fill(orgBillingCity)

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

        val citizenDetailsPage = CitizenDetailsPage(page)
        assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()

        val reservationListPage = ReservationListPage(page)
        reservationListPage.navigateTo()
        assertThat(reservationListPage.header).isVisible()

        val expectedInvoiceAddress = "$orgBillingName/$orgName/$orgBillingAddress,$orgBillingPostalCode,$orgBillingCity"
        messageService.sendScheduledEmails()
        // Email is sent to both organization representative and the reserver
        assertEquals(2, SendEmailServiceMock.emails.size)
        assertEmailIsSentOfEmployeesIndefiniteSlipReservationWithInvoice("foo@bar.com", expectedInvoiceAddress, false)
        assertEmailIsSentOfEmployeesIndefiniteSlipReservationWithInvoice("mikko.virtanen@noreplytest.fi", expectedInvoiceAddress, false)
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
        typeText(formPage.citizenSearchInput, "olivia")
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
        val priceWithTax = "418,00"
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
        assertThat(formPage.boatSpacePriceInEuro).containsText("$priceWithTax €")

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
                priceWithTax.replace(
                    ",",
                    "."
                )
            } €."
        )
    }

    @Test
    fun `employee can renew a boat space reservation and send an invoice`() {
        try {
            renewABoatSpaceReservation(true)
            assertEmailIsSentOfEmployeeSlipRenewalWithInvoice("leo@noreplytest.fi")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `employee can renew a boat space reservation without payment`() {
        try {
            renewABoatSpaceReservation(false)
            assertEmailIsSentOfEmployeeSlipRenewalWithoutPayment("leo@noreplytest.fi")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Organization billing name is added to the invoice preview`() {
        EmployeeHomePage(page).employeeLogin()
        val organizationName = "Espoon Pursiseura"
        val billingName = "expected billing name"

        val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)
        reservationPage.navigateTo()
        reservationPage.revealB314BoatSpace()
        reservationPage.reserveTableB314Row.locator(".reserve-button").click()

        val formPage = BoatSpaceFormPage(page)
        formPage.existingCitizenSelector.click()
        typeText(formPage.citizenSearchInput, "olivia")
        page.waitForCondition { formPage.citizenSearchOption1.isVisible }
        formPage.citizenSearchOption1.clickAndWaitForHtmxSettle()

        formPage.organizationRadioButton.click()
        assertThat(formPage.espoonPursiseuraRadioButton).isVisible()
        formPage.espoonPursiseuraRadioButton.click()
        assertThat(formPage.orgBillingNameInput).not().isEmpty()

        formPage.orgBillingNameInput.fill(billingName)
        formPage.fillFormWithPrefilledValuesAndSubmit()

        val invoicePreviewPage = InvoicePreviewPage(page)
        assertThat(invoicePreviewPage.contactPerson).hasValue(billingName)

        invoicePreviewPage.sendButton.click()

        val organizationDetailsPage = OrganizationDetailsPage(page)
        assertThat(organizationDetailsPage.organizationBillingNameField).hasText(billingName)

        messageService.sendScheduledEmails()
        assertEquals(2, SendEmailServiceMock.emails.size)
        assertTrue(SendEmailServiceMock.emails.all { it.body.contains("Lasku lähetetään osoitteeseen $billingName/$organizationName") })
    }

    @Test
    fun `Organization billing name is optional when making reservation`() {
        EmployeeHomePage(page).employeeLogin()
        val organizationName = "Espoon Pursiseura"

        val reservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)
        reservationPage.navigateTo()
        reservationPage.revealB314BoatSpace()
        reservationPage.reserveTableB314Row.locator(".reserve-button").click()

        val formPage = BoatSpaceFormPage(page)
        formPage.existingCitizenSelector.click()
        typeText(formPage.citizenSearchInput, "olivia")
        page.waitForCondition { formPage.citizenSearchOption1.isVisible }
        formPage.citizenSearchOption1.clickAndWaitForHtmxSettle()

        formPage.organizationRadioButton.click()
        assertThat(formPage.espoonPursiseuraRadioButton).isVisible()
        formPage.espoonPursiseuraRadioButton.click()
        assertThat(formPage.orgBillingNameInput).not().isEmpty()

        formPage.orgBillingNameInput.fill("")
        formPage.fillFormWithPrefilledValuesAndSubmit()

        val invoicePreviewPage = InvoicePreviewPage(page)
        assertThat(invoicePreviewPage.contactPerson).hasValue("")

        invoicePreviewPage.sendButton.click()

        val organizationDetailsPage = OrganizationDetailsPage(page)
        assertThat(organizationDetailsPage.organizationBillingNameField).hasText("-")

        messageService.sendScheduledEmails()
        assertEquals(2, SendEmailServiceMock.emails.size)
        assertTrue(SendEmailServiceMock.emails.all { it.body.contains("Lasku lähetetään osoitteeseen $organizationName") })
    }

    @Test
    fun `Employee should not be bound by citizen boat count limits`() {
        // create maximum amount of boats for citizen
        // uses citizen frontend because employee does not have back button
        CitizenHomePage(page).loginAsEspooCitizenWithoutReservations()

        val citizenReservationPage = CitizenReserveBoatSpacePage(page)
        val citizenFormPage = CitizenBoatSpaceFormPage(page)
        val citizenPaymentPage = CitizenPaymentPage(page)

        citizenReservationPage.navigateToPage()
        citizenReservationPage.startReservingBoatSpaceB314()

        citizenFormPage.fillFormAndSubmit()

        for (i in 1..BoatSpaceConfig.MAX_CITIZEN_BOATS) {
            assertThat(citizenPaymentPage.header).isVisible()
            citizenPaymentPage.backButton.click()

            assertThat(citizenFormPage.header).isVisible()
            citizenFormPage.fillFormAndSubmit {
                val boatNumber = i + 1
                val boatSection = getBoatSection()
                boatSection.newBoatSelection.click()
                boatSection.depthInput.fill("1.5")
                boatSection.weightInput.fill("2000")
                boatSection.nameInput.fill("My Boat $boatNumber")
                boatSection.otherIdentifierInput.fill("B$boatNumber")
                boatSection.noRegistrationCheckbox.check()
                boatSection.ownerRadio.check()
            }
        }

        val citizenErrorModal = citizenFormPage.getErrorModal()
        assertThat(citizenErrorModal.root).isVisible()
        citizenErrorModal.okButton.click()

        citizenFormPage.cancelButton.click()
        citizenFormPage.getConfirmCancelReservationModal().confirmButton.click()
        assertThat(citizenReservationPage.header).isVisible()

        // create boat as employee
        EmployeeHomePage(page).employeeLogin()
        val employeeReservationPage = ReserveBoatSpacePage(page, UserType.EMPLOYEE)
        val employeeFormPage = BoatSpaceFormPage(page)
        val employeeInvoicePage = InvoicePreviewPage(page)

        employeeReservationPage.navigateTo()
        employeeReservationPage.revealB314BoatSpace()
        employeeReservationPage.reserveTableB314Row.locator(".reserve-button").click()

        employeeFormPage.existingCitizenSelector.click()
        typeText(employeeFormPage.citizenSearchInput, "mikko")
        page.waitForCondition { employeeFormPage.citizenSearchOption1.isVisible }
        employeeFormPage.citizenSearchOption1.clickAndWaitForHtmxSettle()
        fillBoatAndOtherDetails(employeeFormPage)
        employeeFormPage.submitButton.click()

        assertThat(employeeInvoicePage.header).isVisible()
    }

    private fun renewABoatSpaceReservation(sendInvoice: Boolean) {
        mockTimeProvider(timeProvider, LocalDateTime.of(2025, 1, 7, 0, 0, 0))
        val listingPage = reservationListPage()

        listingPage.boatSpace1.click()
        val citizenDetails = CitizenDetailsPage(page)
        assertThat(citizenDetails.citizenDetailsSection).isVisible()

        citizenDetails.renewReservationButton(1).click()
        val invoiceDetails = InvoicePreviewPage(page)
        assertThat(invoiceDetails.header).isVisible()
        invoiceDetails.cancelButton.click()
        assertThat(citizenDetails.citizenDetailsSection).isVisible()
        citizenDetails.renewReservationButton(1).click()
        assertThat(invoiceDetails.header).isVisible()
        invoiceDetails.priceWithTax.fill("101")
        invoiceDetails.description.fill("Test description")

        if (sendInvoice) {
            invoiceDetails.sendButton.click()
        } else {
            invoiceDetails.markAsPaid.click()
            invoiceDetails.confirmModalSubmit.click()
        }
        assertThat(citizenDetails.renewReservationButton(1)).isHidden()
        assertThat(citizenDetails.reservationListCards).containsText("Boat space: Haukilahti B 001")

        citizenDetails.paymentsNavi.click()

        citizenDetails.paymentsTable.textContent().contains("101,00")
        citizenDetails.paymentsTable.textContent().contains("Test description")
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
