package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.ReserveTest
import fi.espoo.vekkuli.pages.citizen.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class EditReservationTest : ReserveTest() {
    @Test
    fun `should be able to edit storage type on winter reservation`() {
        CitizenHomePage(page).loginAsOliviaVirtanen()

        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()
        val reservationSection = citizenDetailsPage.getReservationSection("Haukilahti B 015")

        reservationSection.editStorageTypeButton.click()
        reservationSection.storageTypeInput.selectOption("Buck")

        var trailerSection = reservationSection.getTrailerSection()
        assertThat(trailerSection.registrationCodeInput).isHidden()

        reservationSection.storageTypeInput.selectOption("Trailer")
        trailerSection = reservationSection.getTrailerSection()
        val registrationNumber = "ABC123"

        assertThat(trailerSection.registrationCodeInput).isVisible()
        trailerSection.registrationCodeInput.fill(registrationNumber)
        trailerSection.widthInput.fill("1")
        trailerSection.lengthInput.fill("2")
        trailerSection.saveButton.click()

        assertThat(trailerSection.registrationCodeField).hasText(registrationNumber)
        assertThat(trailerSection.widthField).hasText("1,00")
        assertThat(trailerSection.lengthField).hasText("2,00")
        assertThat(reservationSection.storageTypeField).hasText("Trailerisäilytys")

        page.reload()
        assertThat(trailerSection.registrationCodeField).hasText(registrationNumber)
        assertThat(trailerSection.widthField).hasText("1,00")
        assertThat(trailerSection.lengthField).hasText("2,00")
        assertThat(reservationSection.storageTypeField).hasText("Trailerisäilytys")
    }

    @Test
    fun `should be able to edit empty trailer on storage reservation`() {
        CitizenHomePage(page).loginAsOliviaVirtanen()

        val citizenDetailsPage = CitizenDetailsPage(page)
        citizenDetailsPage.navigateToPage()

        val reservationSection = citizenDetailsPage.getReservationSection("Haukilahti B 015")

        val trailerSection = reservationSection.getTrailerSection()
        assertThat(trailerSection.registrationCodeInput).isHidden()
        trailerSection.editButton.click()

        val registrationNumber = "ABC123"
        assertThat(trailerSection.registrationCodeInput).isVisible()
        trailerSection.registrationCodeInput.fill(registrationNumber)
        trailerSection.widthInput.fill("1")
        trailerSection.lengthInput.fill("2")
        trailerSection.saveButton.click()

        assertThat(trailerSection.registrationCodeField).hasText(registrationNumber)
        assertThat(trailerSection.widthField).hasText("1,00")
        assertThat(trailerSection.lengthField).hasText("2,00")

        // Cancel button should work correctly
        trailerSection.editButton.click()
        trailerSection.registrationCodeInput.fill("AAA123")
        trailerSection.widthInput.fill("3")
        trailerSection.lengthInput.fill("4")
        trailerSection.cancelButton.click()

        assertThat(trailerSection.registrationCodeField).hasText(registrationNumber)
        assertThat(trailerSection.widthField).hasText("1,00")
        assertThat(trailerSection.lengthField).hasText("2,00")
    }
}
