package fi.espoo.vekkuli.pages.employee

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.BasePage

class ReservationListPage(
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

        page.navigate("$baseUrl/virkailija/venepaikat/varaukset$paramsString")
    }

    private val filterLocator = { filter: String -> getByDataTestId("filter-$filter") }

    val header = page.getByTestId("reservations-header")
    val boatSpace1 = page.locator("#boat-space-1").first()
    val boatSpace2 = page.locator("#boat-space-2").first()
    val boatSpace8 = page.locator("#boat-space-8").first()
    val boatSpaceLeoKorhonen = page.locator("tr:has-text('Korhonen Leo')").first()
    val boatSpaceJormaPulkkinen = page.locator("tr:has-text('Pulkkinen Jorma')").first()
    val createReservation = page.locator("#create-reservation")
    val warningIcon = getByDataTestId("warning-icon", boatSpace1)
    val warningIcon8 = getByDataTestId("warning-icon", boatSpace8)
    val reservationsTableB314Row = page.locator("tr:has-text('B 314')")
    val reservationsTableB314RowEndDate = getByDataTestId("reservation-end-date", reservationsTableB314Row)
    val reservations = page.locator(".reservation-item")
    val reserverRowEmail = getByDataTestId("reserver-email")
    val searchInput = { inputName: String -> getByDataTestId("search-input-$inputName") }
    val reservationExpiration = { state: String -> filterLocator("reservation-expiration-$state") }
    val boatSpaceTypeFilter = { type: String -> filterLocator("type-$type") }
    val reservationStateFilter = { state: String -> filterLocator("reservation-state-$state") }
    val reservationValidityFilter = { validity: String -> filterLocator("reservation-validity-$validity") }
    val exceptionsFilter = filterLocator("exceptions")
    val expandingSelectionFilter = { selection: String -> filterLocator("selection-$selection") }
    val amenityFilter = { amenity: String -> filterLocator("amenity-$amenity") }
    val dateFilter = filterLocator("date")
    val reservationValidFromInput: Locator = page.getByTestId("reservationValidFrom")
    val reservationValidUntilInput: Locator = page.getByTestId("reservationValidUntil")
    val selectAllReservations = getByDataTestId("select-all-toggle")
    val reservationRowCheckBox = { reservationId: Int ->
        getByDataTestId("reservation-$reservationId")
    }
    val sendMassMessageLink = getByDataTestId("send-mass-email-link")
    val sendMassMessageForm = getByDataTestId("send-mass-email")
    val sendMassMessageModalSubtitle = getByDataTestId("send-mass-email-modal-subtitle")
    val sendMassMessageTitleInput = getByDataTestId("message-title")
    val sendMassMessageContentInput = getByDataTestId("message-content")
    val sendMassMessageModalSubmit = getByDataTestId("send-mass-email-modal-confirm")
    val sendMassMessageModalSuccess = getByDataTestId("message-sent-success-modal")
    val warningsFilterCheckbox = filterLocator("warnings")

    fun boatSpace(customer: String) =
        getByDataTestId("reserver-name")
            .getByText(customer)
            .first()
}
