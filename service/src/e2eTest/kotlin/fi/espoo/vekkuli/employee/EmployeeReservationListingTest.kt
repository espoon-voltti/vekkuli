package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.pages.citizen.components.IKnowCitizenIds
import fi.espoo.vekkuli.pages.citizen.components.IKnowOrganizationIds
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.pages.employee.ReservationListPage
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class EmployeeReservationListingTest : PlaywrightTest() {
    @Test
    fun `Employee can filter boat spaces`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 5 }
        listingPage.boatSpaceTypeFilter("Winter").click()
        page.waitForCondition { listingPage.reservations.count() == 1 }
    }

    @Test
    fun `Employee can filter by reserver phone number`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 5 }
        listingPage.searchInput("phoneSearch").fill("04056")
        listingPage.searchInput("phoneSearch").blur()
        page.waitForCondition { listingPage.reservations.count() == 1 }
        assertThat(listingPage.getByDataTestId("reserver-name").first()).containsText("Korhonen Leo")
    }

    @Test
    fun `Employee can filter by reservation validity`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 5 }
        listingPage.reservationValidityFilter(ReservationValidity.FixedTerm.toString()).click()
        page.waitForCondition { listingPage.reservations.count() == 1 }
        assertThat(listingPage.getByDataTestId("place").first()).containsText("B 003")
    }

    @Test
    fun `Employee can filter by reserver exceptions`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 5 }
        listingPage.exceptionsFilter.click()
        page.waitForCondition { listingPage.getByDataTestId("reserver-name").count() == 1 }
        assertThat(listingPage.getByDataTestId("reserver-name").first()).containsText("Pulkkinen Jorma")
    }

    @Test
    fun `Employee can filter by amenity`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 5 }
        listingPage.expandingSelectionFilter("amenity").click()
        listingPage.amenityFilter(BoatSpaceAmenity.Trailer.name).click()
        page.waitForCondition { listingPage.reservations.count() == 1 }
        assertThat(listingPage.getByDataTestId("place").first()).containsText("B 015")
        listingPage.amenityFilter(BoatSpaceAmenity.Beam.name).click()
        page.waitForCondition { listingPage.reservations.count() == 5 }
    }

    @Test
    fun `reservations list should shield against XSS scripts from citizen information`() {
        try {
            EmployeeHomePage(page).employeeLogin()
            val listingPage = ReservationListPage(page)

            // Inject XSS scripts to citizen information from citizen details page and return assertions
            val assertions = injectXSSToCitizenInformation(page, IKnowCitizenIds.citizenIdLeo)

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
            val assertions = injectXSSToOrganizationInformation(page, IKnowOrganizationIds.espoonPursiseura)

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
    fun `reservations list should shield against XSS reflection scripts from parameters`(parameter: String, maliciousValue: String) {
        try {
            EmployeeHomePage(page).employeeLogin()
            val listingPage = ReservationListPage(page)

            fun maliciousCode(value: String) = "%22%3E%3Cscript%3Ewindow.${value}=true;%3C/script%3E%20"

            val params = mapOf(
                parameter to maliciousCode(maliciousValue),
            )
            listingPage.navigateToWithParams(params)

            assertFalse(page.evaluate("() => window.hasOwnProperty('${maliciousValue}')") as Boolean, "XSS script was executed on $parameter")
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
