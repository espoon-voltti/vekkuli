package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.pages.employee.CitizenDetailsPage
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.pages.employee.ReservationListPage
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.shared.CitizenIds
import fi.espoo.vekkuli.shared.OrganizationIds
import fi.espoo.vekkuli.utils.formatAsTestDate
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.util.regex.Pattern
import kotlin.test.assertEquals

@ActiveProfiles("test")
class EmployeeReservationListingTest : PlaywrightTest() {
    @Test
    fun `Employee can filter boat spaces`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 6 }
        listingPage.boatSpaceTypeFilter("Winter").click()
        page.waitForCondition { listingPage.reservations.count() == 1 }
    }

    @Test
    fun `Employee can filter by reserver phone number`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 6 }
        typeText(listingPage.searchInput("phoneSearch"), "04056")
        page.waitForCondition { listingPage.reservations.count() == 1 }

        listingPage.boatSpace1.click()

        val citizenDetails = CitizenDetailsPage(page)
        assertThat(citizenDetails.citizenDetailsSection).isVisible()
    }

    @Test
    fun `Employee can filter by reserver email address`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 6 }
        typeText(listingPage.searchInput("emailSearch"), "leo")
        page.waitForCondition { listingPage.reservations.count() == 1 }
        assertThat(listingPage.reserverRowEmail).hasText("leo@noreplytest.fi")
        listingPage.boatSpace1.click()

        val citizenDetails = CitizenDetailsPage(page)
        assertThat(citizenDetails.citizenDetailsSection).isVisible()
    }

    @Test
    fun `Employee can filter by reserver name`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 6 }
        typeText(listingPage.searchInput("nameSearch"), "leo")
        page.waitForCondition { listingPage.reservations.count() == 1 }

        listingPage.boatSpace1.click()

        val citizenDetails = CitizenDetailsPage(page)
        assertThat(citizenDetails.citizenDetailsSection).isVisible()
    }

    @Test
    fun `Employee can filter by reservation validity`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 6 }
        listingPage.reservationValidityFilter(ReservationValidity.FixedTerm.toString()).click()
        page.waitForCondition { listingPage.reservations.count() == 1 }
        assertThat(listingPage.getByDataTestId("place").first()).containsText("B 003")
    }

    @Test
    fun `Employee can filter by reserver exceptions`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 6 }
        listingPage.exceptionsFilter.click()
        page.waitForCondition { listingPage.getByDataTestId("reserver-name").count() == 1 }
        assertThat(listingPage.getByDataTestId("reserver-name").first()).containsText("Pulkkinen Jorma")
    }

    @Test
    fun `Employee can filter by amenity`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 6 }
        listingPage.expandingSelectionFilter("amenity").click()
        listingPage.amenityFilter(BoatSpaceAmenity.Trailer.name).click()
        page.waitForCondition { listingPage.reservations.count() == 2 }
        assertThat(listingPage.getByDataTestId("place").first()).containsText("B 009")
        listingPage.amenityFilter(BoatSpaceAmenity.Beam.name).click()
        page.waitForCondition { listingPage.reservations.count() == 6 }
    }

    @Test
    fun `Employee can filter by date`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 6 }
        listingPage.reservationValidFromInput.fill(formatAsTestDate(LocalDate.of(2024, 1, 1)))
        listingPage.reservationValidUntilInput.fill(formatAsTestDate(LocalDate.of(2024, 12, 31)))
        listingPage.dateFilter.click()
        page.waitForCondition { listingPage.reservations.count() == 1 }
        assertThat(listingPage.getByDataTestId("place").first()).containsText("B 003")
        listingPage.dateFilter.click()
        page.waitForCondition { listingPage.reservations.count() == 6 }
    }

    @Test
    fun `should sort the reservations by most recent warnings first when warning filter is chosen`() {
        val listingPage = reservationListPage()
        assertThat(listingPage.warningsFilterCheckbox).not().isChecked()
        val warningsFilter = page.getByTestId("employee-reservation-list-warnings-filter")
        assertThat(warningsFilter).containsText("(0 reservations)")
        listingPage.boatSpaceLeoKorhonen.click()
        val citizenDetails = CitizenDetailsPage(page)
        // click the link to add general warning
        citizenDetails.addNewGeneralWarningLink.clickAndWaitForHtmxSettle()
        assertThat(citizenDetails.generalWarningModal).isVisible()
        citizenDetails.generalWarningSaveBtn.clickAndWaitForHtmxSettle()
        listingPage.navigateTo()
        assertThat(warningsFilter).containsText("(1 reservations)")
        listingPage.boatSpaceJormaPulkkinen.click()
        // click the link to add general warning
        citizenDetails.addNewGeneralWarningLink.clickAndWaitForHtmxSettle()
        assertThat(citizenDetails.generalWarningModal).isVisible()
        citizenDetails.generalWarningSaveBtn.clickAndWaitForHtmxSettle()
        listingPage.navigateTo()
        assertThat(warningsFilter).containsText("(2 reservations)")
        // Warnings should update based on the filters
        listingPage.reservationValidityFilter(ReservationValidity.FixedTerm.name).click()
        page.waitForCondition { listingPage.reservations.count() == 1 }
        assertThat(warningsFilter.first()).containsText("(0 reservations)")
        listingPage.reservationValidityFilter(ReservationValidity.FixedTerm.name).click()
        assertThat(warningsFilter).containsText("(2 reservations)")

        // Jorma Pulkkinen should be on top since it has most recent warning
        listingPage.warningsFilterCheckbox.click()
        assertThat(listingPage.reservations).hasCount(2)
        assertThat(listingPage.reservations.first()).containsText("Pulkkinen")
        assertThat(listingPage.reservations.last()).containsText("Korhonen")
    }

    @Test
    fun `should only show general warnings when filter is select`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 6 }
        assertThat(listingPage.generalWarningsFilterCheckbox).not().isChecked()
        assertThat(listingPage.generalWarningsFilterLabel).containsText("(0 reservations)")
        listingPage.boatSpaceLeoKorhonen.click()
        val citizenDetails = CitizenDetailsPage(page)
        // click the link to add general warning
        citizenDetails.addNewGeneralWarningLink.clickAndWaitForHtmxSettle()
        assertThat(citizenDetails.generalWarningModal).isVisible()
        citizenDetails.generalWarningSaveBtn.clickAndWaitForHtmxSettle()
        listingPage.navigateTo()
        assertThat(listingPage.generalWarningsFilterLabel).containsText("(1 reservations)")
        listingPage.boatSpaceJormaPulkkinen.click()
        // click the link to add general warning
        citizenDetails.addNewGeneralWarningLink.clickAndWaitForHtmxSettle()
        assertThat(citizenDetails.generalWarningModal).isVisible()
        citizenDetails.generalWarningSaveBtn.clickAndWaitForHtmxSettle()
        listingPage.navigateTo()

        // Selecting the general warnings filter should show only reservations with general warnings
        assertThat(listingPage.generalWarningsFilterLabel).containsText("(2 reservations)")
        listingPage.generalWarningsFilterCheckbox.click()
        page.waitForCondition { listingPage.reservations.count() == 2 }

        // Toggling the filter should show all reservations again
        listingPage.generalWarningsFilterCheckbox.click()
        assertThat(listingPage.generalWarningsFilterCheckbox).not().isChecked()
        page.waitForCondition { listingPage.reservations.count() == 6 }
    }

    @Test
    fun `Send mass email link is enabled and opens a send message modal when reservation list is not empty`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 6 }
        assertThat(listingPage.sendMassMessageLink).hasClass(Pattern.compile("(^|\\s)disabled(\\s|$)"))
        assertThat(listingPage.sendMassMessageLink).containsText("(0 varausta)")
        listingPage.selectAllReservations.click()
        listingPage.reservations
        assertThat(listingPage.sendMassMessageLink).containsText("(6 varausta)")
        listingPage.sendMassMessageLink.click()
        assertThat(listingPage.sendMassMessageForm).isVisible()
    }

    @Test
    fun `Mass email checks persist through filters`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 6 }
        listingPage.selectAllReservations.click()
        assertThat(listingPage.sendMassMessageLink).containsText("(6 varausta)")
        listingPage.reservationValidityFilter(ReservationValidity.FixedTerm.toString()).click()
        page.waitForCondition { listingPage.reservations.count() == 1 }
        assertThat(listingPage.sendMassMessageLink).containsText("(1 varausta)")
        listingPage.sendMassMessageLink.click()
        assertThat(listingPage.sendMassMessageForm).isVisible()
    }

    @Test
    fun `Send mass email link is disabled when reservation list is empty`() {
        val listingPage = reservationListPage()
        listingPage.selectAllReservations.click()
        page.waitForCondition { listingPage.reservations.count() == 6 }
        listingPage.searchInput("phoneSearch").fill("8888888888")
        listingPage.searchInput("phoneSearch").blur()
        page.waitForCondition { listingPage.reservations.count() == 0 }
        assertThat(listingPage.sendMassMessageLink).hasClass(Pattern.compile("(^|\\s)disabled(\\s|$)"))
    }

    @Test
    fun `Email is sent to filtered recipients with mass message`() {
        val expectedReservationCount = 6
        val expectedSentEmailCount = 3
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == expectedReservationCount }

        listingPage.selectAllReservations.click()
        assertThat(listingPage.sendMassMessageLink).containsText("($expectedReservationCount varausta)")
        listingPage.reservationRowCheckBox(1).click()
        assertThat(listingPage.sendMassMessageLink).containsText("(${expectedReservationCount - 1} varausta)")

        listingPage.sendMassMessageLink.click()
        assertThat(listingPage.sendMassMessageForm).isVisible()
        assertThat(listingPage.sendMassMessageModalSubtitle).containsText(
            "Varauksia ${expectedReservationCount - 1} kpl, viestin vastaanottajia $expectedSentEmailCount kpl."
        )
        val emailSubject = "Email message title"
        val emailBody = "Email message content"
        listingPage.sendMassMessageTitleInput.fill(emailSubject)
        listingPage.sendMassMessageTitleInput.blur()
        listingPage.sendMassMessageContentInput.fill(emailBody)
        listingPage.sendMassMessageContentInput.blur()

        listingPage.sendMassMessageModalSubmit.click()
        assertThat(listingPage.sendMassMessageModalSuccess).isVisible()

        messageService.sendScheduledEmails()
        assertEquals(expectedSentEmailCount, SendEmailServiceMock.emails.size)
        val email = SendEmailServiceMock.emails[0]
        assertEquals(email.subject, emailSubject)
        assertEquals(email.body, emailBody)
    }

    @Test
    fun `reservations list should shield against XSS scripts from citizen information`() {
        try {
            EmployeeHomePage(page).employeeLogin()
            val listingPage = ReservationListPage(page)

            // Inject XSS scripts to citizen information from citizen details page and return assertions
            val assertions = injectXSSToCitizenInformation(page, CitizenIds.leo)

            listingPage.navigateTo()
            // For some reason the page doesn't automatically reload the updated data
            page.reload()
            page.waitForCondition { listingPage.reservations.count() >= 5 }

            assertions()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reservations list should shield against XSS scripts from organization information`() {
        try {
            EmployeeHomePage(page).employeeLogin()
            val listingPage = ReservationListPage(page)

            // Inject XSS scripts to citizen information from citizen details page and return assertions
            val assertions = injectXSSToOrganizationInformation(page, OrganizationIds.espoonPursiseura)

            listingPage.navigateTo()
            // For some reason the page doesn't automatically reload the updated data
            page.reload()
            page.waitForCondition { listingPage.reservations.count() >= 5 }

            assertions()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "sortBy, XSS_ATTACK_SORT_BY",
        "ascending, XSS_ATTACK_ASCENDING",
        "amenity, XSS_ATTACK_AMENITY",
        "harbor, XSS_ATTACK_HARBOR",
        "payment, XSS_ATTACK_PAYMENT",
        "nameSearch, XSS_ATTACK_NAME_SEARCH",
        "phoneSearch, XSS_ATTACK_PHONE_SEARCH",
        "warningFilter, XSS_ATTACK_WARNING_FILTER",
        "exceptionsFilter, XSS_ATTACK_EXCEPTIONS_FILTER",
        "sectionFilter, XSS_ATTACK_SECTION_FILTER",
        "expiration, XSS_ATTACK_EXPIRATION",
        "boatSpaceType, XSS_ATTACK_BOAT_SPACE_TYPE",
        "validity, XSS_ATTACK_VALIDITY",
    )
    fun `reservations list should shield against XSS reflection scripts from parameters`(
        parameter: String,
        maliciousValue: String
    ) {
        try {
            EmployeeHomePage(page).employeeLogin()
            val listingPage = ReservationListPage(page)

            fun maliciousCode(value: String) = "%22%3E%3Cscript%3Ewindow.$value=true;%3C/script%3E%20"

            val params =
                mapOf(
                    parameter to maliciousCode(maliciousValue),
                )
            listingPage.navigateToWithParams(params)

            assertFalse(page.evaluate("() => window.hasOwnProperty('$maliciousValue')") as Boolean, "XSS script was executed on $parameter")
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
}
