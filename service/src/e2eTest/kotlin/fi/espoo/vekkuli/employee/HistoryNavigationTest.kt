package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.pages.employee.*
import fi.espoo.vekkuli.shared.CitizenIds
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.UUID

@ActiveProfiles("test")
@EnabledIf(value = "fi.espoo.vekkuli.employee.HistoryNavigationTest#isLocal", disabledReason = "HistoryNavigationTests only works locally")
class HistoryNavigationTest : PlaywrightTest() {
    companion object {
        @JvmStatic
        fun isLocal(): Boolean = System.getenv("ENVIRONMENT") == null || System.getenv("ENVIRONMENT") == "local"
    }

    private val listingPageUrl = "$baseUrl/virkailija/venepaikat/varaukset"
    private val reservePageUrl = "$baseUrl/virkailija/venepaikat"

    private fun citizenDetailsPageUrl(citizenId: UUID) = "$baseUrl/virkailija/kayttaja/$citizenId"

    private fun invoicePageUrl(reservationId: Int) = "$baseUrl/virkailija/venepaikka/varaus/$reservationId/lasku"

    private val defaultReservationId = 10

    @Test
    fun `canceling new reservation should navigate back to reservation list`() {
        val invoicePreviewPage = createReservationInPreviewInvoiceState()
        page.waitForURL(invoicePageUrl(defaultReservationId))
        invoicePreviewPage.cancelButton.click()

        page.waitForURL(listingPageUrl)
        assertThat(ReservationListPage(page).header).isVisible()
    }

    @Test
    fun `reserving with invoice a new reservation should navigate to citizen details`() {
        val citizenDetailsPage = CitizenDetailsPage(page)
        val invoicePreviewPage = createReservationInPreviewInvoiceState()
        page.waitForURL(invoicePageUrl(defaultReservationId))
        invoicePreviewPage.sendButton.click()

        page.waitForURL(citizenDetailsPageUrl(CitizenIds.leo))
        assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()

        citizenDetailsPage.backButton.click()
        page.waitForURL(reservePageUrl)
        assertThat(ReserveBoatSpacePage(page, UserType.EMPLOYEE).header).isVisible()
    }

    @Test
    fun `reserving without invoice a new reservation should navigate to citizen details`() {
        val citizenDetailsPage = CitizenDetailsPage(page)
        val invoicePreviewPage = createReservationInPreviewInvoiceState()
        page.waitForURL(invoicePageUrl(defaultReservationId))
        invoicePreviewPage.markAsPaid.click()
        invoicePreviewPage.confirmModalSubmit.click()

        page.waitForURL(citizenDetailsPageUrl(CitizenIds.leo))
        assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()

        citizenDetailsPage.backButton.click()
        page.waitForURL(reservePageUrl)
        assertThat(ReserveBoatSpacePage(page, UserType.EMPLOYEE).header).isVisible()
    }

    @Test
    fun `canceling reservation renewing should navigate back to citizen details`() {
        val citizenDetailsPage = CitizenDetailsPage(page)
        val invoicePreviewPage = startRenewingReservation()
        page.waitForURL(citizenDetailsPageUrl(CitizenIds.leo))
        invoicePreviewPage.cancelButton.click()

        page.waitForURL(citizenDetailsPageUrl(CitizenIds.leo))
        assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()

        citizenDetailsPage.backButton.click()
        page.waitForURL(listingPageUrl)
        assertThat(ReservationListPage(page).header).isVisible()
    }

    @Test
    fun `renewing reservation with invoice should navigate back to citizen details`() {
        val citizenDetailsPage = CitizenDetailsPage(page)
        val invoicePreviewPage = startRenewingReservation()
        page.waitForURL(citizenDetailsPageUrl(CitizenIds.leo))
        invoicePreviewPage.sendButton.click()

        page.waitForURL(citizenDetailsPageUrl(CitizenIds.leo))
        assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()

        citizenDetailsPage.backButton.click()
        page.waitForURL(listingPageUrl)
        assertThat(ReservationListPage(page).header).isVisible()
    }

    @Test
    fun `renewing reservation without invoice should navigate back to citizen details`() {
        val citizenDetailsPage = CitizenDetailsPage(page)
        val invoicePreviewPage = startRenewingReservation()
        page.waitForURL(citizenDetailsPageUrl(CitizenIds.leo))
        invoicePreviewPage.markAsPaid.click()
        invoicePreviewPage.confirmModalSubmit.click()

        page.waitForURL(citizenDetailsPageUrl(CitizenIds.leo))
        assertThat(citizenDetailsPage.citizenDetailsSection).isVisible()

        citizenDetailsPage.backButton.click()
        page.waitForURL(listingPageUrl)
        assertThat(ReservationListPage(page).header).isVisible()
    }

    private fun createReservationInPreviewInvoiceState(): InvoicePreviewPage {
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
        formPage.existingCitizenSelector.click()
        typeText(formPage.citizenSearchInput, "leo")
        formPage.citizenSearchOption1.clickAndWaitForHtmxSettle()
        assertThat(page.getByTestId("firstName")).containsText("Leo")
        assertThat(page.getByTestId("lastName")).containsText("Korhonen")

        formPage.boatTypeSelect.selectOption("Sailboat")
        formPage.widthInput.fill("3")
        formPage.lengthInput.fill("5")
        formPage.depthInput.fill("1.5")
        formPage.weightInput.fill("2000")
        formPage.boatNameInput.fill("My Boat")
        formPage.otherIdentification.fill("ID12345")
        formPage.noRegistrationCheckbox.check()
        formPage.ownerRadioButton.check()
        formPage.certifyInfoCheckbox.check()
        formPage.agreementCheckbox.check()
        formPage.submitButton.click()

        val invoicePreviewPage = InvoicePreviewPage(page)
        assertThat(invoicePreviewPage.header).isVisible()
        return invoicePreviewPage
    }

    private fun startRenewingReservation(): InvoicePreviewPage {
        mockTimeProvider(timeProvider, LocalDateTime.of(2025, 1, 7, 0, 0, 0))

        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin()

        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        listingPage.boatSpace1.click()

        val citizenDetails = CitizenDetailsPage(page)
        assertThat(citizenDetails.citizenDetailsSection).isVisible()

        citizenDetails.renewReservationButton(1).click()

        val invoicePreviewPage = InvoicePreviewPage(page)
        assertThat(invoicePreviewPage.header).isVisible()
        return invoicePreviewPage
    }
}
