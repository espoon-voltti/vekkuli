package fi.espoo.vekkuli.employee

import com.microsoft.playwright.Locator
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReasonOptions
import fi.espoo.vekkuli.citizenPageInEnglish
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.pages.CitizenDetailsPage
import fi.espoo.vekkuli.pages.CitizenHomePage
import fi.espoo.vekkuli.pages.EmployeeHomePage
import fi.espoo.vekkuli.pages.ReservationListPage
import fi.espoo.vekkuli.service.TemplateEmailService
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.utils.formatAsTestDate
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class TerminateCitizenReservationAsEmployeeTest : PlaywrightTest() {
    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var templateEmailService: TemplateEmailService

    @Test
    fun `employee navigates to citizen details page and opens a terminate reservation modal from a reservation list item and cancel it`() {
        try {
            val listingPage = ReservationListPage(page)
            val citizenDetailsPage = CitizenDetailsPage(page)

            val employeeHome = EmployeeHomePage(page)
            employeeHome.employeeLogin()

            listingPage.navigateTo()
            listingPage.boatSpace1.click()

            assertThat(citizenDetailsPage.firstBoatSpaceReservationCard).isVisible()
            assertThat(citizenDetailsPage.expiredReservationList)
            citizenDetailsPage.terminateReservationAsEmployeeButton.click()
            assertThat(citizenDetailsPage.terminateReservationAsEmployeeForm).isVisible()
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
    fun `Employee can terminate reservation with an endDate, reason, comment and message and see it in expired reservations list`() {
        try {
            val listingPage = ReservationListPage(page)
            val citizenDetailsPage = CitizenDetailsPage(page)
            val endDate = timeProvider.getCurrentDate()
            val terminationComment = "Test comment"
            val terminationReason = ReservationTerminationReasonOptions.PaymentViolation
            val expectedTerminationReason = messageUtil.getMessage("boatSpaceReservation.terminateReason.paymentViolation")
            val defaultEmailTemplate = templateEmailService.getTemplate("marine_employee_reservation_termination_custom_message")
            val defaultMessageTitle = defaultEmailTemplate?.subject
            val defaultMessageContent = defaultEmailTemplate?.body

            val employeeHome = EmployeeHomePage(page)
            employeeHome.employeeLogin()

            listingPage.navigateTo()
            listingPage.boatSpace1.click()

            // Expired list is not on the page
            assertThat(citizenDetailsPage.expiredReservationListLoader).hasCount(0)
            assertThat(citizenDetailsPage.expiredReservationList).hasCount(0)

            citizenDetailsPage.terminateReservationAsEmployeeButton.click()
            assertThat(citizenDetailsPage.terminateReservationAsEmployeeForm).isVisible()

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText("Haukilahti B 001")

            // Fills the form
            citizenDetailsPage.terminateReservationEndDate.fill(formatAsTestDate(endDate))
            citizenDetailsPage.terminateReservationReason.selectOption(terminationReason.toString())
            citizenDetailsPage.terminateReservationExplanation.fill(terminationComment)

            // Make sure that there is text in the title
            assert(!defaultMessageTitle.isNullOrEmpty())
            assertThat(citizenDetailsPage.terminateReservationMessageTitle).hasValue(defaultMessageTitle)

            // Make sure that there is text in the content
            assert(!defaultMessageContent.isNullOrEmpty())
            // Default message content contains values replaced (harbor, place) from the data. Would need to somehow replace these values in the test.
            assertThat(citizenDetailsPage.terminateReservationMessageContent).containsText(defaultMessageContent?.substring(0, 10))

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
            assertThat(citizenDetailsPage.placeInFirstExpiredReservationListItem).hasText("B 001")

            assertThat(
                citizenDetailsPage.terminationReasonInFirstExpiredReservationListItem
            ).containsText(expectedTerminationReason)

            assertThat(
                citizenDetailsPage.terminationCommentInFirstExpiredReservationListItem
            ).containsText(terminationComment)

            citizenDetailsPage.messagesNavi.click()

            // Check that the message is sent
            assertThat(citizenDetailsPage.messages.first()).containsText(defaultMessageTitle)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    @Disabled("Waiting for React version")
    fun `Employee can terminate reservation to end in the future`() {
        try {
            val listingPage = ReservationListPage(page)
            val citizenDetailsPage = CitizenDetailsPage(page)
            val weeksAddedToEndTime = 2L
            val endDate = timeProvider.getCurrentDate().plusWeeks(weeksAddedToEndTime)
            val expectedTerminationDate = timeProvider.getCurrentDate()
            val terminationComment = "Test comment"
            val terminationReason = ReservationTerminationReasonOptions.RuleViolation

            val expectedTerminationReason = messageUtil.getMessage("boatSpaceReservation.terminateReason.ruleViolation")

            val employeeHome = EmployeeHomePage(page)
            employeeHome.employeeLogin()

            listingPage.navigateTo()
            listingPage.boatSpace1.click()

            // Expired list is not on the page
            assertThat(citizenDetailsPage.expiredReservationListLoader).hasCount(0)
            assertThat(citizenDetailsPage.expiredReservationList).hasCount(0)

            citizenDetailsPage.terminateReservationAsEmployeeButton.click()
            assertThat(citizenDetailsPage.terminateReservationAsEmployeeForm).isVisible()

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText("Haukilahti B 001")

            // Fills the form
            citizenDetailsPage.terminateReservationEndDate.fill(formatAsTestDate(endDate))
            citizenDetailsPage.terminateReservationReason.selectOption(terminationReason.toString())
            citizenDetailsPage.terminateReservationExplanation.fill(terminationComment)

            citizenDetailsPage.terminateReservationModalConfirm.click()

            // Shows a success message in modal
            assertThat(citizenDetailsPage.terminateReservationSuccess).isVisible()

            // Hides the modal and the expired list is on the page, but not visible
            citizenDetailsPage.hideModalWindow()

            assertThat(citizenDetailsPage.terminateReservationAsEmployeeForm).not().isVisible()
            // Should not be in expired list yet
            assertThat(citizenDetailsPage.expiredReservationList).hasCount(0)

            assertThat(
                citizenDetailsPage.terminationReasonInFirstReservationListItem
            ).containsText(expectedTerminationReason)

            assertThat(
                citizenDetailsPage.terminationCommentInFirstReservationListItem
            ).containsText(terminationComment)

            // Wait for the reservation to expire
            mockTimeProvider(timeProvider, timeProvider.getCurrentDateTime().plusWeeks(weeksAddedToEndTime))
            listingPage.navigateTo()
            assertThat(listingPage.boatSpace1).not().isVisible()

            CitizenHomePage(page).loginAsLeoKorhonen()
            page.navigate(citizenPageInEnglish)

            assertThat(citizenDetailsPage.expiredReservationList).hasCount(1)
            citizenDetailsPage.getByDataTestId("accordion-title", citizenDetailsPage.expiredReservationListAccordion).click()
            assertThat(citizenDetailsPage.expiredReservationList).isVisible()
            assertThat(citizenDetailsPage.locationNameInFirstExpiredReservationListItem).hasText("Haukilahti")
            assertThat(citizenDetailsPage.placeInFirstExpiredReservationListItem).hasText("B 001")

            assertThat(
                citizenDetailsPage.terminationReasonInFirstExpiredReservationListItem
            ).containsText(expectedTerminationReason)

            assertThat(
                citizenDetailsPage.terminationCommentInFirstExpiredReservationListItem
            ).containsText(terminationComment)

            assertThat(
                citizenDetailsPage.terminationDateInFirstExpiredReservationListItem
            ).containsText(formatAsFullDate(expectedTerminationDate))
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee sees an error message if the termination is unsuccessful`() {
        try {
            val listingPage = ReservationListPage(page)
            val citizenDetailsPage = CitizenDetailsPage(page)

            val employeeHome = EmployeeHomePage(page)
            employeeHome.employeeLogin()

            listingPage.navigateTo()
            listingPage.boatSpace1.click()

            citizenDetailsPage.terminateReservationAsEmployeeButton.click()
            assertThat(citizenDetailsPage.terminateReservationAsEmployeeForm).isVisible()

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText("Haukilahti B 001")
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
