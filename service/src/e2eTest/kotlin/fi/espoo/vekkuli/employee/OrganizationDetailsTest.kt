package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.citizenPageInEnglish
import fi.espoo.vekkuli.pages.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipRenewPeriod
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class OrganizationDetailsTest : PlaywrightTest() {
    @Test
    fun `employee can renew slip reservation`() {
        try {
            mockTimeProvider(timeProvider, startOfSlipRenewPeriod)

            EmployeeHomePage(page).employeeLogin()

            val organizationDetailsPage = OrganizationDetailsPage(page)

            organizationDetailsPage.navigateToEspoonPursiseura()

            assertThat(organizationDetailsPage.organizationDetailsSection).isVisible()

            val reservationId = 7
            organizationDetailsPage.renewReservationButton(reservationId).click()
            val invoiceDetails = InvoicePreviewPage(page)
            assertThat(invoiceDetails.header).isVisible()
            invoiceDetails.cancelButton.click()

            assertThat(organizationDetailsPage.organizationDetailsSection).isVisible()
            organizationDetailsPage.renewReservationButton(reservationId).click()
            assertThat(invoiceDetails.header).isVisible()
            invoiceDetails.sendButton.click()
            assertThat(organizationDetailsPage.invoicePaidButton).isVisible()
            assertThat(organizationDetailsPage.renewReservationButton(reservationId)).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `employee can edit organizations own boat`() {
        try {
            CitizenHomePage(page).loginAsLeoKorhonen()

            page.navigate(citizenPageInEnglish)

            val citizenDetails = CitizenDetailsPage(page)
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            citizenDetails.showAllBoatsButton.click()
            page.getByTestId("edit-boat-3").click()
            assertThat(page.getByTestId("form")).isVisible()

            citizenDetails.nameInput.fill("New Boat Name")
            citizenDetails.weightInput.fill("2000")
            citizenDetails.typeSelect.selectOption("Sailboat")
            citizenDetails.depthInput.fill("1.5")
            citizenDetails.widthInput.fill("3")
            citizenDetails.registrationNumberInput.fill("ABC123")
            citizenDetails.length.fill("6")
            citizenDetails.ownership.selectOption("Owner")
            citizenDetails.otherIdentifier.fill("ID12345")
            citizenDetails.extraInformation.fill("Extra info")

            citizenDetails.submitButton.click()
            assertThat(citizenDetails.nameText(3)).hasText("New Boat Name")
            assertThat(citizenDetails.weightText(3)).hasText("2000")
            assertThat(citizenDetails.typeText(3)).hasText("Sailboat")
            assertThat(citizenDetails.depthText(3)).hasText("1.50")
            assertThat(citizenDetails.widthText(3)).hasText("3.00")
            assertThat(citizenDetails.registrationNumberText(3)).hasText("ABC123")

            assertThat(citizenDetails.lengthText(3)).hasText("6.00")
            assertThat(citizenDetails.ownershipText(3)).hasText("I own the boat")
            assertThat(citizenDetails.otherIdentifierText(3)).hasText("ID12345")
            assertThat(citizenDetails.extraInformationText(3)).hasText("Extra info")

            // delete the boat
            page.getByTestId("delete-boat-3").click()
            page.getByTestId("delete-modal-confirm-3").click()
            assertThat(page.getByTestId("boat-3")).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should add warning when citizen edits boat to be too heavy`() {
        try {
            CitizenHomePage(page).loginAsLeoKorhonen()

            page.navigate(citizenPageInEnglish)

            val citizenDetails = CitizenDetailsPage(page)
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            citizenDetails.showAllBoatsButton.click()
            page.getByTestId("edit-boat-1").click()
            assertThat(page.getByTestId("form")).isVisible()

            citizenDetails.nameInput.fill("New Boat Name")
            citizenDetails.weightInput.fill("16000")
            citizenDetails.typeSelect.selectOption("Sailboat")
            citizenDetails.depthInput.fill("1.5")
            citizenDetails.widthInput.fill("2")
            citizenDetails.registrationNumberInput.fill("ABC123")
            citizenDetails.length.fill("5")
            citizenDetails.ownership.selectOption("Owner")
            citizenDetails.otherIdentifier.fill("ID12345")
            citizenDetails.extraInformation.fill("Extra info")
            citizenDetails.submitButton.click()

            val employeeHomePage = EmployeeHomePage(page)
            employeeHomePage.employeeLogin()

            val listingPage = ReservationListPage(page)
            listingPage.navigateTo()

            assertThat(listingPage.warningIcon).isVisible()

            listingPage.boatSpace1.click()

            citizenDetails.acknowledgeWarningButton(1).click()
            assertThat(citizenDetails.boatWarningModalWeightInput).isVisible()
            citizenDetails.boatWarningModalWeightInput.click()
            val infoText = "Test info"
            citizenDetails.boatWarningModalInfoInput.fill(infoText)
            citizenDetails.boatWarningModalConfirmButton.click()

            citizenDetails.memoNavi.click()
            assertThat(citizenDetails.userMemo(2)).containsText(infoText)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
