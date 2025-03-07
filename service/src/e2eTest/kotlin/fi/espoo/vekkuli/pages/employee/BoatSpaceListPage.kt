package fi.espoo.vekkuli.pages.employee

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.domain.BoatSpaceState
import fi.espoo.vekkuli.pages.BasePage
import fi.espoo.vekkuli.pages.getByDataTestId

class BoatSpaceListPage(
    page: Page
) : BasePage(page) {
    fun navigateTo() {
        navigateToWithParams()
    }

    fun navigateToWithParams(params: Map<String, String>? = null) {
        // if params, map params to query string
        val paramsString =
            params
                ?.takeIf { it.isNotEmpty() }
                ?.map { (key, value) -> "$key=$value" }
                ?.joinToString("&", prefix = "?") ?: ""

        page.navigate("$baseUrl/virkailija/venepaikat/selaa$paramsString")
    }

    private val filterLocator = { filter: String -> getByDataTestId("filter-$filter") }

    val listItems = page.locator(".boat-space-item")
    val boatSpaceTypeFilter = { type: String -> filterLocator("type-$type") }
    val boatStateFilter = { state: String -> filterLocator("boatSpaceState-$state") }
    val searchInput = { inputName: String -> getByDataTestId("search-input-$inputName") }
    val expandingSelectionFilter = { selection: String -> filterLocator("selection-$selection") }
    val amenityFilter = { amenity: String -> filterLocator("amenity-$amenity") }

    fun boatSpaceRow(boatSpaceId: Int) = page.getByDataTestId("boat-space-$boatSpaceId")

    fun editButton(boatSpaceId: Int) = page.getByDataTestId("edit-boat-space-$boatSpaceId")

    val editModalButton = page.getByDataTestId("open-edit-modal")
    val editModalPage = BoatSpaceEditModalPage(page)
}

class BoatSpaceEditModalPage(
    page: Page
) : BasePage(page) {
    val harborEdit = page.getByTestId("harborEdit")
    val sectionEdit = page.getByTestId("sectionEdit")
    val placeNumberEdit = page.getByTestId("placeNumberEdit")
    val boatSpaceTypeEdit = page.getByTestId("boatSpaceTypeEdit")
    val boatSpaceAmenityEdit = page.getByTestId("boatSpaceAmenityEdit")
    val widthEdit = page.getByTestId("widthEdit")
    val lengthEdit = page.getByTestId("lengthEdit")
    val paymentEdit = page.getByTestId("paymentEdit")

    fun boatSpaceStateEdit(state: BoatSpaceState) = page.getByTestId("boatSpaceStateEdit-$state")

    val submitButton = page.getByTestId("edit-modal-confirm")
    val cancelButton = page.getByTestId("cancel-edit-boat-space")

    fun fillForm(
        width: String,
        length: String,
        harbor: String = "1",
        section: String = "A",
        placeNumber: String = "1",
        boatSpaceType: String = "Storage",
        boatSpaceAmenity: String = "None",
        payment: String = "2",
        boatSpaceState: BoatSpaceState = BoatSpaceState.Active
    ) {
        harborEdit.selectOption(harbor)
        sectionEdit.fill(section)
        placeNumberEdit.fill(placeNumber)
        boatSpaceTypeEdit.selectOption(boatSpaceType)
        boatSpaceAmenityEdit.selectOption(boatSpaceAmenity)
        widthEdit.fill(width)
        lengthEdit.fill(length)
        paymentEdit.selectOption(payment)
        boatSpaceStateEdit(boatSpaceState).click()
    }
}
