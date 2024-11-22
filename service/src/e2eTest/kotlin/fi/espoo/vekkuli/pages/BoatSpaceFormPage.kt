package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Page

class BoatSpaceFormPage(
    private val page: Page
) {
    val header = page.getByTestId("boat-space-form-header")
    val boatTypeSelect = page.getByTestId("boatType")
    val widthInput = page.getByTestId("width")
    val widthError = page.getByTestId("width-error")
    val lengthInput = page.getByTestId("length")
    val lengthError = page.getByTestId("length-error")
    val depthInput = page.getByTestId("depth")
    val depthError = page.getByTestId("depth-error")
    val weightInput = page.getByTestId("weight")
    val weightError = page.getByTestId("weight-error")
    val boatSizeWarning = page.getByTestId("boatSize-warning")
    val noRegistrationCheckbox = page.getByTestId("noRegistrationNumber")
    val boatRegistrationNumberError = page.getByTestId("boatRegistrationNumber-error")
    val boatNameInput = page.getByTestId("boatName")
    val boatNameError = page.getByTestId("boatName-error")
    val otherIdentification = page.getByTestId("otherIdentification")
    val emailInput = page.getByTestId("email")
    val emailError = page.getByTestId("email-error")
    val phoneInput = page.getByTestId("phone")
    val phoneError = page.getByTestId("phone-error")
    val certifyInfoCheckbox = page.getByTestId("certifyInformation")
    val certifyInfoError = page.getByTestId("certifyInformation-error")
    val agreementCheckbox = page.getByTestId("agreeToRules")
    val agreementError = page.getByTestId("agreeToRules-error")
    val submitButton = page.getByTestId("submit")
    val cancelButton = page.getByTestId("cancel")
    val confirmCancelModal = page.getByTestId("confirm-cancel-modal")
    val ownerRadioButton = page.getByTestId("ownership-Owner")
    val confirmCancelModalCancel = page.getByTestId("confirm-cancel-modal-cancel")
    val confirmCancelModalConfirm = page.getByTestId("confirm-cancel-modal-confirm")

    val firstNameInput = page.getByTestId("firstName")
    val lastNameInput = page.getByTestId("lastName")
    val ssnInput = page.getByTestId("ssn")
    val addressInput = page.getByTestId("address")
    val postalCodeInput = page.getByTestId("postalCode")

    val citizenSearchContainer = page.locator("#customer-search-container")
    val citizenInformationContainer = page.locator("#citizen-details")
    val citizenSearchInput = page.locator("#customer-search")
    val citizenSearchOption1 = page.locator("#option-0")
    val citizenSearchOption2 = page.locator("#option-1")
    val citizenEmptyInput = page.locator("#citizen-empty-input")
    val existingCitizenSelector = page.locator("#existing-citizen-selector")
    val citizenIdError = page.getByTestId("citizenId-error")

    val organizationRadioButton = page.getByTestId("reseverTypeOrg")
    val orgNameInput = page.getByTestId("orgName")
    val orgBusinessIdInput = page.getByTestId("orgBusinessId")
    val orgPhoneNumberInput = page.getByTestId("orgPhone")
    val orgEmailInput = page.getByTestId("orgEmail")

    val backButton = page.getByTestId("go-back")

    fun fillFormAndSubmit() {
        boatTypeSelect.selectOption("Sailboat")
        widthInput.fill("3")
        lengthInput.fill("6")
        depthInput.fill("1.5")
        weightInput.fill("2000")
        boatNameInput.fill("My Boat")
        otherIdentification.fill("ID12345")
        noRegistrationCheckbox.check()
        ownerRadioButton.check()
        emailInput.fill("test@example.com")
        phoneInput.fill("123456789")
        certifyInfoCheckbox.check()
        agreementCheckbox.check()
        submitButton.click()
    }
}
