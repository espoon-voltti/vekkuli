package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.pages.BasePage

class BoatSpaceFormPage(
    page: Page
) : BasePage(page) {
    class CitizenSection(root: Locator) {
        private val fields = FieldLocator(root)
        val emailError = fields.getInputError("Sähköposti")
        val emailInput = fields.getInput("Sähköposti")
        val phoneError = fields.getInputError("Puhelinnumero")
        val phoneInput = fields.getInput("Puhelinnumero")
    }

    class BoatSection(root: Locator) {
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
    }

    class UserAgreementSection(root: Locator) {
        private val fields = FieldLocator(root)
        val certifyInfoCheckbox = fields.getCheckbox("Vakuutan antamani tiedot oikeiksi")
        val certifyInfoError = fields.getCheckboxError("Vakuutan antamani tiedot oikeiksi")
        val agreementCheckbox = fields.getCheckbox("Olen lukenut venesatamasäännöt")
        val agreementError = fields.getCheckboxError("Olen lukenut venesatamasäännöt")
    }

    class FieldLocator(private val root: Locator) {
        fun getInput(
            label: String,
            exact: Boolean = false
        ) = root.locator("label")
            .getByText(label, Locator.GetByTextOptions().setExact(exact))
            .locator("..")
            .locator("input")

        fun getInputError(
            label: String,
            exact: Boolean = false
        ) = root.locator("label")
            .getByText(label, Locator.GetByTextOptions().setExact(exact))
            .locator("..")
            .locator(".help.is-danger:not(:empty)")

        fun getCheckbox(
            label: String,
            exact: Boolean = false
        ) = root.locator("label.checkbox span")
            .getByText(label, Locator.GetByTextOptions().setExact(exact))
            .locator("..")
            .locator("input[type=checkbox]")

        fun getCheckboxError(
            label: String,
            exact: Boolean = false
        ) = root.locator("label.checkbox span")
            .getByText(label, Locator.GetByTextOptions().setExact(exact))
            .locator("..")
            .locator("input[type=checkbox]")

        fun getRadio(
            label: String,
            exact: Boolean = false
        ) = root.locator("label .body")
            .getByText(label, Locator.GetByTextOptions().setExact(exact))
            .locator("..")
            .locator("..")
            .locator("input[type=radio]")

        fun getSelect(
            label: String,
            exact: Boolean = false
        ) = root.locator("label")
            .getByText(label, Locator.GetByTextOptions().setExact(exact))
            .locator("..")
            .locator("select")
    }

    fun getCitizenSection() = CitizenSection(getByDataTestId("citizen"))

    fun getBoatSection() = BoatSection(getByDataTestId("boat"))

    fun getUserAgreementSection() = UserAgreementSection(getByDataTestId("user-agreement"))

    val validationWarning = page.locator(".form-validation-message").getByText("Pakollisia tietoja puuttuu")
    val submitButton = page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Jatka maksamaan").setExact(true))
}
