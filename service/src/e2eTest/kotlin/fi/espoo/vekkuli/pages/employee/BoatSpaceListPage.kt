package fi.espoo.vekkuli.pages.employee

import com.microsoft.playwright.Locator
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
    val harborFilter = { harborId: String -> page.getByDataTestId("filter-harbor-$harborId") }
    val widthSelectionFilter = filterLocator("selection-selectedWidths")
    val widthOptions = widthSelectionFilter.locator("label")
    val lengthSelectionFilter = filterLocator("selection-selectedLengths")
    val lengthOptions = lengthSelectionFilter.locator("label")
    val lengthOption = {optionValue: String -> lengthSelectionFilter.locator("label:has-text('$optionValue')")}
    val expandingSelectionFilter = filterLocator("selection-selectedSections")
    val expandingSelectionFilterValue = { value: String -> filterLocator("selection-selectedSections").locator("input[value='$value']") }
    val amenityFilter = { amenity: String -> filterLocator("amenity-$amenity") }
    val selectAllToggle = page.getByDataTestId("select-all-toggle")
    val showOnlyFreeSpaces = page.getByTestId("showOnlyFreeSpaces")

    fun boatSpaceRow(boatSpaceId: Int) = page.getByDataTestId("boat-space-$boatSpaceId")

    fun getBoatSpaceRowByIndex(index: Int) = page.locator(".boat-space-item").nth(index)

    fun checkBox(boatSpaceId: Int) = page.getByDataTestId("edit-boat-space-$boatSpaceId")

    fun checkBox(row: Locator) = row.locator(".boat-space-checkbox")

    fun showMoreButton() = page.getByTestId("boat-space-load-more-container").locator("button")

    val editModalButton = page.getByDataTestId("open-edit-modal")
    val editModalPage = BoatSpaceEditModalPage(page)
    val createModal = BoatSpaceCreationModal(page)

    val addBoatSpaceButton = page.getByDataTestId("create-boat-space")

    fun placeColumn(boatSpaceId: Int) = boatSpaceRow(boatSpaceId).locator("[data-testid='place']")
}

class BoatSpaceEditModalPage(
    page: Page
) : BasePage(page) {
    val harborEdit = page.getByTestId("harbor")
    val sectionEdit = page.getByTestId("section")
    val placeNumberEdit = page.getByTestId("placeNumber")
    val boatSpaceTypeEdit = page.getByTestId("boatSpaceType")
    val boatSpaceAmenityEdit = page.getByTestId("boatSpaceAmenity")
    val widthEdit = page.getByTestId("width")
    val lengthEdit = page.getByTestId("length")
    val paymentEdit = page.getByTestId("payment")
    val boatSpaceCount = getByDataTestId("target-boat-space-count")

    fun boatSpaceStateEdit(state: BoatSpaceState) = page.getByTestId("boatSpaceState-$state")

    val submitButton = page.getByDataTestId("edit-modal-confirm")
    val cancelButton = page.getByDataTestId("edit-modal-cancel")
    val deleteButton = page.getByDataTestId("open-delete-modal")
    val confirmButton = page.getByDataTestId("delete-modal-confirm")
    val deletionSuccessModal = page.getByDataTestId("deletion-success-modal")

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

class BoatSpaceCreationModal(
    page: Page
) : BasePage(page) {
    val harborCreation = page.getByTestId("harborCreation")
    val sectionCreation = page.getByTestId("sectionCreation")
    val placeNumberCreation = page.getByTestId("placeNumberCreation")
    val boatSpaceTypeCreation = page.getByTestId("boatSpaceTypeCreation")
    val boatSpaceAmenityCreation = page.getByTestId("boatSpaceAmenityCreation")
    val widthCreation = page.getByTestId("widthCreation")
    val lengthCreation = page.getByTestId("lengthCreation")
    val paymentCreation = page.getByTestId("paymentCreation")

    fun boatSpaceStateCreation(state: BoatSpaceState) = page.getByTestId("boatSpaceStateCreation-${state.name}")

    val submitButton = page.getByDataTestId("create-modal-confirm")
    val cancelButton = page.getByTestId("cancel-create-boat-space")
    val successModal = page.getByDataTestId("creation-success-modal")

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
        harborCreation.selectOption(harbor)
        sectionCreation.fill(section)
        placeNumberCreation.fill(placeNumber)
        boatSpaceTypeCreation.selectOption(boatSpaceType)
        boatSpaceAmenityCreation.selectOption(boatSpaceAmenity)
        widthCreation.fill(width)
        lengthCreation.fill(length)
        paymentCreation.selectOption(payment)
        boatSpaceStateCreation(boatSpaceState).click()
    }
}
