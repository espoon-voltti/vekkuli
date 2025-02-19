package fi.espoo.vekkuli.pages.citizen.components

import com.microsoft.playwright.Locator
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.pages.BasePage

interface IHaveOrganizationContactList<T> : IGetByTestId<T> where T : BasePage, T : IHaveOrganizationContactList<T> {
    class ContactListItem(
        private val root: Locator
    ) {
        val name = root.locator("div").nth(0)
        val phone = root.locator("div").nth(1)
        val email = root.locator("div").nth(2)
    }

    class ContactListSection(
        val root: Locator
    ) {
        val title = root.getByRole(AriaRole.HEADING)
        val labels = root.locator("label")
    }

    val contactList: Locator get() = getByDataTestId("organization-contact-list")
    val contactListItems: Locator get() = getByDataTestId("organization-contact-list-item", contactList)

    fun getContactListItems(): List<ContactListItem> = contactListItems.all().map { ContactListItem(it) }

    fun getContactList() = ContactListSection(contactList)
}
