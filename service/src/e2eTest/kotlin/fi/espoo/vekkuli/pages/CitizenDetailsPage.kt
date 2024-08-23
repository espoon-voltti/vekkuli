package fi.espoo.vekkuli.pages

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page

class CitizenDetailsPage(
    private val page: Page
) {
    val citizenDetailsSection = page.getByTestId("citizen-details")

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

    val submitButton: Locator = page.getByTestId("submit")
    val cancelButton: Locator = page.getByTestId("cancel")
}
