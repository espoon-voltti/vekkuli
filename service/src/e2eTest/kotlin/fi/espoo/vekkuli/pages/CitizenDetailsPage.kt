package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page

class CitizenDetailsPage(
    private val page: Page
) {
    val citizenDetailsSection = page.getByTestId("citizen-details")

    private fun getBoatText(
        prop: String,
        i: Int
    ) = page.getByTestId("boat-$prop-text-$i")

    fun nameText(i: Int): Locator = getBoatText("name", i)

    fun weightText(i: Int): Locator = getBoatText("weight", i)

    fun typeText(i: Int): Locator = getBoatText("type", i)

    fun depthText(i: Int): Locator = getBoatText("depth", i)

    fun widthText(i: Int): Locator = getBoatText("width", i)

    fun registrationNumberText(i: Int): Locator = getBoatText("registrationNumber", i)

    fun lengthText(i: Int): Locator = getBoatText("length", i)

    fun ownershipText(i: Int): Locator = getBoatText("ownership", i)

    fun otherIdentifierText(i: Int): Locator = getBoatText("otherIdentifier", i)

    fun extraInformationText(i: Int): Locator = getBoatText("extraInformation", i)

    fun getByDataTestId(testId: String) = page.locator("[data-testid=\"$testId\"]")

    // Citizen information
    val editButton = page.getByTestId("edit-customer")
    val citizenFirstNameInput = page.getByTestId("firstName")
    val citizenLastNameInput = page.getByTestId("lastName")
    val citizenNationalIdInput = page.getByTestId("nationalId")
    val citizenCityInput = page.getByTestId("city")
    val citizenMunicipalityInput = page.getByTestId("municipalityCode")
    val citizenEmailInput = page.getByTestId("email")
    val citizenPhoneInput = page.getByTestId("phoneNumber")
    val citizenAddressInput = page.getByTestId("address")
    val citizenPostalCodeInput = page.getByTestId("postalCode")

    val citizenFirstNameField = page.getByTestId("firstNameField")
    val citizenLastNameField = page.getByTestId("lastNameField")
    val citizenNationalIdField = page.getByTestId("nationalIdField")
    val citizenCityField = page.getByTestId("cityField")
    val citizenMunicipalityField = page.getByTestId("municipalityCodeField")
    val citizenEmailField = page.getByTestId("emailField")
    val citizenPhoneField = page.getByTestId("phoneNumberField")
    val citizenAddressField = page.getByTestId("addressField")
    val citizenPostalCodeField = page.getByTestId("postalCodeField")

    val citizenEditSubmitButton = page.getByTestId("submit-boat-edit-form")
    val citizenEmailPatternError = page.getByTestId("email-pattern-error")
    val citizenEmailError = page.getByTestId("email-error")
    val citizenPhonePatternError = page.getByTestId("phoneNumber-pattern-error")
    val citizenPhoneError = page.getByTestId("phoneNumber-error")
    val citizenNationalIdError = page.getByTestId("nationalId-error")
    val citizenNationalIdPatternError = page.getByTestId("nationalId-pattern-error")

    fun userMemo(id: Int): Locator = page.getByTestId("memo-$id")

    val nameInput: Locator = page.getByTestId("name")
    val weightInput: Locator = page.getByTestId("weight")
    val typeSelect: Locator = page.getByTestId("type")
    val depthInput: Locator = page.getByTestId("depth")
    val widthInput: Locator = page.getByTestId("width")
    val registrationNumberInput: Locator = page.getByTestId("registrationNumber")
    val length: Locator = page.getByTestId("length")
    val ownership: Locator = page.getByTestId("ownership")
    val otherIdentifier: Locator = page.getByTestId("otherIdentifier")
    val extraInformation: Locator = page.getByTestId("extraInformation")

    val submitButton: Locator = page.getByTestId("submit")
    val cancelButton: Locator = page.getByTestId("cancel")

    val memoNavi: Locator = page.getByTestId("memos-tab-navi")
    val addNewMemoBtn: Locator = page.getByTestId("add-new-memo")
    val newMemoContent: Locator = page.getByTestId("new-memo-content")
    val newMemoSaveBtn: Locator = page.getByTestId("new-memo-save-button")

    val messagesNavi: Locator = page.getByTestId("messages-tab-navi")
    val messages: Locator = page.getByTestId("messages-table").locator("tbody tr")

    val showAllBoatsButton: Locator = page.getByTestId("showAllBoats")

    val invoicePaidButton = page.getByTestId("invoice-paid-button")
    val invoicePaidInfo = page.getByTestId("invoicePaidInfo")
    val invoiceModalConfirm = page.getByTestId("invoice-modal-confirm")

    val terminateReservationButton = getByDataTestId("open-terminate-reservation-modal")
    val terminateReservationModalConfirm = getByDataTestId("terminate-reservation-modal-confirm")
    val terminateReservationModalCancel = getByDataTestId("terminate-reservation-modal-cancel")
    val terminateReservationForm = getByDataTestId("terminate-reservation-form")
    val terminateReservationFormLocation = getByDataTestId("terminate-reservation-location")
    val terminateReservationFormSize = getByDataTestId("terminate-reservation-size")
    val terminateReservationFormAmenity = getByDataTestId("terminate-reservation-amenity")
}
