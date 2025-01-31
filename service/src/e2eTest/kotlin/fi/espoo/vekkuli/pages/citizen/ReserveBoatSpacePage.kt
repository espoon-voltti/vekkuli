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
        val b314ReserveButton = root.locator("tr:has-text('B 314')").locator("button:has-text('Varaa')")
        val b059ReserveButton = root.locator("tr:has-text('B 059')").locator("button:has-text('Varaa')")
        val b007ReserveButton = root.locator("tr:has-text('B 007')").locator("button:has-text('Varaa')")

        internal fun reserveButtonByPlace(
            section: String,
            placeNumber: String,
        ) = root.locator("tr:has-text('$section $placeNumber')").locator("button:has-text('Varaa')")
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
        storageFilterSection.widthInput.fill("1")
        storageFilterSection.lengthInput.fill("3")

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

    fun reserveWinterBoatSpace(
        filterSection: FilterSection,
        winterFilterSection: WinterFilterSection
    ) {
        navigateToPage()

        filterSection.winterRadio.click()

        winterFilterSection.widthInput.fill("1")
        winterFilterSection.lengthInput.fill("3")

        val searchResultsSection = getSearchResultsSection()
        searchResultsSection.firstReserveButton.click()

        // click send to trigger validation
        val formPage = BoatSpaceFormPage(page)
        formPage.submitButton.click()

        val boatSection = formPage.getBoatSection()
        assertThat(boatSection.widthError).isVisible()
        assertThat(boatSection.lengthError).isVisible()
        assertThat(boatSection.depthError).isVisible()
        assertThat(boatSection.weightError).isVisible()
        assertThat(boatSection.registrationNumberError).isVisible()

        val userAgreementSection = formPage.getUserAgreementSection()
        assertThat(userAgreementSection.certifyInfoError).isVisible()
        assertThat(userAgreementSection.agreementError).isVisible()

        assertThat(formPage.validationWarning).isVisible()

        // Fill in the boat information
        boatSection.typeSelect.selectOption("Sailboat")

        boatSection.widthInput.clear()
        assertThat(boatSection.widthError).isVisible()

        boatSection.lengthInput.clear()
        assertThat(boatSection.lengthError).isVisible()

        boatSection.widthInput.fill("-1")
        assertThat(boatSection.widthError).isVisible()
        assertThat(boatSection.widthError).hasText("Anna positiivinen luku")

        boatSection.nameInput.fill("My Boat")
        assertThat(boatSection.nameError).isHidden()

        boatSection.lengthInput.fill("3")
        assertThat(boatSection.lengthError).isHidden()

        boatSection.widthInput.fill("25")
        assertThat(boatSection.widthError).isHidden()

        boatSection.depthInput.fill("1.5")
        assertThat(boatSection.depthError).isHidden()

        boatSection.weightInput.fill("2000")
        assertThat(boatSection.weightError).isHidden()

        boatSection.otherIdentifierInput.fill("ID12345")
        assertThat(boatSection.otherIdentifierError).isHidden()

        boatSection.noRegistrationCheckbox.check()
        assertThat(boatSection.registrationNumberError).isHidden()

        boatSection.ownerRadio.click()

        val citizenSection = formPage.getCitizenSection()
        citizenSection.emailInput.fill("test@example.com")
        assertThat(citizenSection.emailError).isHidden()

        citizenSection.phoneInput.fill("123456789")
        assertThat(citizenSection.phoneError).isHidden()

        val winterStorageTypeSection = formPage.getWinterStorageTypeSection()
        val reservedSpaceSection = formPage.getReservedSpaceSection()

        assertThat(winterStorageTypeSection.trailerRegistrationNumberError).isVisible()
        assertThat(reservedSpaceSection.storageTypeField).hasText("Trailerisäilytys")
        winterStorageTypeSection.buckStorageTypeRadio.click()
        assertThat(reservedSpaceSection.storageTypeField).hasText("Pukkisäilytys")
        assertThat(winterStorageTypeSection.trailerRegistrationNumberInput).isHidden()

        winterStorageTypeSection.trailerStorageTypeRadio.click()
        assertThat(winterStorageTypeSection.trailerRegistrationNumberInput).isVisible()

        val trailerRegistrationCode = "ID12345"

        winterStorageTypeSection.trailerRegistrationNumberInput.fill(trailerRegistrationCode)
        winterStorageTypeSection.trailerWidthInput.fill("1.5")
        winterStorageTypeSection.trailerLengthInput.fill("2.5")
        assertThat(reservedSpaceSection.storageTypeField).hasText("Trailerisäilytys")

        userAgreementSection.certifyInfoCheckbox.check()
        userAgreementSection.agreementCheckbox.check()
        formPage.submitButton.click()

        // assert that payment title is shown
        val paymentPage = PaymentPage(page)
        // Cancel the payment at first
        paymentPage.nordeaFailedButton.click()
        // Then go through the payment
        paymentPage.nordeaSuccessButton.click()
        // Now we should be on the confirmation page
        val confirmationPage = ConfirmationPage(page)
        assertThat(confirmationPage.reservationSuccessNotification).isVisible()

        val citizenDetailPage =
            fi.espoo.vekkuli.pages.citizen
                .CitizenDetailsPage(page)
        citizenDetailPage.navigateToPage()

        val reservationSection = citizenDetailPage.getReservationSection("Talvipaikka: Haukilahti B 013")
        val trailerSection = reservationSection.getTrailerSection()

        page.waitForCondition { trailerSection.widthField.isVisible }
        assertThat(trailerSection.widthField).containsText("1,50")
        assertThat(trailerSection.lengthField).containsText("2,50")
        assertThat(trailerSection.registrationCodeField).containsText(trailerRegistrationCode)
    }
}
