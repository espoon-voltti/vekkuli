package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReasonOptions
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.ReservationExpiration
import fi.espoo.vekkuli.pages.employee.CitizenDetailsPage
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.pages.employee.ReservationListPage
import fi.espoo.vekkuli.pages.employee.ReserveBoatSpacePage
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ActiveProfiles("test")
class ReserveAndTerminateFlowTest : PlaywrightTest() {
    @Autowired
    lateinit var messageUtil: MessageUtil

    @Test
    fun `employee can reserve a boat space and terminate to future and allow others to see it after end date`() {
        // We are at the start of reservation period
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)

        val listingPage = ReservationListPage(page)
        val citizenDetailsPage = CitizenDetailsPage(page)
        val employeeHome = EmployeeHomePage(page)
        val reserveBoatSpacePage = ReserveBoatSpacePage(page, userType = UserType.EMPLOYEE)
        val expectedHarbour = "Haukilahti"
        val expectedPlaceId = "B 314"
        val expectedReserverSearch = "Virtanen Mikko"
        val expectedTerminationLocation = "$expectedHarbour $expectedPlaceId"
        val weeksAddedToEndTime = 2L
        val endDate = timeProvider.getCurrentDate().plusWeeks(weeksAddedToEndTime)
        val expectedTerminationDate = timeProvider.getCurrentDate()
        val expectedEndDateInReservationsList = formatAsShortYearDate(endDate)
        val terminationComment = "Test comment"
        val terminationReason = ReservationTerminationReasonOptions.RuleViolation
        val expectedTerminationReason = messageUtil.getMessage("boatSpaceReservation.terminateReason.ruleViolation")

        // Create a reservation for B 314 boat space
        employeeHome.employeeLogin()
        listingPage.navigateTo()
        listingPage.createReservation.click()

        // Creates a reservation for a citizen, invoices it and returns to the listing page
        reserveBoatSpacePage.reserveB314BoatSpaceToASailboatAsEmployee(expectedReserverSearch)

        // Check that the citizen and boat space is visible in the reservation list
        assertThat(listingPage.reservationsTableB314Row).isVisible()

        // Go to the citizen details page
        listingPage.reservationsTableB314Row.click()
        assertThat(citizenDetailsPage.placeInFirstBoatSpaceReservationCard).hasText(expectedPlaceId)

        // Check that the boat space is not available for reservation anymore
        reserveBoatSpacePage.revealB314BoatSpace()
        assertThat(reserveBoatSpacePage.reserveTableB314Row).not().isVisible()

        messageService.sendScheduledEmails()
        assertEquals(1, SendEmailServiceMock.emails.size)
        assertTrue(
            SendEmailServiceMock.emails.get(
                0
            ).contains("mikko.virtanen@noreplytest.fi with subject Espoon kaupungin venepaikan varaus")
        )
        SendEmailServiceMock.resetEmails()

        // Terminate the reservation
        listingPage.navigateTo()
        listingPage.reservationsTableB314Row.click()
        assertThat(citizenDetailsPage.placeInFirstBoatSpaceReservationCard).hasText(expectedPlaceId)
        citizenDetailsPage.terminateReservationAsEmployeeButton.click()
        assertThat(citizenDetailsPage.terminateReservationAsEmployeeForm).isVisible()
        assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText(expectedTerminationLocation)
        citizenDetailsPage.terminateReservationEndDate.fill(formatAsTestDate(endDate))
        citizenDetailsPage.terminateReservationReason.selectOption(terminationReason.toString())
        citizenDetailsPage.terminateReservationExplanation.fill(terminationComment)
        citizenDetailsPage.terminateReservationModalConfirm.click()
        assertThat(citizenDetailsPage.terminateReservationSuccess).isVisible()
        citizenDetailsPage.hideModalWindow()

        // Check that the boat space is not yet in the expired list
        assertThat(citizenDetailsPage.terminateReservationAsEmployeeForm).not().isVisible()
        assertThat(citizenDetailsPage.expiredReservationList).hasCount(0)

        // Check that it's shown as terminated with the correct reason and end date
        assertThat(
            citizenDetailsPage.terminationReasonInFirstReservationListItem
        ).containsText(expectedTerminationReason)
        assertThat(
            citizenDetailsPage.terminationDateInFirstReservationListItem
        ).containsText(formatAsFullDate(expectedTerminationDate))

        // Assert that termination email to citizen has been sent
        messageService.sendScheduledEmails()
        assertEquals(1, SendEmailServiceMock.emails.size)
        assertTrue(SendEmailServiceMock.emails.get(0).contains("Venepaikka: Haukilahti B 314 on irtisanottu virkailijan toimesta"))

        // Check that the reservation is still visible in the listing page with the correct end date
        listingPage.navigateTo()
        assertThat(listingPage.reservationsTableB314Row).isVisible()
        assertThat(listingPage.reservationsTableB314RowEndDate).hasText("Terminated $expectedEndDateInReservationsList")

        // Check that the boat space is not available for reservation
        reserveBoatSpacePage.revealB314BoatSpace()
        assertThat(reserveBoatSpacePage.reserveTableB314Row).not().isVisible()

        // Go forward in time to the end date of the reservation
        mockTimeProvider(timeProvider, timeProvider.getCurrentDateTime().plusWeeks(weeksAddedToEndTime))

        // The reservation should not be visible anymore
        listingPage.navigateTo()
        assertThat(listingPage.reservationsTableB314Row).not().isVisible()

        // Check that the boat space is available for reservation again
        reserveBoatSpacePage.revealB314BoatSpace()
        assertThat(reserveBoatSpacePage.reserveTableB314Row).isVisible()

        // Check that the user sees the reservation as expired
        listingPage.navigateTo()
        listingPage.reservationExpiration(ReservationExpiration.Expired.toString()).click()
        assertThat(listingPage.reservationsTableB314Row).isVisible()
        listingPage.reservationsTableB314Row.click()

        assertThat(citizenDetailsPage.expiredReservationList).hasCount(1)
        citizenDetailsPage.toggleExpiredReservationsAccordion()
        assertThat(citizenDetailsPage.expiredReservationList).isVisible()
        assertThat(citizenDetailsPage.locationNameInFirstExpiredReservationListItem).hasText(expectedHarbour)
        assertThat(citizenDetailsPage.placeInFirstExpiredReservationListItem).hasText(expectedPlaceId)
    }
}
