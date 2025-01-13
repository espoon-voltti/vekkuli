package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.BasePage

class CitizenDetailsPage(
    page: Page
) : BasePage(page) {
    inner class BoatSection(locator: Locator) {
        val editButton = locator.getByRole(AriaRole.LINK, Locator.GetByRoleOptions().setName("Muokkaa veneen tietoja").setExact(true))
        val saveButton = locator.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Tallenna muutokset").setExact(true))

        val depthField = getField("Syväys (m)", locator)
        val depthInput = getInput("Syväys (m) *", locator)
        val extraInformationField = getField("Lisätiedot", locator)
        val extraInformationInput = getInput("Lisätiedot", locator)
        val lengthField = getField("Pituus (m)", locator)
        val lengthInput = getInput("Pituus (m) *", locator)
        val nameField = getField("Veneen nimi", locator)
        val nameInput = getInput("Veneen nimi *", locator)
        val otherIdentifierField = getField("Muu tunniste", locator)
        val otherIdentifierInput = getInput("Muu tunniste *", locator)
        val ownershipField = getField("Omistussuhde", locator)
        val ownershipSelect = getSelect("Omistussuhde *", locator)
        val registrationNumberField = getField("Rekisteritunnus", locator)
        val registrationNumberInput = getInput("Rekisteritunnus", locator)
        val typeField = getField("Veneen tyyppi", locator)
        val typeSelect = getSelect("Veneen tyyppi *", locator)
        val weightField = getField("Paino (kg)", locator)
        val weightInput = getInput("Paino (kg) *", locator)
        val widthField = getField("Leveys (m)", locator)
        val widthInput = getInput("Leveys (m) *", locator)
    }

    inner class CitizenSection(locator: Locator) {
        val editButton = locator.getByRole(AriaRole.LINK, Locator.GetByRoleOptions().setName("Muokkaa").setExact(true))
        val saveButton = locator.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Tallenna muutokset").setExact(true))

        val emailError = getInputError("Sähköposti", locator)
        val emailField = getField("Sähköposti", locator)
        val emailInput = getInput("Sähköposti", locator)
        val municipalityField = getField("Kotikunta", locator)
        val phoneError = getInputError("Puhelinnumero", locator)
        val phoneField = getField("Puhelinnumero", locator)
        val phoneInput = getInput("Puhelinnumero", locator)
    }

    inner class ReservationSection(locator: Locator) {
        val terminateButton = locator.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Irtisano paikka").setExact(true))

        val locationName = getField("Satama", locator)
        val place = getField("Paikka", locator)

        val trailerSection = TrailerSection(getByDataTestId("trailer-information", locator))
    }

    inner class TrailerSection(locator: Locator) {
        val editButton = locator.getByRole(AriaRole.LINK, Locator.GetByRoleOptions().setName("Muokkaa trailerin tietoja").setExact(true))
        val saveButton = locator.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Tallenna muutokset").setExact(true))
        val cancelButton = locator.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Peruuta").setExact(true))

        val lengthField = getField("Pituus (m)", locator)
        val lengthInput = getInput("Pituus (m)", locator)

        val registrationCodeField = getField("Rekisterinumero", locator)
        val registrationCodeInput = getInput("Rekisterinumero", locator)

        val widthField = getField("Leveys (m)", locator)
        val widthInput = getInput("Leveys (m)", locator)
    }

    inner class TerminateReservationModal(locator: Locator) {
        val element = locator
        val cancelButton = locator.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Peruuta").setExact(true))
        val confirmButton = locator.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Irtisano venepaikka").setExact(true))

        val placeIdentifierText = getByDataTestId("place-identifier", locator)
        val boatSpaceText = getByDataTestId("boat-space", locator)
        val amenityText = getByDataTestId("amenity", locator)
    }

    inner class TerminateReservationFailureModal(locator: Locator) {
        val element = locator
        val okButton = locator.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Ok").setExact(true))
    }

    fun navigateToPage() {
        page.navigate("$baseUrl/kuntalainen/omat-tiedot?lang=en")
    }

//    fun hideModalWindow() {
//        modalWindow.click(
//            Locator
//                .ClickOptions()
//                .setPosition(5.0, 5.0)
//        )
//    }

    fun getCitizenSection() = CitizenSection(getByDataTestId("citizen-information"))

    fun getBoatSection(id: Int) = BoatSection(getByDataTestId("boat-$id"))

    val showAllBoatsButton = getInput("Näytä myös veneet joita ei ole liitetty venepaikkoihin")

    val reservationList = getByDataTestId("reservation-list")
    val reservationListCards = getByDataTestId("reservation-list-card", reservationList)

    fun getReservationSection(nth: Int) = ReservationSection(reservationListCards.nth(nth))

    fun getFirstReservationSection() = getReservationSection(0)

    val expiredReservationList = getByDataTestId("expired-reservation-list")
    val expiredReservationListCards = getByDataTestId("reservation-list-card", expiredReservationList)

    fun getExpiredReservationSection(nth: Int) = ReservationSection(expiredReservationListCards.nth(nth))

    fun getFirstExpiredReservationSection() = getExpiredReservationSection(0)

    val showExpiredReservationsToggle = expiredReservationList.getByText("Päättyneet varaukset")

    fun getTerminateReservationModal() = TerminateReservationModal(getByDataTestId("terminate-reservation-modal"))

    fun getTerminateReservationFailureModal() = TerminateReservationFailureModal(getByDataTestId("terminate-reservation-failure-modal"))

    val terminateReservationSuccessModal = getByDataTestId("terminate-reservation-success-modal")

    private fun getInput(
        label: String,
        locator: Locator? = null
    ) = getInputWrapper(label, locator).locator("input")

    private fun getSelect(
        label: String,
        locator: Locator? = null
    ) = getInputWrapper(label, locator).locator("select")

    private fun getField(
        label: String,
        locator: Locator? = null
    ) = getInputWrapper(label, locator).locator("p")

    private fun getInputError(
        label: String,
        locator: Locator? = null
    ) = getInputWrapper(label, locator).locator(".help.is-danger")

    private fun getInputWrapper(
        label: String,
        locator: Locator? = null
    ): Locator {
        val labelElement =
            if (locator != null) {
                locator.locator("label", Locator.LocatorOptions().setHasText(label))
            } else {
                page.locator("label", Page.LocatorOptions().setHasText(label))
            }

        return labelElement.locator("..")
    }
}
