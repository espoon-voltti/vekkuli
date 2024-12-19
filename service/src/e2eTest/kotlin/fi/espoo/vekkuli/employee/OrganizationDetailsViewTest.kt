package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.pages.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipRenewPeriod
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class OrganizationDetailsViewTest : PlaywrightTest() {
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

    @Test
    fun userMemos() {
        try {
            EmployeeHomePage(page).employeeLogin()

            val organizationDetails = OrganizationDetailsPage(page)
            organizationDetails.navigateToEspoonPursiseura()
            assertThat(organizationDetails.organizationDetailsSection).isVisible()
            organizationDetails.memoNavi.click()

            // Add memo, added the waitFor which seemed to fix the flakiness
            organizationDetails.addNewMemoBtn.waitFor()
            organizationDetails.addNewMemoBtn.click()
            val text = "This is a new memo"
            val memoId = 2
            organizationDetails.newMemoContent.fill(text)
            organizationDetails.newMemoSaveBtn.click()
            assertThat(organizationDetails.userMemo(memoId)).containsText(text)

            // Edit memo
            val newText = "Edited memo"
            organizationDetails.userMemo(memoId).getByTestId("edit-memo-button").click()
            organizationDetails.userMemo(memoId).getByTestId("edit-memo-content").fill(newText)
            organizationDetails.userMemo(memoId).getByTestId("save-edit-button").click()
            assertThat(organizationDetails.userMemo(memoId).locator(".memo-content")).containsText(newText)

            // Delete memo
            page.onDialog { it.accept() }
            organizationDetails.userMemo(memoId).getByTestId("delete-memo-button").click()
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
            organizationDetails.organizationNameInput.fill("")
            organizationDetails.organizationBusinessIdInput.fill("")
            organizationDetails.organizationMunicipalityInput.selectOption(organizationMunicipalityCode)
            organizationDetails.organizationPhoneInput.fill(organizationPhone)
            organizationDetails.organizationEmailInput.fill(organizationEmail)
            organizationDetails.organizationAddressInput.fill(organizationAddress)
            organizationDetails.organizationPostalCodeInput.fill(organizationPostalCode)
            organizationDetails.organizationPostOfficeInput.fill(organizationPostOffice)
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
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
