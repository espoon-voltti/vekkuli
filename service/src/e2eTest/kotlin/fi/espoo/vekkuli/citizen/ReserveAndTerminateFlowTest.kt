package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.EmailSendingTest
import fi.espoo.vekkuli.pages.citizen.BoatSpaceFormPage
import fi.espoo.vekkuli.pages.citizen.CitizenDetailsPage
import fi.espoo.vekkuli.pages.citizen.CitizenHomePage
import fi.espoo.vekkuli.pages.citizen.PaymentPage
import fi.espoo.vekkuli.pages.citizen.ReserveBoatSpacePage
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.pages.employee.OrganizationDetailsPage
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ActiveProfiles("test")
class ReserveAndTerminateFlowTest : EmailSendingTest() {
    final val terminatorName = "Mikko Virtanen"
    final val expectedHarbour = "Haukilahti"
    final val expectedReservationId = "B 314"
    val expectedTerminationLocation = "$expectedHarbour $expectedReservationId"

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun `citizen can reserve a slip boat space and terminate it to allow others to see it, termination email is sent to citizen and employee`() {
        val reserverName = "Virtanen Mikko"
        val recipientsAddresses = listOf("test@example.com")
        reserveAndTerminateBoatSpace(recipientsAddresses, false)

        // validate that termination email is sent to both the citizen and to employee
        assertTerminationEmailIsSentToCitizenAndEmployee(
            "laituripaikka",
            expectedTerminationLocation,
            terminatorName,
            reserverName
        )
    }

    @Test
    fun `terminating organization reservation sends email to all organization members and to employee`() {
        val reserverName = "Espoon Pursiseura"
        val recipientsAddresses = listOf("olivia@noreplytest.fi", "test@example.com", "eps@noreplytest.fi")
        addMikkoVirtanenToOrganization()
        reserveAndTerminateBoatSpace(recipientsAddresses, true)

        // validate that termination email is sent to organization, all its members and to employee
        assertTerminationEmailIsSentToCitizenAndEmployee(
            "laituripaikka",
            expectedTerminationLocation,
            terminatorName,
            reserverName,
            recipientsAddresses
        )
    }

    private fun addMikkoVirtanenToOrganization() {
        EmployeeHomePage(page).employeeLogin()
        val organizationDetails = OrganizationDetailsPage(page)
        organizationDetails.navigateToEspoonPursiseura()
        assertThat(organizationDetails.addOrganizationContainer).isVisible()

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
        organizationDetails.submitOrganizationMemberAdd.click()
        assertThat(organizationDetails.organizationMemberTableBody).containsText("Mikko Virtanen")
    }

    private fun reserveAndTerminateBoatSpace(
        recipientAddresses: List<String>,
        forOrganization: Boolean
    ) {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod.minusYears(1))

        val citizenDetailsPage = CitizenDetailsPage(page)
        val reserveBoatSpacePage = ReserveBoatSpacePage(page)

        CitizenHomePage(page).loginAsMikkoVirtanen()

        // Create a reservation for B 314 boat space
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()
        BoatSpaceFormPage(page).fillFormAndSubmit {
            if (forOrganization) {
                val organizationSection = getOrganizationSection()
                organizationSection.reserveForOrganization.click()
                organizationSection.organization("Espoon Pursiseura").click()
                page.getByText("Espoon lohi").click()
            }
        }
        PaymentPage(page).payReservation()

        citizenDetailsPage.navigateToPage()

        messageService.sendScheduledEmails()
        assertEquals(recipientAddresses.size, SendEmailServiceMock.emails.size)

        if (forOrganization) {
            recipientAddresses.forEach { address ->
                assertEmailIsSentOfCitizensFixedTermSlipReservation(
                    address,
                    sendAndAssertSendCount = false,
                )
            }
        } else {
            recipientAddresses.forEach { address ->
                assertEmailIsSentOfCitizensIndefiniteSlipReservation(
                    address,
                    sendAndAssertSendCount = false
                )
            }
        }
        SendEmailServiceMock.resetEmails()

        if (forOrganization) {
            citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()
        }

        val firstReservationSection = citizenDetailsPage.getReservationSection(expectedReservationId.toString())
        assertThat(firstReservationSection.place).hasText(expectedReservationId)

        // Check that the boat space is not available for reservation anymore
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.filterForBoatSpaceB314()

        val searchResultsSection = reserveBoatSpacePage.getSearchResultsSection()
        assertThat(searchResultsSection.firstReserveButton).isVisible()
        assertThat(searchResultsSection.b314ReserveButton).not().isVisible()

        // Terminate the reservation
        citizenDetailsPage.navigateToPage()
        if (forOrganization) {
            citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()
        }
        assertThat(firstReservationSection.place).hasText(expectedReservationId)

        // Opens up information from the first reservation and confirms it's the same we just reserved
        firstReservationSection.terminateButton.click()
        val terminateReservationModal = citizenDetailsPage.getTerminateReservationModal()
        assertThat(terminateReservationModal.root).isVisible()
        assertThat(terminateReservationModal.placeIdentifierText).hasText(expectedTerminationLocation)

        // Terminate reservation and check for success message
        terminateReservationModal.confirmButton.click()

        // Check that the boat space is in expired reservations
        citizenDetailsPage.showExpiredReservationsToggle.click()
        assertThat(citizenDetailsPage.expiredReservationList).isVisible()

        val firstExpiredReservationSection = citizenDetailsPage.getFirstExpiredReservationSection()
        assertThat(firstExpiredReservationSection.place).hasText(expectedReservationId)

        // Check that the boat space is available for reservation again
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.filterForBoatSpaceB314()
        assertThat(searchResultsSection.b314ReserveButton).isVisible()
    }

    @Test
    fun `citizen can reserve a storage boat space and terminate it to allow others to see it`() {
        mockTimeProvider(timeProvider, startOfStorageReservationPeriod)
        val citizenHomePage = CitizenHomePage(page)
        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        val expectedHarbour = "Haukilahti"
        val expectedReservationId = "B 007"
        val expectedTerminationLocation = "$expectedHarbour $expectedReservationId"
        citizenHomePage.loginAsLeoKorhonen()
        citizenHomePage.navigateToPage()
        citizenHomePage.languageSelector.click()
        citizenHomePage.languageSelector.getByText("Suomi").click()
        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()

        val filterSection = reservationPage.getFilterSection()
        filterSection.storageRadio.click()
        val storageFilterSection = filterSection.getStorageFilterSection()
        storageFilterSection.trailerRadio.click()
        storageFilterSection.widthInput.fill("1")
        storageFilterSection.lengthInput.fill("3")
        val searchResultsSection = reserveBoatSpacePage.getSearchResultsSection()
        searchResultsSection.b007ReserveButton.click()

        val form = BoatSpaceFormPage(page)
        form.fillFormAndSubmit {
            getBoatSection().widthInput.fill("2")
            getBoatSection().lengthInput.fill("5")
            getWinterStorageTypeSection().trailerLengthInput.fill("5")
            getWinterStorageTypeSection().trailerWidthInput.fill("2")
            getWinterStorageTypeSection().trailerRegistrationNumberInput.fill("ABC-123")
        }
        PaymentPage(page).payReservation()
        assertThat(PaymentPage(page).reservationSuccessNotification).isVisible()

        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()
        val firstReservationSection = citizenDetailsPage.getReservationSection(expectedTerminationLocation)
        // Opens up information from the first reservation and confirms it's the same we just reserved
        firstReservationSection.terminateButton.click()
        val terminateReservationModal = citizenDetailsPage.getTerminateReservationModal()
        assertThat(terminateReservationModal.root).isVisible()
        assertThat(terminateReservationModal.placeIdentifierText).hasText(expectedTerminationLocation)

        // Terminate reservation and check for success message
        terminateReservationModal.confirmButton.click()

        // Check that the boat space is in expired reservations
        citizenDetailsPage.showExpiredReservationsToggle.click()
        assertThat(citizenDetailsPage.expiredReservationList).isVisible()

        assertNotNull(citizenDetailsPage.getReservationSection(expectedReservationId))

        // Check that the boat space is available for reservation again
        reserveBoatSpacePage.navigateToPage()
        storageFilterSection.trailerRadio.click()
        storageFilterSection.widthInput.fill("1")
        storageFilterSection.lengthInput.fill("3")
        assertThat(searchResultsSection.b007ReserveButton).isVisible()
    }
}
