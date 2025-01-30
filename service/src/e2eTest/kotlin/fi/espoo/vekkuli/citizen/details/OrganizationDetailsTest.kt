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

    @Test
    fun `member can edit boat information`() {
        CitizenHomePage(page).loginAsOliviaVirtanen()

        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()
        citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()

        val organizationDetailsPage = OrganizationDetailsPage(page)
        var boatSection = organizationDetailsPage.getBoatSection("Espoon lohi")

        boatSection.editButton.click()

        boatSection.nameInput.fill("New Boat Name")
        boatSection.weightInput.fill("2000")
        boatSection.typeSelect.selectOption("Sailboat")
        boatSection.depthInput.fill("1.5")
        boatSection.widthInput.fill("3")
        boatSection.registrationNumberInput.fill("ABC123")
        boatSection.lengthInput.fill("6")
        boatSection.ownershipSelect.selectOption("Owner")
        boatSection.otherIdentifierInput.fill("ID12345")
        boatSection.extraInformationInput.fill("Extra info")

        boatSection.saveButton.click()

        boatSection = organizationDetailsPage.getBoatSection("New Boat Name")
        assertThat(boatSection.nameField).hasText("New Boat Name")
        assertThat(boatSection.weightField).hasText("2000")
        assertThat(boatSection.typeField).hasText("Purjevene")
        assertThat(boatSection.depthField).hasText("1,50")
        assertThat(boatSection.widthField).hasText("3,00")
        assertThat(boatSection.registrationNumberField).hasText("ABC123")
        assertThat(boatSection.lengthField).hasText("6,00")
        assertThat(boatSection.ownershipField).hasText("Omistan veneen")
        assertThat(boatSection.otherIdentifierField).hasText("ID12345")
        assertThat(boatSection.extraInformationField).hasText("Extra info")
    }
}
