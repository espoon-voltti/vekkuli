package fi.espoo.vekkuli.citizen.details

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.CitizenDetailsPage
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class TerminateReservation : PlaywrightTest() {
    @Test
    fun `citizen can open a terminate reservation modal from a reservation list item`() {
        try {
            page.navigate(baseUrl)
            page.getByTestId("loginButton").click()
            page.getByText("Kirjaudu").click()

            page.navigate(baseUrl + "/kuntalainen/omat-tiedot")

            assertThat(page.locator(".reservation-list").first()).isVisible()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.terminateReservationButton.click()
            assertThat(citizenDetailsPage.terminateReservationForm).isVisible()
            assertThat(citizenDetailsPage.terminateReservationModalConfirm).isVisible()
            assertThat(citizenDetailsPage.terminateReservationModalCancel).isVisible()

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText("Haukilahti B1")
            assertThat(citizenDetailsPage.terminateReservationFormSize).hasText("2.5 x 4.5 m")
            assertThat(citizenDetailsPage.terminateReservationFormAmenity).hasText("Beam")

            citizenDetailsPage.terminateReservationModalCancel.click()
            assertThat(citizenDetailsPage.terminateReservationForm).not().isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
