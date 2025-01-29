package fi.espoo.vekkuli.pages.citizen.components

import com.microsoft.playwright.Locator
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.pages.BasePage
import fi.espoo.vekkuli.pages.citizen.FieldLocator

interface IHaveBoatList<T> : IGetByTestId<T> where T : BasePage, T : IHaveBoatList<T> {
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

    val boatList: Locator get() = getByDataTestId("boat-list")
    val boatListRows: Locator get() = getByDataTestId("boat-row", boatList)

    fun getBoatSection(text: String) = BoatSection(boatListRows.filter(Locator.FilterOptions().setHasText(text)))

    fun getDeleteBoatModal() = DeleteBoatModal(getByDataTestId("delete-boat-modal"))

    fun getDeleteBoatSuccessModal() = DeleteBoatSuccessModal(getByDataTestId("delete-boat-success-modal"))

    val showAllBoatsButton: Locator get() =
        FieldLocator(
            getByDataTestId("boat-list")
        ).getInput("Näytä myös veneet joita ei ole liitetty venepaikkoihin")
}
