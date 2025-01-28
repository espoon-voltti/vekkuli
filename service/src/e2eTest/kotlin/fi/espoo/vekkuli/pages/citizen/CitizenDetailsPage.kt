package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.BasePage
import fi.espoo.vekkuli.pages.citizen.components.IHaveReservationList

class CitizenDetailsPage(
    page: Page
) : BasePage(page), IHaveReservationList<OrganizationDetailsPage> {
    class BoatSection(
        val root: Locator
    ) {
        private val fields = FieldLocator(root)
        val editButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Muokkaa veneen tietoja").setExact(true))
        val saveButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Tallenna muutokset").setExact(true))
        val deleteButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Poista vene").setExact(true))

        val depthField = fields.getField("Syväys")
        val depthInput = fields.getInput("Syväys")
        val extraInformationField = fields.getField("Lisätiedot")
        val extraInformationInput = fields.getInput("Lisätiedot")
        val lengthField = fields.getField("Pituus")
        val lengthInput = fields.getInput("Pituus")
        val nameField = fields.getField("Veneen nimi")
        val nameInput = fields.getInput("Veneen nimi")
        val otherIdentifierField = fields.getField("Muu tunniste")
        val otherIdentifierInput = fields.getInput("Muu tunniste")
        val ownershipField = fields.getField("Omistussuhde")
        val ownershipSelect = fields.getSelect("Omistussuhde")
        val registrationNumberField = fields.getField("Rekisteritunnus")
        val registrationNumberInput = fields.getInput("Rekisteritunnus")
        val typeField = fields.getField("Veneen tyyppi")
        val typeSelect = fields.getSelect("Veneen tyyppi")
        val weightField = fields.getField("Paino")
        val weightInput = fields.getInput("Paino")
        val widthField = fields.getField("Leveys")
        val widthInput = fields.getInput("Leveys")
    }

    class DeleteBoatModal(
        val root: Locator
    ) {
        val cancelButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Peruuta").setExact(true))
        val confirmButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Poista vene").setExact(true))
    }

    class DeleteBoatSuccessModal(
        val root: Locator
    )

    class CitizenSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val editButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Muokkaa").setExact(true))
        val saveButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Tallenna muutokset").setExact(true))

        val emailError = fields.getInputError("Sähköposti")
        val emailField = fields.getField("Sähköposti")
        val emailInput = fields.getInput("Sähköposti")
        val municipalityField = fields.getField("Kotikunta")
        val phoneError = fields.getInputError("Puhelinnumero")
        val phoneField = fields.getField("Puhelinnumero")
        val phoneInput = fields.getInput("Puhelinnumero")
    }

    class OrganizationSection(root: Locator) {
        private val fields = FieldLocator(root)
        val nameField = fields.getField("Nimi")
        val businessIdField = fields.getField("Y-tunnus")
    }

    inner class ErrorModal(
        private val root: Locator
    ) {
        private val fields = FieldLocator(root)
        val title = root.getByRole(AriaRole.HEADING, Locator.GetByRoleOptions().setName("Varaaminen ei onnistunut").setExact(true))
        val okButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Ok").setExact(true))
    }

    fun navigateToPage() {
        page.navigate("$baseUrl/kuntalainen/omat-tiedot?lang=en")
    }

    fun getCitizenSection() = CitizenSection(getByDataTestId("citizen-information"))

    val organizationList = getByDataTestId("organization-list")
    val organizationListRows = getByDataTestId("organization-row", organizationList)

    fun getOrganizationsSection(text: String) = OrganizationSection(organizationListRows.filter(Locator.FilterOptions().setHasText(text)))

    fun getErrorModal() = ErrorModal(getByDataTestId("error-modal"))

    fun getBoatSection(id: Int) = BoatSection(getByDataTestId("boat-$id"))

    fun getDeleteBoatModal() = DeleteBoatModal(getByDataTestId("delete-boat-modal"))

    fun getDeleteBoatSuccessModal() = DeleteBoatSuccessModal(getByDataTestId("delete-boat-success-modal"))

    val showAllBoatsButton = FieldLocator(page.locator("body")).getInput("Näytä myös veneet joita ei ole liitetty venepaikkoihin")
}
