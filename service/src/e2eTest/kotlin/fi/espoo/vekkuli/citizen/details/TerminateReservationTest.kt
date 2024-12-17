package fi.espoo.vekkuli.citizen.details

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.pages.CitizenDetailsPage
import fi.espoo.vekkuli.pages.CitizenHomePage
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipReservationPeriod
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class TerminateReservationTest : PlaywrightTest() {
    val citizenPageInEnglish = "$baseUrl/kuntalainen/omat-tiedot?lang=en"

    @Autowired
    lateinit var messageUtil: MessageUtil

    @Test
    fun `citizen can open a terminate reservation modal from a reservation list item and cancel it`() {
        try {
            val citizenDetailsPage = CitizenDetailsPage(page)
            CitizenHomePage(page).loginAsLeoKorhonen()
            citizenDetailsPage.navigateToPage()

            assertThat(citizenDetailsPage.firstBoatSpaceReservationCard).isVisible()
            assertThat(citizenDetailsPage.expiredReservationList)
            citizenDetailsPage.terminateReservationButton.click()
            assertThat(citizenDetailsPage.terminateReservationForm).isVisible()
            assertThat(citizenDetailsPage.terminateReservationModalConfirm).isVisible()
            assertThat(citizenDetailsPage.terminateReservationModalCancel).isVisible()

            assertThat(citizenDetailsPage.locationNameInFirstBoatSpaceReservationCard).hasText("Haukilahti")
            assertThat(citizenDetailsPage.placeInFirstBoatSpaceReservationCard).hasText("B 001")

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText("Haukilahti B 001")
            assertThat(citizenDetailsPage.terminateReservationFormSize).hasText("2.50 x 4.50 m")
            assertThat(citizenDetailsPage.terminateReservationFormAmenity).hasText("Beam")

            citizenDetailsPage.terminateReservationModalCancel.click()
            assertThat(citizenDetailsPage.terminateReservationForm).not().isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `citizen can terminate reservation and see it in expired reservations list`() {
        try {
            mockTimeProvider(timeProvider, startOfSlipReservationPeriod)

            val citizenDetailsPage = CitizenDetailsPage(page)
            val expectedTerminationReason = messageUtil.getMessage("boatSpaceReservation.terminateReason.userRequest")
            val expectedTerminationDate = timeProvider.getCurrentDate()

            CitizenHomePage(page).loginAsLeoKorhonen()
            citizenDetailsPage.navigateToPage()

            // Expired list is not on the page
            assertThat(citizenDetailsPage.expiredReservationListLoader).hasCount(0)
            assertThat(citizenDetailsPage.expiredReservationList).hasCount(0)

            citizenDetailsPage.terminateReservationButton.click()
            assertThat(citizenDetailsPage.terminateReservationForm).isVisible()

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText("Haukilahti B 001")

            citizenDetailsPage.terminateReservationModalConfirm.click()

            // Shows a success message in modal
            assertThat(citizenDetailsPage.terminateReservationSuccess).isVisible()

            // Hides the modal and the expired list is on the page, but not visible
            citizenDetailsPage.hideModalWindow()

            assertThat(citizenDetailsPage.terminateReservationForm).not().isVisible()
            assertThat(citizenDetailsPage.expiredReservationList).not().isVisible()
            assertThat(citizenDetailsPage.expiredReservationList).hasCount(1)

            citizenDetailsPage.toggleExpiredReservationsAccordion()
            assertThat(citizenDetailsPage.expiredReservationList).isVisible()
            assertThat(citizenDetailsPage.locationNameInFirstExpiredReservationListItem).hasText("Haukilahti")
            assertThat(citizenDetailsPage.placeInFirstExpiredReservationListItem).hasText("B 001")

            assertThat(
                citizenDetailsPage.terminationDateInFirstExpiredReservationListItem
            ).containsText(formatAsFullDate(expectedTerminationDate))

            assertThat(
                citizenDetailsPage.terminationReasonInFirstExpiredReservationListItem
            ).containsText(expectedTerminationReason)

            assertThat(
                citizenDetailsPage.terminationCommentInFirstExpiredReservationListItem
            ).containsText("-")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `citizen sees an error message if the termination is unsuccessful`() {
        try {
            val citizenDetailsPage = CitizenDetailsPage(page)

            CitizenHomePage(page).loginAsLeoKorhonen()
            citizenDetailsPage.navigateToPage()

            citizenDetailsPage.terminateReservationButton.click()
            assertThat(citizenDetailsPage.terminateReservationForm).isVisible()

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText("Haukilahti B 001")
            jdbi.inTransactionUnchecked { tx ->
                tx
                    .createUpdate("UPDATE boat_space_reservation SET status = :status WHERE id = :id")
                    .bind("status", ReservationStatus.Cancelled)
                    .bind("id", 1)
                    .execute()
            }
            citizenDetailsPage.terminateReservationModalConfirm.click()
            assertThat(citizenDetailsPage.terminateReservationForm).not().isVisible()
            assertThat(citizenDetailsPage.terminateReservationFail).isVisible()
            citizenDetailsPage.terminateReservationFailOkButton.click()
            assertThat(citizenDetailsPage.terminateReservationFail).not().isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
