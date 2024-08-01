package fi.espoo.vekkuli

import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.pages.BoatSpaceForm
import fi.espoo.vekkuli.pages.ReserveBoatSpacePage
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class E2eTest : PlaywrightTest() {
    @Test
    fun reservingABoatSpace() {
        try {
            page.navigate(baseUrl)

            page.getByTestId("loginButton").click()
            page.getByText("Kirjaudu").click()

            val reservationPage = ReserveBoatSpacePage(page)
            reservationPage.navigateTo()
            reservationPage.boatTypeSelectFilter.selectOption("Sailboat")
            reservationPage.widthFilterInput.fill("3")
            reservationPage.lenghtFilterInput.fill("6")
            reservationPage.boatSpaceTypeSlipRadio.click()
            reservationPage.amenityBuoyCheckbox.check()
            reservationPage.amenityRearBuoyCheckbox.check()
            reservationPage.amenityBeamCheckbox.check()
            reservationPage.amenityWalkBeamCheckbox.check()

            assertThat(reservationPage.harborHeaders).hasCount(4)
            reservationPage.haukilahtiCheckbox.check()
            reservationPage.kivenlahtiCheckbox.check()
            assertThat(reservationPage.harborHeaders).hasCount(2)

            reservationPage.firstReserveButton.click()

            // click send to trigger validation
            val formPage = BoatSpaceForm(page)
            formPage.submitButton.click()

            assertThat(formPage.widthError).isHidden()
            assertThat(formPage.lengthError).isHidden()
            assertThat(formPage.depthError).isVisible()
            assertThat(formPage.weightError).isVisible()
            assertThat(formPage.boatRegistrationNumberError).isVisible()
            assertThat(formPage.emailError).isVisible()
            assertThat(formPage.phoneError).isVisible()
            assertThat(formPage.certifyInfoError).isVisible()
            assertThat(formPage.agreementError).isVisible()

            // Fill in the boat information
            formPage.boatTypeSelect.selectOption("Sailboat")
            formPage.widthInput.fill("3")
            formPage.lengthInput.fill("6")

            formPage.depthInput.fill("1.5")
            formPage.depthInput.blur()
            assertThat(formPage.depthError).isHidden()

            formPage.weightInput.fill("2000")
            formPage.weightInput.blur()
            assertThat(formPage.weightError).isHidden()

            formPage.boatNameInput.fill("My Boat")
            formPage.otherIdentification.fill("ID12345")
            formPage.noRegistrationCheckbox.check()
            assertThat(formPage.boatRegistrationNumberError).isHidden()

            formPage.ownerRadioButton.check()

            formPage.emailInput.fill("test@example.com")
            formPage.emailInput.blur()
            assertThat(formPage.emailError).isHidden()

            formPage.phoneInput.fill("123456789")
            formPage.phoneInput.blur()
            assertThat(formPage.phoneError).isHidden()

            formPage.certifyInfoCheckbox.check()
            formPage.agreementCheckbox.check()
            formPage.submitButton.click()

            // assert that payment title is shown
            assertThat(reservationPage.paymentPageTitle).hasCount(1)
        } catch (e: AssertionError) {
            page.screenshot(Page.ScreenshotOptions().setPath(Path("build/failure-screenshot.png")))
            throw e
        }
    }
}
