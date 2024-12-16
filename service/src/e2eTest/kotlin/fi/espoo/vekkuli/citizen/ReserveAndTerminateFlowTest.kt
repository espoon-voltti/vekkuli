package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReasonOptions
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.pages.*
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
        val citizenDetailsPage = CitizenDetailsPage(page)
        val reserveBoatSpacePage = ReserveBoatSpacePage(page, userType = UserType.CITIZEN)
        val expectedHarbour = "Haukilahti"
        val expectedReservationId = "B 314"
        val expectedTerminationLocation = "$expectedHarbour $expectedReservationId"
        val indefiniteValidityText = messageUtil.getMessage("boatSpaceReservation.validity.Indefinite")
        val expiredValidityTextEndDate = formatAsFullDate(timeProvider.getCurrentDate())
        val expiredValidityTextTerminationDate =
            messageUtil.getMessage(
                "boatSpaceReservation.terminated",
                listOf(formatAsFullDate(timeProvider.getCurrentDate()))
            )

        // Create a reservation for B 314 boat space
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod.atTime(10, 0))
        CitizenHomePage(page).loginAsMikkoVirtanen()
        reserveBoatSpacePage.reserveB314BoatSpaceToASailboat()

        // Check that the citizen and boat space is visible in the reservation list
        citizenDetailsPage.navigateToPage()
        assertThat(citizenDetailsPage.placeInFirstBoatSpaceReservationCard).hasText(expectedReservationId)
        assertThat(citizenDetailsPage.locationNameInFirstBoatSpaceReservationCard).hasText(expectedHarbour)
        assertThat(citizenDetailsPage.validityInFirstBoatSpaceReservationCard).containsText(indefiniteValidityText)

        // Check that the boat space is not available for reservation anymore
        reserveBoatSpacePage.revealB314BoatSpace()
        assertThat(reserveBoatSpacePage.reserveTableB314Row).not().isVisible()

        // Terminate the reservation
        citizenDetailsPage.navigateToPage()
        assertThat(citizenDetailsPage.placeInFirstBoatSpaceReservationCard).hasText(expectedReservationId)
        citizenDetailsPage.terminateReservationButton.click()
        assertThat(citizenDetailsPage.terminateReservationForm).isVisible()

        // Opens up information from the first reservation and confirms it's the same we just reserved
        assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText(expectedTerminationLocation)

        // Terminate reservation and check for success message
        citizenDetailsPage.terminateReservationModalConfirm.click()
        assertThat(citizenDetailsPage.terminateReservationSuccess).isVisible()
        citizenDetailsPage.hideModalWindow()
        assertThat(citizenDetailsPage.terminateReservationForm).not().isVisible()

        // Check that the boat space is in expired reservations
        citizenDetailsPage.toggleExpiredReservationsAccordion()
        assertThat(citizenDetailsPage.expiredReservationList).isVisible()
        assertThat(citizenDetailsPage.placeInFirstExpiredReservationListItem).hasText(expectedReservationId)

        assertThat(citizenDetailsPage.validityInFirstExpiredReservationListItem).containsText(expiredValidityTextEndDate)
        assertThat(citizenDetailsPage.validityInFirstExpiredReservationListItem).containsText(expiredValidityTextTerminationDate)

        // Check that the boat space is available for reservation again
        reserveBoatSpacePage.revealB314BoatSpace()
        assertThat(reserveBoatSpacePage.reserveTableB314Row).isVisible()
    }

    @Test
    fun `employee can reserve a boat space and terminate to future and allow others to see it after end date`() {
        // We are at the start of reservation period
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod.atTime(10, 0))

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
        val indefiniteValidityText = messageUtil.getMessage("boatSpaceReservation.validity.Indefinite")
        val validityTextEndDate = formatAsFullDate(expectedTerminationDate)
        val validityTextTerminationDate =
            messageUtil.getMessage(
                "boatSpaceReservation.terminated",
                listOf(formatAsFullDate(expectedTerminationDate))
            )

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
        assertThat(citizenDetailsPage.validityInFirstBoatSpaceReservationCard).containsText(indefiniteValidityText)

        // Check that the boat space is not available for reservation anymore
        reserveBoatSpacePage.revealB314BoatSpace()
        assertThat(reserveBoatSpacePage.reserveTableB314Row).not().isVisible()

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
        assertThat(citizenDetailsPage.validityInFirstBoatSpaceReservationCard).containsText(validityTextEndDate)
        assertThat(citizenDetailsPage.validityInFirstBoatSpaceReservationCard).containsText(validityTextTerminationDate)
        assertThat(
            citizenDetailsPage.terminationDateInFirstReservationListItem
        ).containsText(formatAsFullDate(expectedTerminationDate))

        // Check that the reservation is still visible in the listing page with the correct end date
        listingPage.navigateTo()
        assertThat(listingPage.reservationsTableB314Row).isVisible()
        // The first reservation is indefinite, so the end date is empty
        assertThat(listingPage.reservationsTableB314RowEndDate).isEmpty()
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
        CitizenHomePage(page).loginAsMikkoVirtanen()
        citizenDetailsPage.navigateToPage()

        assertThat(citizenDetailsPage.expiredReservationList).hasCount(1)
        citizenDetailsPage.toggleExpiredReservationsAccordion()
        assertThat(citizenDetailsPage.expiredReservationList).isVisible()
        assertThat(citizenDetailsPage.locationNameInFirstExpiredReservationListItem).hasText(expectedHarbour)
        assertThat(citizenDetailsPage.placeInFirstExpiredReservationListItem).hasText(expectedPlaceId)
        assertThat(citizenDetailsPage.validityInFirstExpiredReservationListItem).containsText(validityTextEndDate)
        assertThat(citizenDetailsPage.validityInFirstExpiredReservationListItem).containsText(validityTextTerminationDate)
    }
}
