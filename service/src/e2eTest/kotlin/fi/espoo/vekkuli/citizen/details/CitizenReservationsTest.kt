package fi.espoo.vekkuli.citizen.details

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.pages.citizen.CitizenDetailsPage
import fi.espoo.vekkuli.pages.citizen.CitizenHomePage
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class CitizenReservationsTest : PlaywrightTest() {
    @Test
    fun `citizen can see their active reservations`() {
        try {
            CitizenHomePage(page).loginAsOliviaVirtanen()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()

            val firstReservationSection = citizenDetailsPage.getFirstReservationSection()
            assertThat(firstReservationSection.locationName).hasText("Haukilahti")
            assertThat(firstReservationSection.place).hasText("B 003")

            // Seed user has 2 active reservations
            assertThat(citizenDetailsPage.reservationListCards).hasCount(2)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `citizen can see their expired reservations`() {
        try {
            CitizenHomePage(page).loginAsOliviaVirtanen()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()

            citizenDetailsPage.showExpiredReservationsToggle.click()

            val firstExpiredReservationSection = citizenDetailsPage.getFirstExpiredReservationSection()
            assertThat(firstExpiredReservationSection.locationName).hasText("Haukilahti")
            assertThat(firstExpiredReservationSection.place).hasText("B 003")

            // Seed user has 2 expired reservations
            assertThat(citizenDetailsPage.expiredReservationListCards).hasCount(2)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
