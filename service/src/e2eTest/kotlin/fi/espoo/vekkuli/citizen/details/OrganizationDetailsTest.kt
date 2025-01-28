package fi.espoo.vekkuli.citizen.details

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.pages.citizen.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfTrailerReservationPeriod
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class OrganizationDetailsTest : PlaywrightTest() {
    @Test
    fun `member can edit trailer information`() {
        mockTimeProvider(timeProvider, startOfTrailerReservationPeriod)

        CitizenHomePage(page).loginAsOliviaVirtanen()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateToPage()
        reservationPage.startReservingBoatSpace012()

        BoatSpaceFormPage(page).fillFormAndSubmit {
            val organizationSection = getOrganizationSection()
            organizationSection.reserveForOrganization.click()
            organizationSection.organization("Espoon Pursiseura").click()
            getTrailerStorageTypeSection().trailerRegistrationNumberInput.fill("ABC789")
        }

        PaymentPage(page).payReservation()

        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()
        citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()

        val organizationDetailsPage = OrganizationDetailsPage(page)
        val reservationSection = organizationDetailsPage.getReservationSection("TRAILERI 012")
        val trailerSection = reservationSection.getTrailerSection()

        trailerSection.editButton.click()

        trailerSection.registrationCodeInput.fill("UPDATED")
        trailerSection.widthInput.fill("6.2")
        trailerSection.lengthInput.fill("12.11")
        trailerSection.saveButton.click()

        assertThat(trailerSection.registrationCodeField).containsText("UPDATED")
        assertThat(trailerSection.widthField).containsText("6,20")
        assertThat(trailerSection.lengthField).containsText("12,11")
    }
}
