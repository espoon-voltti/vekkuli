package fi.espoo.vekkuli.citizen.details

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.pages.citizen.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipRenewPeriod
import fi.espoo.vekkuli.utils.startOfTrailerReservationPeriod
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

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

    @Test
    fun `member can not delete boat when it's in active reservation`() {
        CitizenHomePage(page).loginAsOliviaVirtanen()

        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()
        citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()

        val organizationDetailsPage = OrganizationDetailsPage(page)
        val boatSection = organizationDetailsPage.getBoatSection("Espoon lohi")

        assertThat(boatSection.deleteButton).not().isVisible()
    }

    @Test
    fun `member can delete boat when it's without active reservation`() {
        CitizenHomePage(page).loginAsOliviaVirtanen()

        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()
        citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()

        val organizationDetailsPage = OrganizationDetailsPage(page)
        organizationDetailsPage.showAllBoatsButton.click()

        val boat = organizationDetailsPage.getBoatSection("Espoon kuha")
        assertThat(boat.deleteButton).isVisible()

        boat.deleteButton.click()
        val deleteBoatModal = organizationDetailsPage.getDeleteBoatModal()
        assertThat(deleteBoatModal.root).isVisible()
        assertThat(deleteBoatModal.root).containsText("Espoon kuha")

        deleteBoatModal.confirmButton.click()
        assertThat(deleteBoatModal.root).not().isVisible()

        val deleteBoatSuccessModal = organizationDetailsPage.getDeleteBoatSuccessModal()
        assertThat(deleteBoatSuccessModal.root).isVisible()

        assertThat(boat.root).not().isVisible()
    }

    @Test
    fun `member can terminate active reservation`() {
        CitizenHomePage(page).loginAsEspooCitizenWithActiveOrganizationSlipReservation()

        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()
        citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()

        val organizationDetailsPage = OrganizationDetailsPage(page)

        // Expired list is not on the page
        assertThat(organizationDetailsPage.expiredReservationList).hasCount(0)

        val reservationSection = organizationDetailsPage.getReservationSection("Haukilahti B 005")
        reservationSection.terminateButton.click()

        val terminateReservationModal = organizationDetailsPage.getTerminateReservationModal()
        assertThat(terminateReservationModal.root).isVisible()
        assertThat(terminateReservationModal.placeIdentifierText).hasText("Haukilahti B 005")

        terminateReservationModal.confirmButton.click()

        val terminateReservationSuccessModal = organizationDetailsPage.getTerminateReservationSuccessModal()
        assertThat(terminateReservationSuccessModal.root).isVisible()

        assertThat(terminateReservationModal.root).not().isVisible()

        organizationDetailsPage.showExpiredReservationsToggle.click()

        val expiredReservationSection = organizationDetailsPage.getExpiredReservationSection("Haukilahti B 005")
        assertThat(expiredReservationSection.locationName).containsText("Haukilahti")
        assertThat(expiredReservationSection.place).containsText("B 005")
    }

    @Test
    fun `member can see contact details`() {
        val expectedTitle = "Yhteyshenkilöt"
        val expectedContactName = "Olivia Virtanen"
        val expectedContactPhone = "04083677348"
        val expectedContactEmail = "olivia@noreplytest.fi"
        CitizenHomePage(page).loginAsEspooCitizenWithActiveOrganization()

        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()
        citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()

        val organizationDetailsPage = OrganizationDetailsPage(page)
        val contactListSection = organizationDetailsPage.getContactList()

        assertThat(contactListSection.title).containsText(expectedTitle)
        assertEquals(contactListSection.labels.count(), 3, "Expected 3 columns in contact list heading")

        val firstContact = organizationDetailsPage.getContactListItems().first()
        assertThat(firstContact.name).containsText(expectedContactName)
        assertThat(firstContact.phone).containsText(expectedContactPhone)
        assertThat(firstContact.email).containsText(expectedContactEmail)
    }

    @Test
    fun `should show renew notification when it's time to renew boat space`() {
        mockTimeProvider(timeProvider, startOfSlipRenewPeriod)

        CitizenHomePage(page).loginAsEspooCitizenWithActiveOrganizationSlipReservation()
        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()
        citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()

        val organizationDetailsPage = OrganizationDetailsPage(page)
        val reservationSection = organizationDetailsPage.getFirstReservationSection()
        assertThat(reservationSection.renewNotification).isVisible()
        assertThat(reservationSection.renewNotification).containsText("31.01.2025")

        reservationSection.renewButton.click()
        BoatSpaceFormPage(page).fillFormAndSubmit()
        PaymentPage(page).payReservation()

        mockTimeProvider(timeProvider, startOfSlipRenewPeriod.plusYears(1))
        citizenDetailsPage.navigateToPage()
        citizenDetailsPage.getOrganizationsSection("Espoon Pursiseura").nameField.click()

        assertThat(reservationSection.renewNotification).isVisible()
        assertThat(reservationSection.renewNotification).containsText("31.01.2026")
    }
}
