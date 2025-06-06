package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.Page.*
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.pages.BasePage
import fi.espoo.vekkuli.pages.citizen.components.IHaveErrorModal

open class BoatSpaceFormPage(
    page: Page
) : BasePage(page),
    IHaveErrorModal<CitizenDetailsPage> {
    class CitizenSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val emailError = fields.getInputError("Sähköposti")
        val emailInput = fields.getInput("Sähköposti")
        val phoneError = fields.getInputError("Puhelinnumero")
        val phoneInput = fields.getInput("Puhelinnumero")
    }

    class OrganizationSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val reserveForOrganization = fields.getRadio("Varaan yhteisön puolesta")
        val reserveForCitizen = fields.getRadio("Varaan yksityishenkilönä")
        val phoneNumberInput = fields.getInput("Puhelinnumero")
        val emailInput = fields.getInput("Sähköposti")

        fun organization(name: String) = fields.getRadio(name)
    }

    class BoatSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val depthError = fields.getInputError("Syväys")
        val depthInput = fields.getInput("Syväys")
        val extraInformationError = fields.getInputError("Lisätiedot")
        val extraInformationInput = fields.getInput("Lisätiedot")
        val lengthError = fields.getInputError("Pituus")
        val lengthInput = fields.getInput("Pituus")
        val nameError = fields.getInputError("Veneen nimi")
        val nameInput = fields.getInput("Veneen nimi")
        val noRegistrationCheckbox = fields.getCheckbox("Ei rekisterinumeroa")
        val otherIdentifierError = fields.getInputError("Muu tunniste")
        val otherIdentifierInput = fields.getInput("Muu tunniste")
        val ownerRadio = fields.getRadio("Omistan veneen", true)
        val registrationNumberError = fields.getInputError("Rekisteritunnus")
        val registrationNumberInput = fields.getInput("Rekisteritunnus")
        val typeError = fields.getInputError("Venetyyppi")
        val typeSelect = fields.getSelect("Venetyyppi")
        val weightError = fields.getInputError("Paino")
        val weightInput = fields.getInput("Paino")
        val widthError = fields.getInputError("Leveys")
        val widthInput = fields.getInput("Leveys")
        val newBoatSelection = fields.getRadio("Uusi vene")
        val boatSizeWarning = root.getByTestId("boatSize-warning")
        val boatWeightWarning = root.getByTestId("boatWeight-warning")
        val boatSizeWarningBackButton = boatSizeWarning.getByText("Palaa takaisin")

        fun existingBoat(name: String) = fields.getRadio(name)
    }

    class WinterStorageTypeSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val buckStorageTypeRadio = fields.getRadio("Pukkisäilytys", true)
        val buckWithTentStorageTypeRadio = fields.getRadio("Pukkisäilytys suojateltalla")
        val trailerLengthError = fields.getInputError("Pituus")
        val trailerLengthInput = fields.getInput("Pituus")
        val trailerRegistrationNumberError = fields.getInputError("Rekisterinumero")
        val trailerRegistrationNumberInput = fields.getInput("Rekisterinumero")
        val trailerStorageTypeRadio = fields.getRadio("Trailerisäilytys")
        val trailerWidthError = fields.getInputError("Leveys")
        val trailerWidthInput = fields.getInput("Leveys")
    }

    class TrailerStorageTypeSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val trailerLengthError = fields.getInputError("Pituus")
        val trailerLengthInput = fields.getInput("Pituus")
        val trailerRegistrationNumberError = fields.getInputError("Rekisterinumero")
        val trailerRegistrationNumberInput = fields.getInput("Rekisterinumero")
        val trailerWidthError = fields.getInputError("Leveys")
        val trailerWidthInput = fields.getInput("Leveys")
    }

    class UserAgreementSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val certifyInfoCheckbox = fields.getCheckbox("Vakuutan antamani tiedot oikeiksi")
        val certifyInfoError = fields.getCheckboxError("Vakuutan antamani tiedot oikeiksi")
        val agreementCheckbox = fields.getCheckbox("Olen lukenut venesatamien sopimusehdot")
        val agreementError = fields.getCheckboxError("Olen lukenut venesatamien sopimusehdot")
    }

    class ReservedSpaceSection(
        root: Locator
    ) {
        val fields = FieldLocator(root)
        val storageTypeField = fields.getField("Säilytystapa")
    }

    class ConfirmCancelReservationModal(
        val root: Locator
    ) {
        val cancelButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Ei, palaa takaisin").setExact(true))
        val confirmButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Kyllä, peru varaus").setExact(true))
    }

    fun getCitizenSection() = CitizenSection(getByDataTestId("citizen"))

    fun getOrganizationSection() = OrganizationSection(getByDataTestId("organization"))

    fun getBoatSection() = BoatSection(getByDataTestId("boat"))

    fun getWinterStorageTypeSection() = WinterStorageTypeSection(getByDataTestId("winter-storage-type"))

    fun getTrailerStorageTypeSection() = TrailerStorageTypeSection(getByDataTestId("trailer-storage-type"))

    fun getUserAgreementSection() = UserAgreementSection(getByDataTestId("user-agreement"))

    fun getReservedSpaceSection() = ReservedSpaceSection(getByDataTestId("reserved-space"))

    fun getConfirmCancelReservationModal() = ConfirmCancelReservationModal(getByDataTestId("confirm-cancel-reservation-modal"))

    val validationWarning = page.locator(".form-validation-message").getByText("Pakollisia tietoja puuttuu")
    val submitButton = page.getByRole(AriaRole.BUTTON, GetByRoleOptions().setName("Jatka maksamaan").setExact(true))
    val confirmButton = page.getByRole(AriaRole.BUTTON, GetByRoleOptions().setName("Vahvista varaus").setExact(true))
    val cancelButton = page.getByRole(AriaRole.BUTTON, GetByRoleOptions().setName("Peruuta varaus").setExact(true))
    val header = page.getByText("Venepaikan varaus:")
    val switchInfoBox = page.getByText("Olet vaihtamassa paikkaa")

    fun fillFormAndSubmit(overrides: (BoatSpaceFormPage.() -> Unit)? = null) {
        val citizenSection = getCitizenSection()
        citizenSection.emailInput.fill("test@example.com")
        citizenSection.phoneInput.fill("123456789")

        val boatSection = getBoatSection()
        boatSection.depthInput.fill("1.5")

        boatSection.weightInput.fill("2000")
        boatSection.nameInput.fill("My Boat")
        boatSection.otherIdentifierInput.fill("ID12345")
        boatSection.noRegistrationCheckbox.check()
        boatSection.ownerRadio.check()

        val userAgreementSection = getUserAgreementSection()
        userAgreementSection.certifyInfoCheckbox.check()
        userAgreementSection.agreementCheckbox.check()

        overrides?.invoke(this)

        resolveSubmitButton().click()
    }

    protected open fun resolveSubmitButton(): Locator = submitButton
}
