package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.BasePage
import fi.espoo.vekkuli.pages.citizen.components.IHaveBoatList
import fi.espoo.vekkuli.pages.citizen.components.IHaveErrorModal
import fi.espoo.vekkuli.pages.citizen.components.IHaveLoginError
import fi.espoo.vekkuli.pages.citizen.components.IHaveReservationList

class CitizenDetailsPage(
    page: Page
) : BasePage(page),
    IHaveBoatList<CitizenDetailsPage>,
    IHaveErrorModal<CitizenDetailsPage>,
    IHaveLoginError<CitizenDetailsPage>,
    IHaveReservationList<CitizenDetailsPage> {
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

    class OrganizationSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val nameField = fields.getField("Nimi")
        val businessIdField = fields.getField("Y-tunnus")
    }

    fun navigateToPage() {
        page.navigate("$baseUrl/kuntalainen/omat-tiedot")
    }

    fun getCitizenSection() = CitizenSection(getByDataTestId("citizen-information"))

    val organizationList = getByDataTestId("organization-list")
    val organizationListRows = getByDataTestId("organization-row", organizationList)
    val header = page.getByRole(AriaRole.HEADING, Page.GetByRoleOptions().setName("Omat tiedot").setExact(true))
    val boatSpaceSearchLink = page.getByRole(AriaRole.LINK, Page.GetByRoleOptions().setName("Venepaikat").setExact(true))

    fun getOrganizationsSection(text: String) = OrganizationSection(organizationListRows.filter(Locator.FilterOptions().setHasText(text)))
}
