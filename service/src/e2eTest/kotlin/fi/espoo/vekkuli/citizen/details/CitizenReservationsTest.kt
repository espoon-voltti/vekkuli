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

            assertThat(firstReservationSection.validity).hasText("31.12.2024 asti")

            val secondReservationSection = citizenDetailsPage.getReservationSection(1)
            assertThat(secondReservationSection.place).hasText("B 015")
            assertThat(secondReservationSection.validity).hasText("Toistaiseksi, jatko vuosittain")

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

            val firstExpiredReservationSection = citizenDetailsPage.getExpiredReservationSection("31.12.2023 asti")
            assertThat(firstExpiredReservationSection.locationName).hasText("Haukilahti")
            assertThat(firstExpiredReservationSection.place).hasText("B 003")
            assertThat(firstExpiredReservationSection.validity).hasText("31.12.2023 asti")

            val secondExpiredReservationSection = citizenDetailsPage.getExpiredReservationSection("31.12.2022 asti")
            assertThat(secondExpiredReservationSection.place).hasText("B 003")
            assertThat(secondExpiredReservationSection.validity).hasText("31.12.2022 asti")
            // Seed user has 2 expired reservations
            assertThat(citizenDetailsPage.expiredReservationListCards).hasCount(2)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
