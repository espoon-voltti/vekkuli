package fi.espoo.vekkuli.pages.employee

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.BoatSpaceType

class ReserveBoatSpacePage(
    private val page: Page,
    private val userType: UserType
) {
    fun navigateTo() {
        page.navigate("$baseUrl/${userType.path}/venepaikat")
    }

    fun reserveB314BoatSpaceToASailboat() {
        val boatSpaceFormPage = BoatSpaceFormPage(page)
        val paymentPage = PaymentPage(page)

        // Go to space search and fill the filters for B314
        revealB314BoatSpace()
        // Select the B314 boat space and reserve
        reserveTableB314Row.locator("button.is-primary").click()
        // Fill in the boat information
        boatSpaceFormPage.fillFormWithPrefilledValuesAndSubmit()
        // Then go through the payment
        paymentPage.nordeaSuccessButton.click()
    }

    fun reserveB314BoatSpaceToASailboatAsEmployee(reserverName: String) {
        val boatSpaceFormPage = BoatSpaceFormPage(page)
        val invoicePreviewPage = InvoicePreviewPage(page)
        // Go to space search and fill the filters for B314
        revealB314BoatSpace()
        // Select the B314 boat space and reserve
        reserveTableB314Row.locator("button.is-primary").click()

        // Fill in the boat information
        boatSpaceFormPage.fillFormAsEmployeeWithPrefilledValuesAndSubmit(reserverName)

        // Then send the invoice
        invoicePreviewPage.sendButton.click()
    }

    fun revealB314BoatSpace() {
        val reserveBoatSpacePage = ReserveBoatSpacePage(page, userType)
        reserveBoatSpacePage.navigateTo()
        boatTypeSelectFilter.selectOption("Sailboat")
        widthFilterInput.fill("3")
        lengthFilterInput.fill("6")
        lengthFilterInput.blur()
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
    val haukilahtiCheckbox = page.getByTestId("haukilahti-checkbox")
    val kivenlahtiCheckbox = page.getByTestId("kivenlahti-checkbox")
    val harborHeaders = page.locator(".harbor-header")
    val firstReserveButton = page.locator(".reserve-button").first()
    val reserveTableB314Row = page.locator("tr:has-text('B 314')")
    val authModal = page.getByTestId("auth-modal")
    val authModalCancel = page.getByTestId("auth-modal-cancel")
    val authModalContinue = page.getByTestId("auth-modal-continue")
}
