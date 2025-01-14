package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Locator

class FieldLocator(private val root: Locator) {
    fun getField(
        label: String,
        exact: Boolean = false
    ) = root.locator("label")
        .getByText(label, Locator.GetByTextOptions().setExact(exact))
        .locator("..")
        .locator("p")

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
