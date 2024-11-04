package fi.espoo.vekkuli.citizen.details

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.citizenPageInEnglish
import fi.espoo.vekkuli.pages.CitizenDetailsPage
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class CitizenReservationsTest : PlaywrightTest() {
    @Test
    fun `citizen can see their active reservations`() {
        try {
            val citizenDetailsPage = CitizenDetailsPage(page)

            page.navigate(baseUrl)
            page.getByTestId("loginButton").click()
            page.getByTestId("031298-988S").click() // Olivia Virtanen
            page.getByText("Kirjaudu").click()
            page.navigate(citizenPageInEnglish)

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.locationNameInFirstBoatSpaceReservationCard).hasText("Haukilahti")
            assertThat(citizenDetailsPage.placeInFirstBoatSpaceReservationCard).hasText("B3")

            // Seed user has 1 active reservations
            assertThat(citizenDetailsPage.reservationListCards).hasCount(1)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `citizen can see their expired reservations`() {
        try {
            val citizenDetailsPage = CitizenDetailsPage(page)

            page.navigate(baseUrl)
            page.getByTestId("loginButton").click()
            page.getByTestId("031298-988S").click() // Olivia Virtanen
            page.getByText("Kirjaudu").click()
            page.navigate(citizenPageInEnglish)

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.locationNameInFirstBoatSpaceReservationCard).hasText("Haukilahti")
            assertThat(citizenDetailsPage.placeInFirstBoatSpaceReservationCard).hasText("B3")

            // Seed user has 2 expired reservations
            assertThat(citizenDetailsPage.expiredReservationListCards).hasCount(2)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
