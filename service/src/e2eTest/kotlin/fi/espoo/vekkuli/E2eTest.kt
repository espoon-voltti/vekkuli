package fi.espoo.vekkuli

import fi.espoo.vekkuli.pages.ReserveBoatSpacePage
import org.junit.jupiter.api.Test

class E2eTest : PlaywrightTest() {
    @Test
    fun reservingABoatSpace() {
        val page = browser.newPage()
        page.navigate(baseUrl)

        page.getByTestId("loginButton").click()
        page.getByText("Kirjaudu").click()

        val reservationPage = ReserveBoatSpacePage(page)
        reservationPage.navigateTo()
        reservationPage.boatTypeSelect.selectOption("Rowboat")
        reservationPage.widthInput.fill("3")
        reservationPage.lenghtInput.fill("6")
        reservationPage.boatSpaceTypeSlipRadio.click()
        reservationPage.amenityNoneCheckbox.check()
        reservationPage.amenityBuoyCheckbox.check()
        reservationPage.amenityRearBuoyCheckbox.check()
        reservationPage.amenityBeamCheckbox.check()
        reservationPage.amenityWalkBeamCheckbox.check()

        page.pause()
    }
}
