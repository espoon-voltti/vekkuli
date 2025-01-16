package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.pages.citizen.BoatSpaceFormPage
import fi.espoo.vekkuli.pages.citizen.CitizenDetailsPage
import fi.espoo.vekkuli.pages.citizen.CitizenHomePage
import fi.espoo.vekkuli.pages.citizen.PaymentPage
import fi.espoo.vekkuli.pages.citizen.ReserveBoatSpacePage
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class ReserveAndTerminateFlowTest : PlaywrightTest() {
    @Autowired
    lateinit var messageUtil: MessageUtil

    @Test
    fun `citizen can reserve a boat space and terminate it to allow others to see it`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)

        val citizenDetailsPage = CitizenDetailsPage(page)
        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        val expectedHarbour = "Haukilahti"
        val expectedReservationId = "B 314"
        val expectedTerminationLocation = "$expectedHarbour $expectedReservationId"

        CitizenHomePage(page).loginAsMikkoVirtanen()

        // Create a reservation for B 314 boat space
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()
        BoatSpaceFormPage(page).fillFormAndSubmit()
        PaymentPage(page).payReservation()

        citizenDetailsPage.navigateToPage()

        val firstReservationSection = citizenDetailsPage.getFirstReservationSection()
        assertThat(firstReservationSection.place).hasText(expectedReservationId)

        // Check that the boat space is not available for reservation anymore
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.filterForBoatSpaceB314()

        val searchResultsSection = reserveBoatSpacePage.getSearchResultsSection()
        assertThat(searchResultsSection.firstReserveButton).isVisible()
        assertThat(searchResultsSection.b314ReserveButton).not().isVisible()

        // Terminate the reservation
        citizenDetailsPage.navigateToPage()
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
}
