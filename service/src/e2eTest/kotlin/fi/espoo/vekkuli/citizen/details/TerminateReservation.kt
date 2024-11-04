package fi.espoo.vekkuli.citizen.details

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.CitizenDetailsPage
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class TerminateReservation : PlaywrightTest() {
    val citizenPageInEnglish = "$baseUrl/kuntalainen/omat-tiedot?lang=en"

    @Test
    fun `citizen can open a terminate reservation modal from a reservation list item`() {
        try {
            val citizenDetailsPage = CitizenDetailsPage(page)

            page.navigate(baseUrl)
            page.getByTestId("loginButton").click()
            page.getByText("Kirjaudu").click()
            page.navigate(citizenPageInEnglish)

            assertThat(citizenDetailsPage.firstBoatSpaceReservationCard).isVisible()
            assertThat(citizenDetailsPage.expiredReservationList)
            citizenDetailsPage.terminateReservationButton.click()
            assertThat(citizenDetailsPage.terminateReservationForm).isVisible()
            assertThat(citizenDetailsPage.terminateReservationModalConfirm).isVisible()
            assertThat(citizenDetailsPage.terminateReservationModalCancel).isVisible()

            assertThat(citizenDetailsPage.locationNameInFirstBoatSpaceReservationCard).hasText("Haukilahti")
            assertThat(citizenDetailsPage.placeInFirstBoatSpaceReservationCard).hasText("B1")

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText("Haukilahti B1")
            assertThat(citizenDetailsPage.terminateReservationFormSize).hasText("2.5 x 4.5 m")
            assertThat(citizenDetailsPage.terminateReservationFormAmenity).hasText("Beam")

            citizenDetailsPage.terminateReservationModalConfirm.click()
            assertThat(citizenDetailsPage.terminateReservationForm).not().isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `citizen can terminate reservation and see it in expired reservations list`() {
        try {
            val citizenDetailsPage = CitizenDetailsPage(page)

            page.navigate(baseUrl)
            page.getByTestId("loginButton").click()
            page.getByText("Kirjaudu").click()
            page.navigate(citizenPageInEnglish)

            // Expired list is not on the page
            assertThat(citizenDetailsPage.expiredReservationListLoader).hasCount(0)
            assertThat(citizenDetailsPage.expiredReservationList).hasCount(0)

            citizenDetailsPage.terminateReservationButton.click()
            assertThat(citizenDetailsPage.terminateReservationForm).isVisible()

            // Opens up information from the first reservation of the first user
            assertThat(citizenDetailsPage.terminateReservationFormLocation).hasText("Haukilahti B1")

            // Hides the modal and the expired list is on the page, but not visible
            citizenDetailsPage.terminateReservationModalConfirm.click()
            assertThat(citizenDetailsPage.terminateReservationForm).not().isVisible()
            assertThat(citizenDetailsPage.expiredReservationList).not().isVisible()
            assertThat(citizenDetailsPage.expiredReservationList).hasCount(1)

            citizenDetailsPage.getByDataTestId("accordion-title", citizenDetailsPage.expiredReservationListAccordion).click()
            assertThat(citizenDetailsPage.expiredReservationList).isVisible()
            assertThat(citizenDetailsPage.locationNameInFirstExpiredReservationListItem).hasText("Haukilahti")
            assertThat(citizenDetailsPage.placeInFirstExpiredReservationListItem).hasText("B1")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
