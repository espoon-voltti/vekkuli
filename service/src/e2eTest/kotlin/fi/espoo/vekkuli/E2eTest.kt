package fi.espoo.vekkuli

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.pages.ReserveBoatSpacePage
import org.junit.jupiter.api.Test

class E2eTest : PlaywrightTest() {
    @Test
    fun reservingABoatSpace() {
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
        reservationPage.laajalahtiCheckbox.check()
        assertThat(reservationPage.harborHeaders).hasCount(2)

        reservationPage.firstReserveButton.click()

        // Fill in the boat information
        reservationPage.boatTypeSelect.selectOption("Sailboat")
//        reservationPage.widthInput.fill("3")
//        reservationPage.lenghtInput.fill("6")
//        reservationPage.depthInput.fill("1.5")
//        reservationPage.weightInput.fill("2000")
//        reservationPage.boatName.fill("My Boat")
//        reservationPage.otherIdentification.fill("ID12345")
//        reservationPage.noRegistrationCheckbox.check()
//        reservationPage.ownerRadioButton.check()
//
//        reservationPage.email.fill("test@example.com")
//        reservationPage.phone.fill("123456789")
//        reservationPage.certifyInfoCheckbox.check()
//        reservationPage.agreementCheckbox.check()
//        reservationPage.submitButton.click()
//
//        // assert that payment title is shown
//        assertThat(reservationPage.paymentPageTitle).hasCount(1)
    }
}
