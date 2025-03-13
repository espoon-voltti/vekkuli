package fi.espoo.vekkuli.citizen.details

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.pages.citizen.CitizenDetailsPage
import fi.espoo.vekkuli.pages.citizen.CitizenHomePage
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipReservationPeriod
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class TerminateReservationTest : PlaywrightTest() {
    @Autowired
    lateinit var messageUtil: MessageUtil

    @Test
    fun `citizen can open a terminate reservation modal from a reservation list item and cancel it`() {
        try {
            CitizenHomePage(page).loginAsLeoKorhonen()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()

            val firstReservationSection = citizenDetailsPage.getFirstReservationSection()
            assertThat(firstReservationSection.locationName).hasText("Haukilahti")
            assertThat(firstReservationSection.place).hasText("B 001")

            firstReservationSection.terminateButton.click()

            val terminateReservationModal = citizenDetailsPage.getTerminateReservationModal()
            assertThat(terminateReservationModal.root).isVisible()
            assertThat(terminateReservationModal.confirmButton).isVisible()
            assertThat(terminateReservationModal.cancelButton).isVisible()
            assertThat(terminateReservationModal.placeIdentifierText).hasText("Haukilahti B 001")
            assertThat(terminateReservationModal.boatSpaceText).hasText("2,50 m x 4,50 m")
            assertThat(terminateReservationModal.amenityText).hasText("Aisa")

            terminateReservationModal.cancelButton.click()
            assertThat(terminateReservationModal.root).not().isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `citizen can terminate reservation and see it in expired reservations list`() {
        try {
            mockTimeProvider(timeProvider, startOfSlipReservationPeriod.minusYears(1))
            CitizenHomePage(page).loginAsLeoKorhonen()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()

            val expectedTerminationDate = timeProvider.getCurrentDate()

            // Expired list is not on the page
            assertThat(citizenDetailsPage.expiredReservationList).hasCount(0)

            val firstReservationSection = citizenDetailsPage.getReservationSection("Haukilahti B 001")
            firstReservationSection.terminateButton.click()

            val terminateReservationModal = citizenDetailsPage.getTerminateReservationModal()
            assertThat(terminateReservationModal.root).isVisible()
            assertThat(terminateReservationModal.placeIdentifierText).hasText("Haukilahti B 001")

            terminateReservationModal.confirmButton.click()

            val terminateReservationSuccessModal = citizenDetailsPage.getTerminateReservationSuccessModal()
            assertThat(terminateReservationSuccessModal.root).isVisible()

            assertThat(terminateReservationModal.root).not().isVisible()

            citizenDetailsPage.showExpiredReservationsToggle.click()

            val expiredReservationSection = citizenDetailsPage.getExpiredReservationSection("Haukilahti B 001")
            assertThat(expiredReservationSection.locationName).hasText("Haukilahti")
            assertThat(expiredReservationSection.place).hasText("B 001")
            assertThat(expiredReservationSection.terminationDate).containsText(formatAsFullDate(expectedTerminationDate))
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `citizen sees an error message if the termination is unsuccessful`() {
        try {
            CitizenHomePage(page).loginAsLeoKorhonen()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()

            val firstReservationSection = citizenDetailsPage.getFirstReservationSection()
            firstReservationSection.terminateButton.click()

            // Opens up information from the first reservation of the first user
            val terminateReservationModal = citizenDetailsPage.getTerminateReservationModal()
            assertThat(terminateReservationModal.placeIdentifierText).hasText("Haukilahti B 001")
            jdbi.inTransactionUnchecked { tx ->
                tx
                    .createUpdate("UPDATE boat_space_reservation SET status = :status WHERE id = :id")
                    .bind("status", ReservationStatus.Cancelled)
                    .bind("id", 1)
                    .execute()
            }

            terminateReservationModal.confirmButton.click()
            assertThat(terminateReservationModal.root).not().isVisible()

            val terminateReservationFailureModal = citizenDetailsPage.getTerminateReservationFailureModal()
            assertThat(terminateReservationFailureModal.root).isVisible()

            terminateReservationFailureModal.okButton.click()
            assertThat(terminateReservationFailureModal.root).not().isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
