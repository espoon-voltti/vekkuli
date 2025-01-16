package fi.espoo.vekkuli.pages.employee

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.BasePage

class CitizenDetailsPage(
    page: Page
) : BasePage(page) {
    val citizenDetailsSection = page.getByTestId("reserver-details")

    val reservationValidity = getByDataTestId("reservation-validity")

    val paymentStatus = getByDataTestId("payment-status")

    val updatePaymentStatusLink = getByDataTestId("update-payment-status-link")

    val paymentStatusUpdateModalConfirmed = page.getByTestId("reservationStatus-Confirmed")
    val paymentStatusUpdateModalInfoTextInput = page.getByTestId("paymentStatusText")
    val paymentStatusUpdateModalDateInput = page.getByTestId("paymentDate")
    val paymentStatusUpdateModalSubmit = page.getByTestId("invoice-modal-confirm")

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

    fun navigateToPage() {
        page.navigate("$baseUrl/kuntalainen/omat-tiedot?lang=en")
    }

    fun hideModalWindow() {
        modalWindow.click(
            Locator
                .ClickOptions()
                .setPosition(5.0, 5.0)
        )
    }

    fun toggleExpiredReservationsAccordion() {
        getByDataTestId("accordion-title", expiredReservationListAccordion).click()
    }

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

    val submitButton: Locator = page.getByTestId("submit-button")
    val cancelButton: Locator = page.getByTestId("cancel")

    val memoNavi: Locator = page.getByTestId("memos-tab-navi")
    val addNewMemoBtn: Locator = page.getByTestId("add-new-memo")
    val newMemoContent: Locator = page.getByTestId("new-memo-content")
    val newMemoSaveBtn: Locator = page.getByTestId("new-memo-save-button")

    val messagesNavi: Locator = getByDataTestId("messages-tab-navi")
    val messages: Locator = page.getByTestId("messages-table").locator("tbody tr")

    val paymentsNavi: Locator = page.getByTestId("payments-tab-navi")
    val noPaymentsIndicator: Locator = getByDataTestId("no-payments-indicator")
    val paymentsTable: Locator = page.getByTestId("payments-table")
    val refundPaymentButton: Locator = getByDataTestId("refund-payment-button")
    val refundPaymentModalConfirm: Locator = getByDataTestId("refund-payment-modal-confirm")

    val showAllBoatsButton: Locator = page.getByTestId("showAllBoats")

    val invoicePaidButton = page.getByTestId("invoice-paid-button")
    val invoicePaidInfo = page.getByTestId("invoicePaidInfo")
    val invoicePaymentDate = page.getByTestId("paymentDate")
    val invoiceModalConfirm = page.getByTestId("invoice-modal-confirm")
    val paidFieldInfo = page.getByTestId("paidFieldInfo")

    val reservationList = getByDataTestId("reservation-list")
    val reservationListCards = getByDataTestId("reservation-list-card", reservationList)
    val firstBoatSpaceReservationCard = reservationListCards.first()
    val locationNameInFirstBoatSpaceReservationCard =
        getByDataTestId(
            "reservation-list-card-location-name",
            firstBoatSpaceReservationCard
        )
    val placeInFirstBoatSpaceReservationCard =
        getByDataTestId(
            "reservation-list-card-place",
            firstBoatSpaceReservationCard
        )

    val terminateReservationButton = getByDataTestId("open-terminate-reservation-modal")
    val terminateReservationModalConfirm = getByDataTestId("terminate-reservation-modal-confirm")
    val terminateReservationModalCancel = getByDataTestId("terminate-reservation-modal-cancel")
    val terminateReservationForm = getByDataTestId("terminate-reservation-form")
    val terminateReservationSuccess = getByDataTestId("termination-success-modal")
    val terminateReservationFail = getByDataTestId("termination-fail-modal")
    val terminateReservationFailOkButton = getByDataTestId("terminate-reservation-fail-modal-ok")
    val terminateReservationFormLocation = getByDataTestId("terminate-reservation-location")
    val terminateReservationFormSize = getByDataTestId("terminate-reservation-size")
    val terminateReservationFormAmenity = getByDataTestId("terminate-reservation-amenity")

    val expiredReservationList = getByDataTestId("expired-reservation-list")
    val expiredReservationListCards = getByDataTestId("expired-reservation-list-card", expiredReservationList)
    val locationNameInFirstExpiredReservationListItem =
        getByDataTestId(
            "reservation-list-card-location-name",
            expiredReservationListCards.first()
        )
    val placeInFirstExpiredReservationListItem =
        getByDataTestId(
            "reservation-list-card-place",
            expiredReservationListCards.first()
        )
    val expiredReservationListLoader = getByDataTestId("expired-reservation-list-loader")
    val expiredReservationListAccordion = getByDataTestId("expired-reservation-list-accordion")
    val modalWindow = getByDataTestId("modal-window")
    val terminateReservationAsEmployeeButton = getByDataTestId("open-terminate-reservation-modal-for-employee")
    val terminateReservationAsEmployeeForm = getByDataTestId("terminate-reservation-employee-form")

    val terminateReservationEndDate = terminateReservationAsEmployeeForm.getByTestId("endDate")
    val terminateReservationReason = terminateReservationAsEmployeeForm.getByTestId("terminationReason")
    val terminateReservationExplanation = terminateReservationAsEmployeeForm.getByTestId("termination-explanation")
    val terminateReservationMessageTitle = terminateReservationAsEmployeeForm.getByTestId("message-title")
    val terminateReservationMessageContent = terminateReservationAsEmployeeForm.getByTestId("message-content")

    val terminationReasonInFirstReservationListItem =
        getByDataTestId("reservation-card-termination-reason", reservationListCards.first())
    val terminationCommentInFirstReservationListItem =
        getByDataTestId("reservation-card-termination-explanation", reservationListCards.first())

    val terminationDateInFirstReservationListItem =
        getByDataTestId("reservation-list-card-terminated-date", reservationListCards.first())

    val terminationReasonInFirstExpiredReservationListItem =
        getByDataTestId("reservation-card-termination-reason", expiredReservationListCards.first())
    val terminationCommentInFirstExpiredReservationListItem =
        getByDataTestId("reservation-card-termination-explanation", expiredReservationListCards.first())

    val terminationDateInFirstExpiredReservationListItem =
        getByDataTestId("reservation-list-card-terminated-date", expiredReservationListCards.first())
    val boatWarningModalWeightInput = page.locator("input[value='BoatWeight']")
    val trailerWarningModalLengthInput = page.locator("input[value='TrailerLength']")
    val trailerWarningModalWidthInput = page.locator("input[value='TrailerWidth']")
    val boatWarningModalInfoInput = getByDataTestId("warning-info-input")
    val boatWarningModalConfirmButton = page.getByTestId("ack-modal-confirm")

    fun acknowledgeWarningButton(id: Int) = getByDataTestId("acknowledge-warnings", page.getByTestId("boat-$id"))

    fun trailerAckWarningButton(id: Int) = getByDataTestId("acknowledge-warnings", page.getByTestId("trailer-$id"))

    fun renewReservationButton(id: Int) = page.getByTestId("renew-place-button-$id")

    fun trailerInformation(id: Int) = page.getByTestId("trailer-$id")

    fun trailerRegistrationCode(id: Int) = getByDataTestId("trailer-registration-code", trailerInformation(id))

    fun trailerWidth(id: Int) = getByDataTestId("trailer-width", trailerInformation(id))

    fun trailerLength(id: Int) = getByDataTestId("trailer-length", trailerInformation(id))

    fun editTrailerButton(id: Int) = page.getByTestId("edit-trailer-$id")

    val trailerRegistrationCodeInput = page.getByTestId("trailerRegistrationCode")
    val trailerWidthInput = page.getByTestId("trailerWidth")
    val trailerLengthInput = page.getByTestId("trailerLength")
    val trailerEditSubmitButton = page.getByTestId("trailer-edit-submit")
    val exceptionsNavi: Locator = getByDataTestId("exceptions-tab-navi")
}
