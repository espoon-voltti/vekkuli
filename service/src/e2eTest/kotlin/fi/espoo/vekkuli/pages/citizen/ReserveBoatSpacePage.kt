package fi.espoo.vekkuli.pages.citizen

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
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
        val amenityBuoyCheckbox = fields.getCheckbox("Poiju merellä", true)
        val amenityRearBuoyCheckbox = fields.getCheckbox("Peräpoiju")
        val amenityBeamCheckbox = fields.getCheckbox("Aisa", true)
        val amenityWalkBeamCheckbox = fields.getCheckbox("Kävelyaisa")
        val haukilahtiCheckbox = fields.getCheckbox("Haukilahti")
        val kivenlahtiCheckbox = fields.getCheckbox("Kivenlahti")
        val svinöCheckbox = fields.getCheckbox("Svinö")
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
        val trailerRadio = fields.getRadio("Trailerisäilytys")
        val buckRadio = fields.getRadio("Pukkisäilytys")
    }

    class WinterFilterSection(
        root: Locator
    ) {
        private val fields = FieldLocator(root)
        val widthInput = fields.getInput("Säilytyspaikan leveys")
        val lengthInput = fields.getInput("Säilytyspaikan pituus")
    }

    class SearchResultsSection(
        private val root: Locator
    ) {
        val harborHeaders = root.locator(".harbor-header")
        val firstReserveButton = root.locator("button:has-text('Varaa')").first()
        val b314ReserveButton = reserveButtonByPlace("B", "314")
        val b059ReserveButton = reserveButtonByPlace("B", "059")
        val b007ReserveButton = reserveButtonByPlace("B", "007")

        internal fun reserveButtonByPlace(
            section: String,
            placeNumber: String,
        ) = root.locator("tr:has-text('$section $placeNumber')").locator("button:has-text('Varaa')")

        fun getReserveButtonPlacementByPlace(
            location: String,
            place: String
        ): Int {
            val location = root.locator("h3:has-text('$location')").locator("..").locator("..")
            val rows = location.locator("tr").all()
            return rows.indexOfFirst { row -> row.innerText().contains(place) }
        }
    }

    class ReserveModal(
        val root: Locator
    ) {
        val cancelButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Peruuta").setExact(true))
        val reserveANewSpace =
            root.getByRole(
                AriaRole.BUTTON,
                Locator.GetByRoleOptions().setName("Varaa uusi paikka").setExact(true)
            )
        val switchReservationButtons =
            root
                .getByRole(
                    AriaRole.BUTTON,
                    Locator.GetByRoleOptions().setName("Vaihdan nykyisen paikan").setExact(true)
                )
        val firstSwitchReservationButton = switchReservationButtons.first()
        val secondSwitchReservationButton =
            switchReservationButtons.nth(1)

        fun getSwitchReservationButton(place: String) =
            root.getByText(place).locator("..").locator("..").locator("..").getByRole(
                AriaRole.BUTTON,
                Locator.GetByRoleOptions().setName("Vaihdan nykyisen paikan").setExact(true)
            )
    }

    class LoginModal(
        val root: Locator
    ) {
        val cancelButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Peruuta").setExact(true))
        val continueButton = root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Jatka tunnistautumiseen").setExact(true))
    }

    val header = page.getByText("Espoon kaupungin venepaikkojen vuokraus")
    val citizenDetailsLink = page.getByRole(AriaRole.LINK, Page.GetByRoleOptions().setName("Omat tiedot").setExact(true))
    val switchInfoBox = page.getByText("Olet vaihtamassa paikkaa")
    val switchGoBackButton = page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Palaa takaisin").setExact(true))

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

    fun filterForSlipBoatSpace() {
        val filterSection = getFilterSection()
        filterSection.slipRadio.click()
        val slipFilterSection = filterSection.getSlipFilterSection()
        slipFilterSection.boatTypeSelect.selectOption("Sailboat")
        slipFilterSection.widthInput.fill("1")
        slipFilterSection.lengthInput.fill("3")
    }

    fun filterForSlipBoatSpaceB001AndÄ001() {
        val filterSection = getFilterSection()
        filterSection.slipRadio.click()
        val slipFilterSection = filterSection.getSlipFilterSection()
        slipFilterSection.boatTypeSelect.selectOption("OutboardMotor")
        slipFilterSection.widthInput.fill("1.7")
        slipFilterSection.lengthInput.fill("4")
        slipFilterSection.svinöCheckbox.click()
    }

    fun filterForTrailerSpace012() {
        val filterSection = getFilterSection()
        filterSection.trailerRadio.click()
        val slipFilterSection = filterSection.getTrailerFilterSection()
        slipFilterSection.widthInput.fill("2")
        slipFilterSection.lengthInput.fill("4")
    }

    // Winter space
    fun filterForWinterSpaceB013() {
        val filterSection = getFilterSection()
        filterSection.winterRadio.click()
        val slipFilterSection = filterSection.getWinterFilterSection()
        slipFilterSection.widthInput.fill("1")
        slipFilterSection.lengthInput.fill("3")
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

    fun startReservingWinterBoatSpaceB013() {
        filterForWinterSpaceB013()
        getSearchResultsSection().firstReserveButton.click()
    }

    fun reserveStorageWithTrailerType(
        filterSection: FilterSection,
        storageFilterSection: StorageFilterSection
    ) {
        val citizenHomePage = CitizenHomePage(page)
        citizenHomePage.loginAsLeoKorhonen()
        citizenHomePage.navigateToPage()
        citizenHomePage.languageSelector.click()
        citizenHomePage.languageSelector.getByText("Suomi").click()
        navigateToPage()

        filterSection.storageRadio.click()

        storageFilterSection.trailerRadio.click()
        storageFilterSection.widthInput.fill("1.23")
        storageFilterSection.lengthInput.fill("3.45")

        getSearchResultsSection().b007ReserveButton.click()
        val form = BoatSpaceFormPage(page)
        form.fillFormAndSubmit {
            assertThat(form.getReservedSpaceSection().storageTypeField).hasText("Trailerisäilytys")
            getWinterStorageTypeSection().trailerRegistrationNumberInput.fill("ABC-123")
        }

        PaymentPage(page).payReservation()
        assertThat(PaymentPage(page).reservationSuccessNotification).isVisible()
    }

    fun reserveTrailerBoatSpace(): BoatSpaceFormPage {
        val filterSection = getFilterSection()
        filterSection.trailerRadio.click()
        val trailerFilterSection = filterSection.getTrailerFilterSection()
        trailerFilterSection.widthInput.fill("1")
        trailerFilterSection.lengthInput.fill("3")

        getSearchResultsSection().firstReserveButton.click()

        val form = BoatSpaceFormPage(page)
        form.fillFormAndSubmit {
            getBoatSection().widthInput.fill("2")
            getBoatSection().lengthInput.fill("5")
            getTrailerStorageTypeSection().trailerRegistrationNumberInput.fill("ABC-123")
        }
        PaymentPage(page).payReservation()
        return form
    }

    fun reserveWinterBoatSpace() {
        navigateToPage()

        startReservingWinterBoatSpaceB013()

        // click send to trigger validation
        val formPage = BoatSpaceFormPage(page)

        val trailerRegistrationCode = "ID12345"
        val trailerWidth = "1.5"
        val trailerLength = "3.5"

        formPage.fillFormAndSubmit {
            getBoatSection().widthInput.fill("1")
            getBoatSection().lengthInput.fill("3")
            val winterStorageTypeSection = getWinterStorageTypeSection()
            winterStorageTypeSection.buckStorageTypeRadio.click()
            winterStorageTypeSection.trailerStorageTypeRadio.click()
            winterStorageTypeSection.trailerRegistrationNumberInput.fill(trailerRegistrationCode)
            winterStorageTypeSection.trailerWidthInput.fill(trailerWidth)
            winterStorageTypeSection.trailerLengthInput.fill(trailerLength)
        }

        // assert that payment title is shown
        val paymentPage = PaymentPage(page)
        // Then go through the payment
        paymentPage.nordeaSuccessButton.click()
        // Now we should be on the confirmation page
        val confirmationPage = ConfirmationPage(page)
        assertThat(confirmationPage.reservationSuccessNotification).isVisible()

        val citizenDetailPage = CitizenDetailsPage(page)
        citizenDetailPage.navigateToPage()

        val reservationSection = citizenDetailPage.getReservationSection("Talvipaikka: Haukilahti B 013")
        val trailerSection = reservationSection.getTrailerSection()
        page.waitForCondition { trailerSection.widthField.isVisible }
        assertThat(trailerSection.widthField).containsText(trailerWidth.replace('.', ','))
        assertThat(trailerSection.lengthField).containsText(trailerLength.replace('.', ','))
        assertThat(trailerSection.registrationCodeField).containsText(trailerRegistrationCode)
    }
}
