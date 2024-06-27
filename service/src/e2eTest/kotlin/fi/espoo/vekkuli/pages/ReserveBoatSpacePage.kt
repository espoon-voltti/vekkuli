package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl

class ReserveBoatSpacePage(private val page: Page) {
    fun navigateTo() {
        page.navigate("$baseUrl/kuntalainen/venepaikat")
    }

    val boatTypeSelectFilter = page.getByTestId("boatTypeFilter")
    val widthFilterInput = page.getByTestId("widthFilter")
    val lenghtFilterInput = page.getByTestId("lengthFilter")

    val boatSpaceTypeSlipRadio = page.getByTestId("boatSpaceType-slip")
    val boatSpaceTypeTrailerRadio = page.getByTestId("boatSpaceType-trailer")
    val amenityBuoyCheckbox = page.getByTestId("buoy-checkbox")
    val amenityRearBuoyCheckbox = page.getByTestId("rearBuoy-checkbox")
    val amenityBeamCheckbox = page.getByTestId("beam-checkbox")
    val amenityWalkBeamCheckbox = page.getByTestId("walkBeam-checkbox")
    val haukilahtiCheckbox = page.getByTestId("haukilahti-checkbox")
    val laajalahtiCheckbox = page.getByTestId("laajalahti-checkbox")
    val harborHeaders = page.locator(".harbor-header")
    val firstReserveButton = page.locator(".reserve-button").first()
    val paymentPageTitle = page.locator("text='Maksunäkymä'")
    val boatTypeSelect = page.getByTestId("boatType")
    val widthInput = page.getByTestId("width")
    val lenghtInput = page.getByTestId("length")
    val depthInput = page.getByTestId("depth")
    val weightInput = page.getByTestId("weight")
    val noRegistrationCheckbox = page.getByTestId("noRegistration")
    val boatName = page.getByTestId("boatName")
    val otherIdentification = page.getByTestId("otherIdentification")
    val email = page.getByTestId("email")
    val phone = page.getByTestId("phone")
    val certifyInfoCheckbox = page.getByTestId("certifyInfoCheckbox")
    val agreementCheckbox = page.getByTestId("agreementCheckbox")
    val submitButton = page.getByTestId("submit")
    val ownerRadioButton = page.getByTestId("owner")
}
