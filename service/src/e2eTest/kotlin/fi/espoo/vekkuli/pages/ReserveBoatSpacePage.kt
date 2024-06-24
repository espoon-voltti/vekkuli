package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl

class ReserveBoatSpacePage(private val page: Page) {
    fun navigateTo() {
        page.navigate("$baseUrl/kuntalainen/venepaikat")
    }

    val boatTypeSelect = page.getByTestId("boatType")
    val widthInput = page.getByTestId("width")
    val lenghtInput = page.getByTestId("length")
    val boatSpaceTypeSlipRadio = page.getByTestId("boatSpaceType-slip")
    val boatSpaceTypeTrailerRadio = page.getByTestId("boatSpaceType-trailer")
    val amenityNoneCheckbox = page.getByTestId("none-checkbox")
    val amenityBuoyCheckbox = page.getByTestId("buoy-checkbox")
    val amenityRearBuoyCheckbox = page.getByTestId("rearBuoy-checkbox")
    val amenityBeamCheckbox = page.getByTestId("beam-checkbox")
    val amenityWalkBeamCheckbox = page.getByTestId("walkBeam-checkbox")
    val haukilahtiCheckbox = page.getByTestId("haukilahti-checkbox")
    val laajalahtiCheckbox = page.getByTestId("laajalahti-checkbox")
    val harborHeaders = page.locator(".harbor-header")
    val firstReserveButton = page.locator(".reserve-button").first()
}
