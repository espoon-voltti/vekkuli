package fi.espoo.vekkuli.citizen.details

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReasonOptions
import fi.espoo.vekkuli.pages.citizen.*
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.pages.employee.ReservationListPage
import fi.espoo.vekkuli.utils.formatAsTestDate
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import fi.espoo.vekkuli.pages.employee.CitizenDetailsPage as EmployeeCitizenDetailsPage

@ActiveProfiles("test")
class CitizenReservationsTest : PlaywrightTest() {
    @Test
    fun `citizen can see their active reservations`() {
        CitizenHomePage(page).loginAsOliviaVirtanen()

        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()

        val firstReservationSection = citizenDetailsPage.getReservationSection("Haukilahti B 003")
        assertThat(firstReservationSection.locationName).hasText("Haukilahti")
        assertThat(firstReservationSection.place).hasText("B 003")

        assertThat(firstReservationSection.validity).hasText("31.12.2024 asti")

        val secondReservationSection = citizenDetailsPage.getReservationSection("Haukilahti B 015")
        assertThat(secondReservationSection.place).hasText("B 015")
        assertThat(secondReservationSection.validity).hasText("Toistaiseksi, jatko vuosittain")

        // Seed user has 3 active reservations
        assertThat(citizenDetailsPage.reservationListCards).hasCount(3)
    }

    @Test
    fun `citizen can see their expired reservations`() {
        CitizenHomePage(page).loginAsOliviaVirtanen()

        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()

        citizenDetailsPage.showExpiredReservationsToggle.click()

        val firstExpiredReservationSection = citizenDetailsPage.getExpiredReservationSection("31.12.2023 asti")
        assertThat(firstExpiredReservationSection.locationName).hasText("Haukilahti")
        assertThat(firstExpiredReservationSection.place).hasText("B 003")
        assertThat(firstExpiredReservationSection.validity).hasText("31.12.2023 asti")

        val secondExpiredReservationSection = citizenDetailsPage.getExpiredReservationSection("31.12.2022 asti")
        assertThat(secondExpiredReservationSection.place).hasText("B 003")
        assertThat(secondExpiredReservationSection.validity).hasText("31.12.2022 asti")
        // Seed user has 2 expired reservations
        assertThat(citizenDetailsPage.expiredReservationListCards).hasCount(2)
    }

    @Test
    fun `Cancelled reservations should show previous date as the end date`() {
        val endDate = LocalDate.of(2024, 4, 8)
        val expectedDisplayedEndDate = "07.04.2024 asti"

        // create reservation
        CitizenHomePage(page).loginAsMikkoVirtanen()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingBoatSpaceB314()
        BoatSpaceFormPage(page).fillFormAndSubmit()
        PaymentPage(page).payReservation()

        // terminate reservation
        EmployeeHomePage(page).employeeLogin()

        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        listingPage.boatSpace("Virtanen Mikko").click()

        val citizenDetails = EmployeeCitizenDetailsPage(page)
        citizenDetails.terminateReservationAsEmployeeButton.click()
        citizenDetails.terminateReservationEndDate.fill(formatAsTestDate(endDate))
        citizenDetails.terminateReservationReason.selectOption(ReservationTerminationReasonOptions.PaymentViolation.toString())
        citizenDetails.terminateReservationModalConfirm.click()
        assertThat(citizenDetails.terminateReservationSuccess).isVisible()
        citizenDetails.hideModalWindow()

        // check reservation details in citizen page
        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()

        val reservationSection = citizenDetailsPage.getReservationSection("Haukilahti B 314")
        assertThat(reservationSection.validity).hasText(expectedDisplayedEndDate)
    }
}
