package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
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
            EmployeeHomePage(page).employeeLogin()

            val organizationDetails = OrganizationDetailsPage(page)
            organizationDetails.navigateToEspoonPursiseura()
            assertThat(organizationDetails.organizationDetailsSection).isVisible()
            organizationDetails.showAllBoatsButton.click()

            val boatId = 6
            page.getByTestId("edit-boat-$boatId").click()
            assertThat(page.getByTestId("form")).isVisible()

            organizationDetails.nameInput.fill("New Boat Name")
            organizationDetails.weightInput.fill("2000")
            organizationDetails.typeSelect.selectOption("Sailboat")
            organizationDetails.depthInput.fill("1.5")
            organizationDetails.widthInput.fill("3")
            organizationDetails.registrationNumberInput.fill("ABC123")
            organizationDetails.length.fill("6")
            organizationDetails.ownership.selectOption("Owner")
            organizationDetails.otherIdentifier.fill("ID12345")
            organizationDetails.extraInformation.fill("Extra info")

            organizationDetails.submitButton.click()
            assertThat(organizationDetails.nameText(boatId)).hasText("New Boat Name")
            assertThat(organizationDetails.weightText(boatId)).hasText("2000")
            assertThat(organizationDetails.typeText(boatId)).hasText("Sailboat")
            assertThat(organizationDetails.depthText(boatId)).hasText("1.50")
            assertThat(organizationDetails.widthText(boatId)).hasText("3.00")
            assertThat(organizationDetails.registrationNumberText(boatId)).hasText("ABC123")

            assertThat(organizationDetails.lengthText(boatId)).hasText("6.00")
            assertThat(organizationDetails.ownershipText(boatId)).hasText("Owner")
            assertThat(organizationDetails.otherIdentifierText(boatId)).hasText("ID12345")
            assertThat(organizationDetails.extraInformationText(boatId)).hasText("Extra info")

            // delete the boat
            page.getByTestId("delete-boat-$boatId").click()
            page.getByTestId("delete-modal-confirm-$boatId").click()
            assertThat(page.getByTestId("boat-$boatId")).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
