package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.ReserveTest
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.pages.employee.InvoicePreviewPage
import fi.espoo.vekkuli.pages.employee.OrganizationDetailsPage
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.shared.CitizenIds
import fi.espoo.vekkuli.shared.OrganizationIds
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipRenewPeriod
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@ActiveProfiles("test")
class OrganizationDetailsViewTest : ReserveTest() {
    @Test
    fun `employee can renew slip reservation for organization`() {
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
            assertThat(invoiceDetails.reserverName).hasText("Espoon Pursiseura")

            invoiceDetails.sendButton.click()
            assertThat(organizationDetailsPage.renewReservationButton(reservationId)).isHidden()
            assertThat(organizationDetailsPage.reservationListCards).containsText("Boat space: Haukilahti B 005")

            messageService.sendScheduledEmails()
            assertEquals(2, SendEmailServiceMock.emails.size)
            assertEmailIsSentOfEmployeeSlipRenewalWithInvoice("olivia@noreplytest.fi", false)
            assertEmailIsSentOfEmployeeSlipRenewalWithInvoice("eps@noreplytest.fi", false)
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
            organizationDetails.editBoatButton(boatId).click()
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
            assertThat(organizationDetails.depthText(boatId)).hasText("1,50")
            assertThat(organizationDetails.widthText(boatId)).hasText("3,00")
            assertThat(organizationDetails.registrationNumberText(boatId)).hasText("ABC123")

            assertThat(organizationDetails.lengthText(boatId)).hasText("6,00")
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

    @Test
    fun `Employee editing a organization boat do not trigger weight warning`() {
        EmployeeHomePage(page).employeeLogin()

        val organizationDetails = OrganizationDetailsPage(page)
        organizationDetails.navigateToEspoonPursiseura()

        val boatId = 5
        organizationDetails.editBoatButton(boatId).click()
        assertThat(page.getByTestId("form")).isVisible()

        organizationDetails.weightInput.fill("16000")
        organizationDetails.submitButton.click()
        assertThat(organizationDetails.weightText(boatId)).hasText("16000")

        page.reload() // warnings show only after full reload
        assertThat(organizationDetails.acknowledgeWarningButton(boatId)).not().isVisible()
    }

    @Test
    fun `Organization billing name is optional when editing organization`() {
        EmployeeHomePage(page).employeeLogin()

        val organizationDetailsPage = OrganizationDetailsPage(page)
        organizationDetailsPage.navigateToEspoonPursiseura()

        assertThat(organizationDetailsPage.organizationBillingNameField).isVisible()
        assertThat(organizationDetailsPage.organizationBillingNameField).not().hasText("-")

        organizationDetailsPage.editButton.click()
        organizationDetailsPage.organizationBillingNameInput.fill("")
        organizationDetailsPage.organizationEditSubmitButton.click()
        assertThat(organizationDetailsPage.organizationBillingNameField).isVisible()
        assertThat(organizationDetailsPage.organizationBillingNameField).hasText("-")
    }

    @Test
    fun userMemos() {
        try {
            EmployeeHomePage(page).employeeLogin()

            val organizationDetails = OrganizationDetailsPage(page)
            organizationDetails.navigateToEspoonPursiseura()
            assertThat(organizationDetails.organizationDetailsSection).isVisible()
            organizationDetails.memoNavi.clickAndWaitForHtmxSettle()

            organizationDetails.addNewMemoBtn.clickAndWaitForHtmxSettle()
            val text = "This is a new memo"
            val memoId = 2
            organizationDetails.newMemoContent.fill(text)
            organizationDetails.newMemoSaveBtn.clickAndWaitForHtmxSettle()
            assertThat(organizationDetails.userMemo(memoId)).containsText(text)

            // Edit memo
            val newText = "Edited memo"
            organizationDetails.userMemo(memoId).getByTestId("edit-memo-button").clickAndWaitForHtmxSettle()
            organizationDetails.userMemo(memoId).getByTestId("edit-memo-content").fill(newText)
            organizationDetails.userMemo(memoId).getByTestId("save-edit-button").clickAndWaitForHtmxSettle()
            assertThat(organizationDetails.userMemo(memoId).locator(".memo-content")).containsText(newText)

            // Delete memo
            page.onDialog { it.accept() }
            organizationDetails.userMemo(memoId).getByTestId("delete-memo-button").clickAndWaitForHtmxSettle()
            assertThat(organizationDetails.userMemo(memoId)).hasCount(0)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun userMessages() {
        try {
            EmployeeHomePage(page).employeeLogin()
            val organizationDetails = OrganizationDetailsPage(page)
            organizationDetails.navigateToEspoonPursiseura()
            assertThat(organizationDetails.organizationDetailsSection).isVisible()
            organizationDetails.messagesNavi.click()
            assertThat(organizationDetails.messages).containsText("Käyttöveden katko")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `employee navigates to organization details page and terminates reservation`() {
        try {
            EmployeeHomePage(page).employeeLogin()
            val organizationDetails = OrganizationDetailsPage(page)
            organizationDetails.navigateToEspoonPursiseura()

            assertThat(organizationDetails.firstBoatSpaceReservationCard).isVisible()
            assertThat(organizationDetails.expiredReservationList)
            organizationDetails.terminateReservationAsEmployeeButton.click()
            assertThat(organizationDetails.terminateReservationAsEmployeeForm).isVisible()
            assertThat(organizationDetails.terminateReservationModalConfirm).isVisible()
            assertThat(organizationDetails.terminateReservationModalCancel).isVisible()

            assertThat(organizationDetails.locationNameInFirstBoatSpaceReservationCard).hasText("Haukilahti")
            assertThat(organizationDetails.placeInFirstBoatSpaceReservationCard).hasText("B 005")

            // Opens up information from the first reservation of the first user
            assertThat(organizationDetails.terminateReservationFormLocation).hasText("Haukilahti B 005")
            assertThat(organizationDetails.terminateReservationFormSize).hasText("2.50 x 4.50 m")
            assertThat(organizationDetails.terminateReservationFormAmenity).hasText("Beam")

            organizationDetails.terminateReservationModalCancel.click()
            assertThat(organizationDetails.terminateReservationForm).not().isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun editOrganization() {
        try {
            EmployeeHomePage(page).employeeLogin()
            val organizationDetails = OrganizationDetailsPage(page)
            organizationDetails.navigateToEspoonPursiseura()
            assertThat(organizationDetails.organizationDetailsSection).isVisible()
            organizationDetails.editButton.click()

            assertThat(page.getByTestId("edit-organization-form")).isVisible()
            val organizationName = "New name"
            val organizationPhone = "0405839281"
            val organizationEmail = "organization@email.com"
            val organizationAddress = "New Address"
            val organizationBusinessId = "1234567-8"
            val organizationPostalCode = "12345"
            val organizationPostOffice = "Espoo"
            val organizationMunicipalityCode = "91"
            val billingName = "Espoo laskutus"
            val billingAddress = "Laskukatu 12"
            val billingPostalCode = "20220"
            val billingPostOffice = "Espoo"
            organizationDetails.organizationNameInput.fill("")
            organizationDetails.organizationBusinessIdInput.fill("")
            organizationDetails.organizationMunicipalityInput.selectOption(organizationMunicipalityCode)
            organizationDetails.organizationPhoneInput.fill(organizationPhone)
            organizationDetails.organizationEmailInput.fill(organizationEmail)
            organizationDetails.organizationAddressInput.fill(organizationAddress)
            organizationDetails.organizationPostalCodeInput.fill(organizationPostalCode)
            organizationDetails.organizationPostOfficeInput.fill(organizationPostOffice)

            organizationDetails.organizationBillingNameInput.fill(billingName)
            organizationDetails.organizationBillingAddressInput.fill(billingAddress)
            organizationDetails.organizationBillingPostalCodeInput.fill(billingPostalCode)
            organizationDetails.organizationBillingPostOfficeInput.fill(billingPostOffice)

            organizationDetails.organizationEditSubmitButton.click()

            // assert that email and phone can not be empty
            assertThat(organizationDetails.organizationBusinessIdError).isVisible()
            assertThat(organizationDetails.organizationNameError).isVisible()

            organizationDetails.organizationNameInput.fill(organizationName)
            organizationDetails.organizationBusinessIdInput.fill(organizationBusinessId)
            organizationDetails.organizationEditSubmitButton.click()

            // assert that the values are updated
            assertThat(organizationDetails.organizationFirstNameField).hasText(organizationName)
            assertThat(organizationDetails.organizationPhoneField).hasText(organizationPhone)
            assertThat(organizationDetails.organizationEmailField).hasText(organizationEmail)
            assertThat(
                organizationDetails.organizationAddressField
            ).hasText("$organizationAddress, $organizationPostalCode, $organizationPostOffice")
            assertThat(organizationDetails.organizationBusinessIdField).hasText(organizationBusinessId)

            assertThat(organizationDetails.organizationMunicipalityField).hasText("Helsinki")

            assertThat(organizationDetails.organizationBillingNameField).hasText(billingName)
            assertThat(
                organizationDetails.organizationBillingAddressField
            ).hasText("$billingAddress, $billingPostalCode, $billingPostOffice")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `can remove a member`() {
        try {
            EmployeeHomePage(page).employeeLogin()
            val organizationDetails = OrganizationDetailsPage(page)
            organizationDetails.navigateToEspoonPursiseura()
            assertThat(organizationDetails.organizationDetailsSection).isVisible()
            organizationDetails.removeOliviaButton.click()
            assertThat(organizationDetails.confirmOliviaRemove).isVisible()
            organizationDetails.confirmOliviaRemove.click()
            assertThat(organizationDetails.removeOliviaButton).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `employee can add an existing citizen to organization`() {
        try {
            EmployeeHomePage(page).employeeLogin()
            val organizationDetails = OrganizationDetailsPage(page)
            organizationDetails.navigateToEspoonPursiseura()
            assertThat(organizationDetails.addOrganizationContainer).isVisible()

            // Test that cancel works
            organizationDetails.addMemberButton.click()
            assertThat(organizationDetails.addMemberSearchContainer).isVisible()
            organizationDetails.cancelOrganizationMemberAdd.click()
            assertThat(organizationDetails.addMemberSearchContainer).not().isVisible()

            // Make sure that the citizen is not in the organization
            organizationDetails.addMemberButton.click()
            assertThat(organizationDetails.citizenSearchContainer).isVisible()
            assertThat(organizationDetails.organizationMemberTableBody).not().containsText("Mikko Virtanen")

            // Add the citizen to the organization
            typeText(organizationDetails.citizenSearchInput, "mikko")
            assertThat(organizationDetails.citizenSearchOption1).isVisible()
            organizationDetails.citizenSearchOption1.click()

            // Check that the citizen is added to the organization
            assertThat(organizationDetails.citizenNameField).isVisible()
            assertThat(organizationDetails.citizenPhoneField).isVisible()
            assertThat(organizationDetails.citizenEmailField).isVisible()
            organizationDetails.submitOrganizationMemberAdd.click()
            assertThat(organizationDetails.organizationMemberTableBody).containsText("Mikko Virtanen")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `organization details should shield against XSS scripts when in edit mode`() {
        try {
            EmployeeHomePage(page).employeeLogin()
            val organizationDetails = OrganizationDetailsPage(page)

            // Inject XSS scripts to organization information from organizations details page and return assertions
            val assertions = injectXSSToOrganizationInformation(page, OrganizationIds.espoonPursiseura)

            // Make sure htmx has settled
            assertThat(organizationDetails.editButton).isVisible()
            // The script was run setting to edit mode after the injection
            organizationDetails.editButton.click()
            // Make sure htmx has settled
            assertThat(organizationDetails.organizationNameInput).isVisible()

            assertions()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `organization members should shield against XSS scripts when in details page`() {
        try {
            EmployeeHomePage(page).employeeLogin()
            val organizationDetails = OrganizationDetailsPage(page)
            // Inject XSS scripts to citizen information from citizen details page and return assertions
            val assertions = injectXSSToCitizenInformation(page, CitizenIds.citizenInEspoonPursiseura)

            organizationDetails.navigateToPage(OrganizationIds.espoonPursiseura)

            // Make sure htmx has settled
            assertThat(organizationDetails.editButton).isVisible()
            organizationDetails.editButton.click()
            // Make sure htmx has settled
            assertThat(organizationDetails.organizationNameInput).isVisible()

            assertions()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
