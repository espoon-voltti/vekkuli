package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Page
import fi.espoo.vekkuli.pages.BasePage
import fi.espoo.vekkuli.pages.citizen.components.IHaveReservationList

class OrganizationDetailsPage(
    page: Page
) : BasePage(page), IHaveReservationList<OrganizationDetailsPage>
