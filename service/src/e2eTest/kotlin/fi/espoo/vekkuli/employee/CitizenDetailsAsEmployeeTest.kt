package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.ReserveTest
import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReasonOptions
import fi.espoo.vekkuli.pages.citizen.CitizenHomePage
import fi.espoo.vekkuli.pages.citizen.components.IHaveBoatList
import fi.espoo.vekkuli.pages.employee.CitizenDetailsPage
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.pages.employee.ReservationListPage
import fi.espoo.vekkuli.pages.getByDataTestId
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.shared.CitizenIds
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfWinterReservationPeriod
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import fi.espoo.vekkuli.pages.citizen.BoatSpaceFormPage as CitizenBoatSpaceFormPage
import fi.espoo.vekkuli.pages.citizen.CitizenDetailsPage as CitizenCitizenDetailsPage
import fi.espoo.vekkuli.pages.citizen.PaymentPage as CitizenPaymentPage
import fi.espoo.vekkuli.pages.citizen.ReserveBoatSpacePage as CitizenReserveBoatSpacePage

@ActiveProfiles("test")
class CitizenDetailsAsEmployeeTest : ReserveTest() {
    @Test
    fun listingReservations() {
        try {
            val listingPage = reservationListPage()
            assertThat(listingPage.boatSpace1).isVisible()
            assertThat(listingPage.boatSpace2).isVisible()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun editCitizen() {
        try {
            val listingPage = reservationListPage()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            citizenDetails.editButton.click()

            assertThat(page.getByTestId("edit-citizen-form")).isVisible()
            val citizenFirstName = "New First Name"
            val citizenLastName = "New Last Name"
            val citizenPhone = "0405839281"
            val citizenEmail = "test2@email.com"
            val citizenAddress = "New Address"
            val citizenNationalId = "031195-950Y"
            val citizenPostalCode = "12345"
            val citizenMunicipalityCode = "49"

            citizenDetails.citizenFirstNameInput.fill(citizenFirstName)
            citizenDetails.citizenLastNameInput.fill(citizenLastName)
            citizenDetails.citizenAddressInput.fill(citizenAddress)
            citizenDetails.citizenEmailInput.fill("")
            citizenDetails.citizenPhoneInput.fill("")
            citizenDetails.citizenNationalIdInput.fill(citizenNationalId)
            citizenDetails.citizenPostalCodeInput.fill(citizenPostalCode)
            citizenDetails.citizenMunicipalityInput.selectOption(citizenMunicipalityCode)
            citizenDetails.citizenEditSubmitButton.click()

            // assert that email and phone can not be empty
            assertThat(citizenDetails.citizenEmailError).isVisible()
            assertThat(citizenDetails.citizenPhoneError).isVisible()
            citizenDetails.citizenEmailInput.fill("asd")
            citizenDetails.citizenPhoneInput.fill("asd")
            citizenDetails.citizenEditSubmitButton.click()

            // assert that email and phone have to be valid
            assertThat(citizenDetails.citizenEmailPatternError).isVisible()
            assertThat(citizenDetails.citizenPhonePatternError).isVisible()
            citizenDetails.citizenEmailInput.fill(citizenEmail)
            citizenDetails.citizenPhoneInput.fill(citizenPhone)
            citizenDetails.citizenEditSubmitButton.click()

            // assert that the values are updated
            assertThat(citizenDetails.citizenFirstNameField).hasText(citizenFirstName)
            assertThat(citizenDetails.citizenLastNameField).hasText(citizenLastName)
            assertThat(citizenDetails.citizenPhoneField).hasText(citizenPhone)
            assertThat(citizenDetails.citizenEmailField).hasText(citizenEmail)
            assertThat(citizenDetails.citizenAddressField).hasText(citizenAddress)
            assertThat(citizenDetails.citizenNationalIdField).hasText(citizenNationalId)
            assertThat(citizenDetails.citizenPostalCodeField).hasText(citizenPostalCode)
            assertThat(citizenDetails.citizenMunicipalityField).hasText("Espoo")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun userMemos() {
        try {
            val listingPage = reservationListPage()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.memoNavi.clickAndWaitForHtmxSettle()

            citizenDetails.addNewMemoBtn.clickAndWaitForHtmxSettle()
            val text = "This is a new memo"
            val memoId = 2
            citizenDetails.newMemoContent.fill(text)
            citizenDetails.newMemoSaveBtn.clickAndWaitForHtmxSettle()

            assertThat(citizenDetails.userMemo(memoId)).containsText(text)

            // Edit memo
            val newText = "Edited memo"
            citizenDetails.userMemo(memoId).getByTestId("edit-memo-button").clickAndWaitForHtmxSettle()
            citizenDetails.userMemo(memoId).getByTestId("edit-memo-content").fill(newText)
            citizenDetails.userMemo(memoId).getByTestId("save-edit-button").clickAndWaitForHtmxSettle()
            assertThat(citizenDetails.userMemo(memoId).locator(".memo-content")).containsText(newText)

            // Delete memo
            page.onDialog { it.accept() }
            citizenDetails.userMemo(memoId).getByTestId("delete-memo-button").clickAndWaitForHtmxSettle()
            assertThat(citizenDetails.userMemo(memoId)).hasCount(0)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun userMessages() {
        try {
            val listingPage = reservationListPage()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.messagesNavi.click()
            assertThat(citizenDetails.messages).containsText("Käyttöveden katko")
            citizenDetails.messages.click()
            assertThat(
                citizenDetails.messageContent
            ).hasText("Haukilahden satamassa on käyttöveden katko 2.9.2024 klo 12-14. Pahoittelemme häiriötä.")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Email is sent to reserver`() {
        val listingPage = reservationListPage()
        listingPage.boatSpace1.click()
        val citizenDetails = CitizenDetailsPage(page)
        citizenDetails.messagesNavi.click()
        assertThat(citizenDetails.sendMessageLink).isVisible()
        citizenDetails.sendMessageLink.click()
        assertThat(citizenDetails.sendReserverMessageForm).isVisible()
        assertThat(citizenDetails.sendReserverMessageModalSubtitle).containsText(
            "Varauksia 1 kpl, viestin vastaanottajia 1 kpl."
        )
        val emailSubject = "Email message title"
        val emailBody = "Email message content"
        citizenDetails.sendReserverMessageTitleInput.fill(emailSubject)
        citizenDetails.sendReserverMessageTitleInput.blur()
        citizenDetails.sendReserverMessageContentInput.fill(emailBody)
        citizenDetails.sendReserverMessageContentInput.blur()

        citizenDetails.sendReserverMessageModalSubmit.click()
        assertThat(citizenDetails.sendReserverMessageModalSuccess).isVisible()

        messageService.sendScheduledEmails()
        assertEquals(1, SendEmailServiceMock.emails.size)
        val email = SendEmailServiceMock.emails[0]
        assertEquals(email.subject, emailSubject)
        assertEquals(email.body, emailBody)

        citizenDetails.messagesNavi.click()
        assertThat(citizenDetails.messages.first()).containsText(emailSubject)
        citizenDetails.messages.first().click()
        assertThat(
            citizenDetails.messageContent
        ).hasText(emailBody)
    }

    @Test
    fun userPaymentsShowsInfoIfThereIsNoPayments() {
        try {
            val listingPage = reservationListPage()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.paymentsNavi.click()
            assertThat(citizenDetails.noPaymentsIndicator).isVisible()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun editBoat() {
        try {
            val listingPage = reservationListPage()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            citizenDetails.showAllBoatsButton.click()
            citizenDetails.editBoatButton(3).click()
            assertThat(page.getByTestId("form")).isVisible()

            citizenDetails.nameInput.fill("New Boat Name")
            citizenDetails.weightInput.fill("2000")
            citizenDetails.typeSelect.selectOption("Sailboat")
            citizenDetails.depthInput.fill("1.50")
            citizenDetails.widthInput.fill("3.00")
            citizenDetails.registrationNumberInput.fill("ABC123")
            citizenDetails.length.fill("6.00")
            citizenDetails.ownership.selectOption("Owner")
            citizenDetails.otherIdentifier.fill("ID12345")
            citizenDetails.extraInformation.fill("Extra info")

            citizenDetails.submitButton.click()
            assertThat(citizenDetails.nameText(3)).hasText("New Boat Name")
            assertThat(citizenDetails.weightText(3)).hasText("2000")
            assertThat(citizenDetails.typeText(3)).hasText("Sailboat")
            assertThat(citizenDetails.depthText(3)).hasText("1,50")
            assertThat(citizenDetails.widthText(3)).hasText("3,00")
            assertThat(citizenDetails.registrationNumberText(3)).hasText("ABC123")

            assertThat(citizenDetails.lengthText(3)).hasText("6,00")
            assertThat(citizenDetails.ownershipText(3)).hasText("Owner")
            assertThat(citizenDetails.otherIdentifierText(3)).hasText("ID12345")
            assertThat(citizenDetails.extraInformationText(3)).hasText("Extra info")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee editing a boat do not trigger weight warning`() {
        EmployeeHomePage(page).employeeLogin()

        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        listingPage.boatSpace1.click()

        val citizenDetails = CitizenDetailsPage(page)
        val boatId = 1
        citizenDetails.editBoatButton(boatId).click()
        assertThat(page.getByTestId("form")).isVisible()

        citizenDetails.weightInput.fill("16000")
        citizenDetails.submitButton.click()
        assertThat(citizenDetails.weightText(boatId)).hasText("16000")

        page.reload() // warnings show only after full reload
        assertThat(citizenDetails.acknowledgeWarningButton(boatId)).not().isVisible()
    }

    @Test
    fun `editing a boat considers all reservations for warnings`() {
        val boatId =
            createReservationWarningsForMikkoVirtanen { boat ->
                // modify boat to fit in B 314 but not in TRAILERI 012
                boat.lengthInput.fill("7.25")
            }

        val employeeHomePage = EmployeeHomePage(page)
        employeeHomePage.employeeLogin()

        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        listingPage.boatSpace("Virtanen Mikko").click()

        val citizenDetails = CitizenDetailsPage(page)
        assertThat(citizenDetails.acknowledgeWarningButton(boatId)).isVisible()
    }

    @Test
    fun `warnings are acknowledged from each reservation`() {
        val boatId =
            createReservationWarningsForMikkoVirtanen { boat ->
                // modify boat to add warnings to both reservations
                boat.weightInput.fill("16000")
            }

        val employeeHomePage = EmployeeHomePage(page)
        employeeHomePage.employeeLogin()

        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        listingPage.boatSpace("Virtanen Mikko").click()

        val citizenDetails = CitizenDetailsPage(page)
        citizenDetails.acknowledgeWarningButton(boatId).click()
        assertThat(citizenDetails.boatWarningModalWeightInput).isVisible()
        citizenDetails.boatWarningModalWeightInput.click()
        citizenDetails.boatWarningModalInfoInput.fill("some text")
        citizenDetails.boatWarningModalConfirmButton.click()

        assertThat(citizenDetails.acknowledgeWarningButton(boatId)).not().isVisible()
    }

    @Test
    fun `warnings are deleted when reservation is terminated`() {
        CitizenHomePage(page).loginAsMikkoVirtanen()

        val reserveBoatSpacePage = CitizenReserveBoatSpacePage(page)
        val boatSpaceFormPage = CitizenBoatSpaceFormPage(page)
        val paymentPage = CitizenPaymentPage(page)
        val boatName = "The Boat"

        // create a reservation for B 314
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()
        boatSpaceFormPage.fillFormAndSubmit {
            val boatSection = getBoatSection()
            boatSection.nameInput.fill(boatName)
        }
        paymentPage.payReservation()

        // create warning
        val citizenCitizenDetails = CitizenCitizenDetailsPage(page)
        citizenCitizenDetails.navigateToPage()
        val boat = citizenCitizenDetails.getBoatSection(boatName)
        boat.editButton.click()
        boat.weightInput.fill("16000")
        boat.saveButton.click()

        // check warning
        val employeeHomePage = EmployeeHomePage(page)
        employeeHomePage.employeeLogin()

        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        listingPage.boatSpace("Virtanen Mikko").click()

        val citizenDetails = CitizenDetailsPage(page)
        val boatId = 8
        assertThat(citizenDetails.acknowledgeWarningButton(boatId)).isVisible()

        // terminate reservation
        citizenDetails.terminateReservationAsEmployeeButton.click()
        citizenDetails.terminateReservationReason.selectOption(ReservationTerminationReasonOptions.PaymentViolation.toString())
        citizenDetails.terminateReservationModalConfirm.click()
        assertThat(citizenDetails.terminateReservationSuccess).isVisible()
        citizenDetails.hideModalWindow()

        // check warning is removed
        citizenDetails.showAllBoatsButton.click()
        assertThat(citizenDetails.acknowledgeWarningButton(boatId)).not().isVisible()
    }

    @Test
    fun deleteBoat() {
        try {
            val listingPage = reservationListPage()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            assertThat(citizenDetails.citizenDetailsSection).isVisible()
            citizenDetails.showAllBoatsButton.click()
            assertThat(page.getByTestId("boat-3")).isVisible()
            page.getByTestId("delete-boat-3").click()
            page.getByTestId("delete-modal-confirm-3").click()
            assertThat(page.getByTestId("boat-3")).isHidden()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `employee can set the reserver exceptions`() {
        try {
            val listingPage = reservationListPage()
            listingPage.exceptionsFilter.click()
            page.waitForCondition { listingPage.getByDataTestId("reserver-name").count() == 1 }
            val reserver = listingPage.getByDataTestId("reserver-name").first()
            assertThat(reserver).containsText("Pulkkinen Jorma")
            reserver.click()

            val citizenDetails = CitizenDetailsPage(page)
            assertThat(citizenDetails.citizenLastNameField).hasText("Pulkkinen")
            citizenDetails.exceptionsNavi.click()

            val espooRulesAppliedCheckbox = page.getByDataTestId("edit-espoorules-applied-checkbox")
            val espooRulesAppliedCheckboxContent = page.getByDataTestId("espoorules-applied-checkbox")
            // Check that cancel does not save the changes
            citizenDetails.exceptionsEditButton.click()
            espooRulesAppliedCheckbox.click()
            citizenDetails.exceptionsCancelButton.click()
            assertThat(espooRulesAppliedCheckboxContent).isChecked()

            // Make sure that the attention indicator is shown when exceptions are added
            assertThat(citizenDetails.getByDataTestId("exceptions-tab-attention")).hasClass("attention on")
            citizenDetails.exceptionsEditButton.click()
            assertThat(espooRulesAppliedCheckbox).isChecked()
            espooRulesAppliedCheckbox.click()
            assertFalse((espooRulesAppliedCheckbox).isChecked)
            citizenDetails.exceptionsSubmitButton.click()
            assertThat(citizenDetails.getByDataTestId("exceptions-tab-attention")).hasClass("attention")

            citizenDetails.exceptionsEditButton.click()
            val discount0 = page.getByDataTestId("reserver_discount_0")
            assertThat(discount0).isChecked()
            page.getByTestId("reserver_discount_50").check()

            citizenDetails.exceptionsSubmitButton.click()

            assertThat(citizenDetails.getByDataTestId("exceptions-tab-attention")).hasClass("attention on")
            listingPage.navigateTo()
            listingPage.exceptionsFilter.click()

            page.waitForCondition { listingPage.reservations.count() == 1 }
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `citizen details should shield against XSS scripts when in edit mode`() {
        try {
            EmployeeHomePage(page).employeeLogin()
            val citizenDetails = CitizenDetailsPage(page)

            // Inject XSS scripts to citizen information from citizen details page and return assertions
            val assertions = injectXSSToCitizenInformation(page, CitizenIds.leo)

            // Make sure htmx has settled
            assertThat(citizenDetails.editButton).isVisible()
            // The script was run setting to edit mode after the injection
            citizenDetails.editButton.click()
            // Make sure htmx has settled
            assertThat(citizenDetails.citizenFirstNameInput).isVisible()

            assertions()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `citizen details should shield agains XSS scripts in memos`() {
        try {
            val maliciousValue = "XSS_ATTACK_MEMO"

            EmployeeHomePage(page).employeeLogin()
            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.navigateToUserPage(CitizenIds.leo)

            citizenDetails.memoNavi.clickAndWaitForHtmxSettle()
            citizenDetails.addNewMemoBtn.clickAndWaitForHtmxSettle()
            citizenDetails.newMemoContent.fill(maliciousCode(maliciousValue))
            citizenDetails.newMemoSaveBtn.clickAndWaitForHtmxSettle()

            assertFalse(page.evaluate("() => window.hasOwnProperty('$maliciousValue')") as Boolean, "XSS script was executed on user memo")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun employeeCanSetAGeneralWarningToReservationAndAcknowledgeIt() {
        try {
            val infoText = "Some text relating to the reservation"
            val listingPage = reservationListPage()
            listingPage.boatSpace1.click()
            val citizenDetails = CitizenDetailsPage(page)
            // click the link to add general warning
            citizenDetails.addNewGeneralWarningLink.clickAndWaitForHtmxSettle()

            // modal should be opened, add text and save the warning
            assertThat(citizenDetails.generalWarningModal).isVisible()
            val infoInput = citizenDetails.generalWarningInfoInput
            infoInput.fill(infoText)
            citizenDetails.generalWarningSaveBtn.clickAndWaitForHtmxSettle()
            assertThat(citizenDetails.generalWarningModal).not().isVisible()

            // warning indicator should be visible, click the link and check the inserted text is still present
            citizenDetails.acknowledgeGeneralWarningLink.click()
            assertThat(infoInput).containsText(infoText)

            // update the text and save
            val updatedText = "$infoText UPDATE"
            infoInput.fill(updatedText)
            citizenDetails.generalWarningUpdateBtn.click()
            assertThat(citizenDetails.generalWarningModal).not().isVisible()

            // click the warning indicator and verify the info text has been updated
            citizenDetails.acknowledgeGeneralWarningLink.click()
            assertThat(infoInput).containsText(updatedText)

            // set the warning as acknowledged and verify the reservation has again the link to add warning
            citizenDetails.generalWarningAcknowledgeBtn.click()
            assertThat(citizenDetails.generalWarningModal).not().isVisible()
            assertThat(citizenDetails.addNewGeneralWarningLink).isVisible()

            // check that the info text is saved to memo, user has one memo from seed, so memo id is 2
            citizenDetails.memoNavi.clickAndWaitForHtmxSettle()
            assertThat(citizenDetails.userMemo(2)).containsText(updatedText)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `employee can change the boat in a citizen reservation`() {
        try {
            val employeeHomePage = EmployeeHomePage(page)
            val existingBoat = "Leon vene"
            val newBoat = "Leon toinen liian iso vene"
            employeeHomePage.employeeLogin()

            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.navigateToUserPage(CitizenIds.leo)

            val boat = citizenDetails.boatInFirstBoatSpaceReservationCard
            assertThat(boat).containsText(existingBoat)

            citizenDetails.editBoatInFirstBoatSpaceReservationCard.click()
            citizenDetails.changeBoatSelect.selectOption(newBoat)
            citizenDetails.changeBoatConfirm.click()

            assertThat(boat).containsText(newBoat)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `employee can add a new boat to a reserver`() {
        try {
            val employeeHomePage = EmployeeHomePage(page)
            val newBoatName = "Leon uusiovene III"
            val newBoatWeight = "1000"
            val newBoatType = "Sailboat"
            val newBoatDepth = "1.5"
            val newBoatWidth = "3.0"
            val newBoatLength = "6.0"
            val newBoatRegNum = "ABC-123"
            val newBoatOwnership = "Owner"
            val newBoatOtherId = "ID12345"
            val newBoatExtraInfo = "Extra info"

            // I'm quite not sure where this comes from. Probably seed state?
            val newBoatId = 8

            employeeHomePage.employeeLogin()

            val citizenDetails = CitizenDetailsPage(page)
            citizenDetails.navigateToUserPage(CitizenIds.leo)

            citizenDetails.openAddNewBoatModal.click()

            citizenDetails.addNewBoatModalNameInput.fill(newBoatName)
            citizenDetails.addNewBoatModalWeightInput.fill(newBoatWeight)
            citizenDetails.addNewBoatModalTypeSelect.selectOption(newBoatType)
            citizenDetails.addNewBoatModalDepthInput.fill(newBoatDepth)
            citizenDetails.addNewBoatModalWidthInput.fill(newBoatWidth)
            citizenDetails.addNewBoatModalLengthInput.fill(newBoatLength)
            citizenDetails.addNewBoatModalRegNumInput.fill(newBoatRegNum)
            citizenDetails.addNewBoatModalOwnershipSelect.selectOption(newBoatOwnership)
            citizenDetails.addNewBoatModalOtherIdInput.fill(newBoatOtherId)
            citizenDetails.addNewBoatModalExtraInfoInput.fill(newBoatExtraInfo)

            citizenDetails.addNewBoatModalConfirm.click()

            citizenDetails.showAllBoatsButton.click()

            assertThat(citizenDetails.nameText(newBoatId)).hasText(newBoatName)
            assertThat(citizenDetails.weightText(newBoatId)).hasText(newBoatWeight)
            assertThat(citizenDetails.typeText(newBoatId)).hasText(newBoatType)
            assertThat(citizenDetails.depthText(newBoatId)).containsText(newBoatDepth.replace(".", ","))
            assertThat(citizenDetails.widthText(newBoatId)).containsText(newBoatWidth.replace(".", ","))
            assertThat(citizenDetails.lengthText(newBoatId)).containsText(newBoatLength.replace(".", ","))
            assertThat(citizenDetails.registrationNumberText(newBoatId)).hasText(newBoatRegNum)
            assertThat(citizenDetails.ownershipText(newBoatId)).hasText(newBoatOwnership)
            assertThat(citizenDetails.otherIdentifierText(newBoatId)).hasText(newBoatOtherId)
            assertThat(citizenDetails.extraInformationText(newBoatId)).hasText(newBoatExtraInfo)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    private fun reservationListPage(): ReservationListPage {
        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin()

        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        return listingPage
    }

    private fun createReservationWarningsForMikkoVirtanen(callback: (boatSection: IHaveBoatList.BoatSection) -> Unit): Int {
        mockTimeProvider(timeProvider, startOfWinterReservationPeriod)

        CitizenHomePage(page).loginAsMikkoVirtanen()
        val reserveBoatSpacePage = CitizenReserveBoatSpacePage(page)
        val boatSpaceFormPage = CitizenBoatSpaceFormPage(page)
        val paymentPage = CitizenPaymentPage(page)

        // create a reservation for TRAILERI 012
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpace012()
        boatSpaceFormPage.fillFormAndSubmit {
            getBoatSection().nameInput.fill("The Boat")
            getBoatSection().widthInput.fill("1")
            getBoatSection().lengthInput.fill("1")
            getTrailerStorageTypeSection().trailerRegistrationNumberInput.fill("ABC-123")
        }
        paymentPage.payReservation()

        // create a reservation for B 314
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()
        boatSpaceFormPage.fillFormAndSubmit {
            getBoatSection().existingBoat("The Boat").click()
        }
        paymentPage.payReservation()

        // modify boat
        val boatId = 8
        val citizenCitizenDetails = CitizenCitizenDetailsPage(page)
        citizenCitizenDetails.navigateToPage()
        val boat = citizenCitizenDetails.getBoatSection("The Boat")
        boat.editButton.click()
        callback(boat)
        boat.saveButton.click()

        return boatId
    }
}
