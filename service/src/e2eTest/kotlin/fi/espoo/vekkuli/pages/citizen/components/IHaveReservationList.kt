package fi.espoo.vekkuli.pages.citizen.components

import com.microsoft.playwright.Locator
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.pages.BasePage
import fi.espoo.vekkuli.pages.citizen.FieldLocator
import fi.espoo.vekkuli.pages.getByDataTestId

interface IHaveReservationList<T> : IGetByTestId<T> where T : BasePage, T : IHaveReservationList<T> {
    class ReservationSection(
        val root: Locator
    ) {
        private val fields = FieldLocator(root)
        val terminateButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Irtisano paikka"))
        val renewButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Maksa kausimaksu"))
        val switchSpace = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Vaihda paikka"))

        val locationName = fields.getField("Satama")
        val place = fields.getField("Paikka")
        val validity = fields.getField("Varaus voimassa")
        val paymentStatus = fields.getField("Maksun tila")

        val renewNotification = root.getByText("Sopimusaika päättymässä. Varmista sama paikka ensi kaudelle maksamalla kausimaksu")

        fun getTrailerSection() = TrailerSection(root.getByDataTestId("trailer-information"))
    }

    class TerminateReservationModal(
        val root: Locator
    ) {
        val cancelButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Peruuta").setExact(true))
        val confirmButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Irtisano venepaikka").setExact(true))

        val placeIdentifierText = root.getByDataTestId("place-identifier")
        val boatSpaceText = root.getByDataTestId("boat-space")
        val amenityText = root.getByDataTestId("amenity")
    }

    class TerminateReservationFailureModal(
        val root: Locator
    ) {
        val okButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Ok").setExact(true))
    }

    class TerminateReservationSuccessModal(
        val root: Locator
    )

    class TrailerSection(
        val root: Locator
    ) {
        private val fields = FieldLocator(root)
        val editButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Muokkaa trailerin tietoja").setExact(true))
        val saveButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Tallenna muutokset").setExact(true))
        val cancelButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Peruuta").setExact(true))

        val lengthField = fields.getField("Pituus")
        val lengthInput = fields.getInput("Pituus")

        val registrationCodeField = fields.getField("Rekisterinumero")
        val registrationCodeInput = fields.getInput("Rekisterinumero")

        val widthField = fields.getField("Leveys")
        val widthInput = fields.getInput("Leveys")
    }

    val reservationList: Locator get() = getByDataTestId("reservation-list")
    val reservationListCards: Locator get() = getByDataTestId("reservation-list-card", reservationList)

    fun getReservationSection(text: String) = ReservationSection(reservationListCards.filter(Locator.FilterOptions().setHasText(text)))

    fun getReservationSection(nth: Int) = ReservationSection(reservationListCards.nth(nth))

    @Deprecated("Use getReservationSection(text) instead")
    fun getFirstReservationSection() = getReservationSection(0)

    val expiredReservationList: Locator get() = getByDataTestId("expired-reservation-list")
    val expiredReservationListCards: Locator get() = getByDataTestId("reservation-list-card", expiredReservationList)

    fun getExpiredReservationSection(text: String) =
        ReservationSection(
            expiredReservationListCards.filter(
                Locator.FilterOptions().setHasText(text)
            )
        )

    fun getExpiredReservationSection(nth: Int) = ReservationSection(expiredReservationListCards.nth(nth))

    @Deprecated("Use getExpiredReservationSection(text) instead")
    fun getFirstExpiredReservationSection() = getExpiredReservationSection(0)

    val showExpiredReservationsToggle: Locator get() = expiredReservationList.getByText("Päättyneet varaukset")

    fun getTerminateReservationModal() = TerminateReservationModal(getByDataTestId("terminate-reservation-modal"))

    fun getTerminateReservationFailureModal() = TerminateReservationFailureModal(getByDataTestId("terminate-reservation-failure-modal"))

    fun getTerminateReservationSuccessModal() = TerminateReservationSuccessModal(getByDataTestId("terminate-reservation-success-modal"))
}
