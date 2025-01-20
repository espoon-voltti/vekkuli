package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.AriaRole
import fi.espoo.vekkuli.baseUrl
import fi.espoo.vekkuli.pages.BasePage

class ReserveBoatSpacePage(
    page: Page
) : BasePage(page) {
    class FilterSection(
        private val root: Locator
    ) {
        private val fields = FieldLocator(root)
        val slipRadio = fields.getRadio("Laituripaikka")
        val trailerRadio = fields.getRadio("Traileripaikka")

        val storageRadio = fields.getRadio("Säilytyspaikka Ämmäsmäessä (ympärivuotinen)")

        val winterRadio = fields.getRadio("Talvipaikka")

        fun getSlipFilterSection() = SlipFilterSection(root)

        fun getTrailerFilterSection() = TrailerFilterSection(root)

        fun getStorageFilterSection() = StorageFilterSection(root)

        fun getWinterFilterSection() = WinterFilterSection(root)
    }

    class SlipFilterSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val boatTypeSelect = fields.getSelect("Venetyyppi")
        val widthInput = fields.getInput("Veneen leveys")
        val lengthInput = fields.getInput("Veneen pituus")
        val amenityBuoyCheckbox = fields.getCheckbox("Poiju", true)
        val amenityRearBuoyCheckbox = fields.getCheckbox("Peräpoiju")
        val amenityBeamCheckbox = fields.getCheckbox("Aisa", true)
        val amenityWalkBeamCheckbox = fields.getCheckbox("Kävelyaisa")
        val haukilahtiCheckbox = fields.getCheckbox("Haukilahti")
        val kivenlahtiCheckbox = fields.getCheckbox("Kivenlahti")
    }

    class TrailerFilterSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val widthInput = fields.getInput("Trailerin leveys")
        val lengthInput = fields.getInput("Trailerin pituus")
    }

    class StorageFilterSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val widthInput = fields.getInput("Säilytyspaikan leveys (m)")
        val lengthInput = fields.getInput("Säilytyspaikan pituus (m)")
    }

    class WinterFilterSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val widthInput = fields.getInput("Säilytyspaikan leveys")
        val lengthInput = fields.getInput("Säilytyspaikan pituus")
    }

    class SearchResultsSection(
        root: Locator
    ) {
        val harborHeaders = root.locator(".harbor-header")
        val firstReserveButton = root.locator("button:has-text('Varaa')").first()
        val b314ReserveButton = root.locator("tr:has-text('B 314')").locator("button:has-text('Varaa')")
        val b059ReserveButton = root.locator("tr:has-text('B 059')").locator("button:has-text('Varaa')")
    }

    class ReserveModal(
        val root: Locator
    ) {
        val cancelButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Peruuta").setExact(true))
        val reserveAnotherButton =
            root.getByRole(
                AriaRole.BUTTON,
                Locator.GetByRoleOptions().setName("Varaan toisen paikan").setExact(true)
            )
        val switchReservationButtons =
            root
                .getByRole(
                    AriaRole.BUTTON,
                    Locator.GetByRoleOptions().setName("Vaihdan nykyisen paikan").setExact(true)
                )
        val firstSwitchReservationButton = switchReservationButtons.first()
    }

    class ReserveModal(
        val root: Locator
    ) {
        val cancelButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Peruuta").setExact(true))
        val reserveAnotherButton =
            root.getByRole(
                AriaRole.BUTTON,
                Locator.GetByRoleOptions().setName("Varaan toisen paikan").setExact(true)
            )
        val switchReservationButtons =
            root
                .getByRole(
                    AriaRole.BUTTON,
                    Locator.GetByRoleOptions().setName("Vaihdan nykyisen paikan").setExact(true)
                )
        val firstSwitchReservationButton = switchReservationButtons.first()
    }

    class LoginModal(
        val root: Locator
    ) {
        val cancelButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Peruuta").setExact(true))
        val continueButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Jatka tunnistautumiseen").setExact(true))
    }

    val header = page.getByText("Espoon kaupungin venepaikkojen vuokraus")

    fun navigateToPage() {
        page.navigate("$baseUrl/kuntalainen/venepaikka")
    }

    fun getFilterSection() = FilterSection(getByDataTestId("boat-space-filter"))

    fun getSearchResultsSection() = SearchResultsSection(getByDataTestId("boat-space-results"))

    fun getLoginModal() = LoginModal(getByDataTestId("login-before-reserving"))

    fun getReserveModal() = ReserveModal(getByDataTestId("reserve-modal"))

    fun filterForBoatSpaceB314() {
        val filterSection = getFilterSection()
        filterSection.slipRadio.click()
        val slipFilterSection = filterSection.getSlipFilterSection()
        slipFilterSection.boatTypeSelect.selectOption("Sailboat")
        slipFilterSection.widthInput.fill("3")
        slipFilterSection.lengthInput.fill("6")
    }

    fun filterForBoatSpaceB059() {
        val filterSection = getFilterSection()
        filterSection.slipRadio.click()
        val slipFilterSection = filterSection.getSlipFilterSection()
        slipFilterSection.boatTypeSelect.selectOption("Sailboat")
        slipFilterSection.widthInput.fill("2")
        slipFilterSection.lengthInput.fill("5")
    }

    fun filterForTrailerSpace012() {
        val filterSection = getFilterSection()
        filterSection.trailerRadio.click()
        val slipFilterSection = filterSection.getTrailerFilterSection()
        slipFilterSection.widthInput.fill("2")
        slipFilterSection.lengthInput.fill("4")
    }

    fun filterForStorageSpaceB007() {
        val filterSection = getFilterSection()
        filterSection.storageRadio.click()
        val slipFilterSection = filterSection.getStorageFilterSection()
        slipFilterSection.widthInput.fill("2")
        slipFilterSection.lengthInput.fill("4")
    }

    fun startReservingBoatSpaceB314() {
        filterForBoatSpaceB314()
        getSearchResultsSection().b314ReserveButton.click()
    }

    fun startReservingBoatSpaceB059() {
        filterForBoatSpaceB059()
        getSearchResultsSection().b059ReserveButton.click()
    }

    fun startReservingBoatSpace012() {
        filterForTrailerSpace012()
        page.locator("tr:has-text('TRAILERI 012')").locator("button:has-text('Varaa')").click()
    }

    fun startReservingStorageSpaceB007() {
        filterForStorageSpaceB007()
        page.locator("tr:has-text('B 007')").locator("button:has-text('Varaa')").click()
    }
}
