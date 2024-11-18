package fi.espoo.vekkuli.employee

import com.microsoft.playwright.Locator
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReasonOptions
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.employeePageInEnglish
import fi.espoo.vekkuli.pages.CitizenDetailsPage
import fi.espoo.vekkuli.pages.ReservationListPage
import fi.espoo.vekkuli.utils.formatAsTestDate
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class TerminateCitizenReservationAsEmpoyeeTest : PlaywrightTest() {
    val citizenPageInEnglish = "$baseUrl/kuntalainen/omat-tiedot?lang=en"

    @Test
    fun `employee navigates to citizen details page and opens a terminate reservation modal from a reservation list item and cancel it`() {
        try {
            val listingPage = ReservationListPage(page)
            val citizenDetailsPage = CitizenDetailsPage(page)

            page.navigate(employeePageInEnglish)
            page.getByTestId("employeeLoginButton").click()
            page.getByText("Kirjaudu").click()

            listingPage.navigateTo()
            listingPage.boatSpace1.click()

            assertThat(citizenDetailsPage.firstBoatSpaceReservationCard).isVisible()
            assertThat(citizenDetailsPage.expiredReservationList)
            citizenDetailsPage.terminateReservationAsEmployeeButton.click()
            assertThat(citizenDetailsPage.terminateReservationAsEmployeeForm).isVisible()
            assertThat(citizenDetailsPage.terminateReservationModalConfirm).isVisible()
            assertThat(citizenDetailsPage.terminateReservationModalCancel).isVisible()

            assertThat(citizenDetailsPage.locationNameInFirstBoatSpaceReservationCard).hasText("Haukilahti")
            assertThat(citizenDetailsPage.placeInFirstBoatSpaceReservationCard).hasText("B001")

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText("Haukilahti B001")
            assertThat(citizenDetailsPage.terminateReservationFormSize).hasText("2.5 x 4.5 m")
            assertThat(citizenDetailsPage.terminateReservationFormAmenity).hasText("Beam")

            citizenDetailsPage.terminateReservationModalCancel.click()
            assertThat(citizenDetailsPage.terminateReservationForm).not().isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can terminate reservation with an endDate, reason and comment and see it in expired reservations list`() {
        try {
            val listingPage = ReservationListPage(page)
            val citizenDetailsPage = CitizenDetailsPage(page)
            val endDate = timeProvider.getCurrentDate()
            page.navigate(employeePageInEnglish)
            page.getByTestId("employeeLoginButton").click()
            page.getByText("Kirjaudu").click()

            listingPage.navigateTo()
            listingPage.boatSpace1.click()

            // Expired list is not on the page
            assertThat(citizenDetailsPage.expiredReservationListLoader).hasCount(0)
            assertThat(citizenDetailsPage.expiredReservationList).hasCount(0)

            citizenDetailsPage.terminateReservationAsEmployeeButton.click()
            assertThat(citizenDetailsPage.terminateReservationAsEmployeeForm).isVisible()

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText("Haukilahti B001")

            // Fills the form
            citizenDetailsPage.terminateReservationEndDate.fill(formatAsTestDate(endDate))
            citizenDetailsPage.terminateReservationReason.selectOption(ReservationTerminationReasonOptions.PaymentViolation.toString())
            citizenDetailsPage.terminateReservationExplanation.fill("Test comment")

            citizenDetailsPage.terminateReservationModalConfirm.click()

            // Shows a success message in modal
            assertThat(citizenDetailsPage.terminateReservationSuccess).isVisible()

            // Hides the modal and the expired list is on the page, but not visible
            citizenDetailsPage.modalWindow.click(
                Locator
                    .ClickOptions()
                    .setPosition(5.0, 5.0)
            )

            assertThat(citizenDetailsPage.terminateReservationAsEmployeeForm).not().isVisible()
            assertThat(citizenDetailsPage.expiredReservationList).not().isVisible()
            assertThat(citizenDetailsPage.expiredReservationList).hasCount(1)

            citizenDetailsPage.getByDataTestId("accordion-title", citizenDetailsPage.expiredReservationListAccordion).click()
            assertThat(citizenDetailsPage.expiredReservationList).isVisible()
            assertThat(citizenDetailsPage.locationNameInFirstExpiredReservationListItem).hasText("Haukilahti")
            assertThat(citizenDetailsPage.placeInFirstExpiredReservationListItem).hasText("B001")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee sees an error message if the termination is unsuccessful`() {
        try {
            val listingPage = ReservationListPage(page)
            val citizenDetailsPage = CitizenDetailsPage(page)

            page.navigate(employeePageInEnglish)
            page.getByTestId("employeeLoginButton").click()
            page.getByText("Kirjaudu").click()

            listingPage.navigateTo()
            listingPage.boatSpace1.click()

            citizenDetailsPage.terminateReservationAsEmployeeButton.click()
            assertThat(citizenDetailsPage.terminateReservationAsEmployeeForm).isVisible()

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText("Haukilahti B001")
            jdbi.inTransactionUnchecked { tx ->
                tx
                    .createUpdate("UPDATE boat_space_reservation SET status = :status WHERE id = :id")
                    .bind("status", ReservationStatus.Cancelled)
                    .bind("id", 1)
                    .execute()
            }
            // Required value
            citizenDetailsPage.terminateReservationReason.selectOption(ReservationTerminationReasonOptions.Other.toString())
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
