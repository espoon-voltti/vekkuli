package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.BoatSpaceType

class ReserveWinterSpacePage(
    private val page: Page,
    private val userType: UserType
) {
    fun navigateTo() {
        page.navigate("$baseUrl/${userType.path}/venepaikat")
    }

    val header = page.getByTestId("search-page-header")
    val emptyDimensionsWarning = page.getByTestId("empty-dimensions-warning")
    val boatTypeSelectFilter = page.getByTestId("boatType")
    val widthFilterInput = page.getByTestId("width")
    val lengthFilterInput = page.getByTestId("length")

    fun boatSpaceTypeSlipRadio(boatSpaceType: BoatSpaceType = BoatSpaceType.Slip) = page.getByTestId("boatSpaceType-${boatSpaceType.name}")

    val boatSpaceTypeTrailerRadio = page.getByTestId("boatSpaceType-Trailer")
    val amenityBuoyCheckbox = page.getByTestId("buoy-checkbox")
    val amenityRearBuoyCheckbox = page.getByTestId("rearBuoy-checkbox")
    val amenityBeamCheckbox = page.getByTestId("beam-checkbox")
    val amenityWalkBeamCheckbox = page.getByTestId("walkBeam-checkbox")
    val harborCheckbox = { harbor: String -> page.getByTestId("$harbor-checkbox") }
    val harborHeaders = page.locator(".harbor-header")
    val firstReserveButton = page.locator(".reserve-button").first()
    val reserveTableB314Row = page.locator("tr:has-text('B 314')")
    val authModal = page.getByTestId("auth-modal")
    val authModalCancel = page.getByTestId("auth-modal-cancel")
    val authModalContinue = page.getByTestId("auth-modal-continue")
}
