package fi.espoo.vekkuli

import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
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

            assertThat(reservationPage.harborHeaders).hasCount(3)
            reservationPage.haukilahtiCheckbox.check()
            reservationPage.kivenlahtiCheckbox.check()
            assertThat(reservationPage.harborHeaders).hasCount(2)

            reservationPage.firstReserveButton.click()

            // Fill in the boat information
            scrollIntoView(reservationPage.boatTypeSelect).selectOption("Sailboat")
            scrollIntoView(reservationPage.widthInput).fill("3")
            scrollIntoView(reservationPage.lengthInput).fill("6")
            scrollIntoView(reservationPage.depthInput).fill("1.5")
            scrollIntoView(reservationPage.weightInput).fill("2000")
            scrollIntoView(reservationPage.boatName).fill("My Boat")
            scrollIntoView(reservationPage.otherIdentification).fill("ID12345")
            scrollIntoView(reservationPage.noRegistrationCheckbox).check()
            scrollIntoView(reservationPage.ownerRadioButton).check()

            scrollIntoView(reservationPage.email).fill("test@example.com")
            scrollIntoView(reservationPage.phone).fill("123456789")
            scrollIntoView(reservationPage.certifyInfoCheckbox).check()
            scrollIntoView(reservationPage.agreementCheckbox).check()
            reservationPage.submitButton.click()

            // assert that payment title is shown
            assertThat(reservationPage.paymentPageTitle).hasCount(1)
        } catch (e: AssertionError) {
            page.screenshot(Page.ScreenshotOptions().setPath(Path("build/failure-screenshot.png")))
            throw e
        }
    }
}
