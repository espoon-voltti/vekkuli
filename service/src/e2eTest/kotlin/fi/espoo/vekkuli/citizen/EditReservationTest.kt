package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.ReserveTest
import fi.espoo.vekkuli.pages.citizen.*
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class EditReservationTest : ReserveTest() {
    @Test
    fun `should be able to edit storage type on winter reservation`() {
        try {
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
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to edit empty trailer on storage reservation`() {
        try {
            CitizenHomePage(page).loginAsOliviaVirtanen()

            val citizenDetailsPage = CitizenDetailsPage(page)
            citizenDetailsPage.navigateToPage()

            val reservationSection = citizenDetailsPage.getReservationSection("Haukilahti B 007")

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
        } catch (e: AssertionError) {
            handleError(e)
        }
    }
}
