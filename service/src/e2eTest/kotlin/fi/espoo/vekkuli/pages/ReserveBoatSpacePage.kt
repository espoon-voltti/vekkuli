package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl

class ReserveBoatSpacePage(
    private val page: Page
) {
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
    val kivenlahtiCheckbox = page.getByTestId("kivenlahti-checkbox")
    val harborHeaders = page.locator(".harbor-header")
    val firstReserveButton = page.locator(".reserve-button").first()
    val paymentPageTitle = page.locator("text='Maksunäkymä'")
}
